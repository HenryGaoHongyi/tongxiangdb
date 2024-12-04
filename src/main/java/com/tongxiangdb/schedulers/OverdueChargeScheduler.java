package com.tongxiangdb.schedulers;

import com.tongxiangdb.entities.Charge;
import com.tongxiangdb.repositories.ChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import static java.lang.Math.round;

@Component
public class OverdueChargeScheduler {

    @Autowired
    private ChargeRepository chargeRepository;

    /**
     * Scheduled to run daily at 4 AM server time.
     * Cron Expression: "0 0 1 * * *"
     */
    @Scheduled(cron = "0 0 4 * * *")
//    @Scheduled(cron = "* * * * * *")
    @Transactional
    public void processOverduePayments() {
        System.out.println("updating charges");
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today.minusDays(1); // Due date +1 day

        List<Charge> overdueCharges = chargeRepository.findOverdueCharges(checkDate);

        for (Charge charge : overdueCharges) {
            // Update remainingBalance
            Double newRemainingBalance = round(charge.getRemainingBalance() * (1 + charge.getOverdueInterestRate()/100) * 100.0) / 100.0;
            charge.setRemainingBalance(newRemainingBalance);

            // Update status to 'Overdue'
            charge.setStatus("Overdue");
            charge.setDueDate(charge.getDueDate().plusMonths(1));

            // Save the updated business
            chargeRepository.save(charge);

            System.out.println("Updated Charge ID: " + charge.getId() +
                    " | New Remaining Balance: $" + String.format("%.2f", newRemainingBalance) +
                    " | Status: Overdue");
        }
    }
}
