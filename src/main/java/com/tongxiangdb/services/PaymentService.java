package com.tongxiangdb.services;

import com.tongxiangdb.entities.Business;
import com.tongxiangdb.repositories.PaymentRepository;
import com.tongxiangdb.repositories.ChargeRepository;
import com.tongxiangdb.entities.Payment;
import com.tongxiangdb.entities.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.round;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ChargeRepository chargeRepository;

    @Transactional
    public void processPayment(Payment payment, Long clientId) {
        Double totalPaymentAmount = payment.getPaymentAmount();

        // Fetch unpaid charges ordered by due date
        List<Charge> unpaidCharges = chargeRepository.findUnpaidChargesByClient(clientId);

        List<Payment> paymentsToSave = new ArrayList<>();

        for (Charge charge : unpaidCharges) {
            if (totalPaymentAmount <= 0) {
                break;
            }

            Double chargeRemaining = charge.getRemainingBalance();
            if (chargeRemaining <= 0) {
                continue; // Skip already fully paid charges
            }

            // Determine how much to allocate to this charge
            Double paymentAmountForCharge = Math.min(chargeRemaining, totalPaymentAmount);

            // Create a new Payment
            Payment individualPayment = new Payment();
            individualPayment.setPaymentAmount(paymentAmountForCharge);
            individualPayment.setPaymentDatetime(payment.getPaymentDatetime());
            individualPayment.setDescription(payment.getDescription());
            individualPayment.setCharge(charge); // Link to charge

            // Update charge's remaining balance
            charge.setRemainingBalance((double) (round((charge.getRemainingBalance() - paymentAmountForCharge)*100)/100));

            // Update charge status if fully paid
            if (charge.getRemainingBalance() <= 0.01) {
                charge.setStatus("paid");
                Business business = charge.getBusiness();
                boolean allChargesPaid = business.getCharges().stream()
                        .allMatch(c -> c.getRemainingBalance() <= 0);
                if (allChargesPaid) {
                    business.setIsCleared(true);
                }
            }

            // Save the charge with updated balance
            chargeRepository.save(charge);

            // Add to the list of payments to save
            paymentsToSave.add(individualPayment);

            // Reduce the total payment amount
            totalPaymentAmount -= paymentAmountForCharge;
        }

        // Save all individual payments
        paymentRepository.saveAll(paymentsToSave);
    }
}

