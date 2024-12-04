package com.tongxiangdb.services;

import com.tongxiangdb.entities.Asset;
import com.tongxiangdb.entities.Business;
import com.tongxiangdb.entities.Charge;
import com.tongxiangdb.entities.Payment;
import com.tongxiangdb.repositories.BusinessRepository;
import com.tongxiangdb.repositories.ChargeRepository;
import com.tongxiangdb.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class BusinessService {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ChargeRepository chargeRepository;

    public List<Business> getAllBusinesses() {
        return businessRepository.findAll();
    }

    public Business getBusinessById(Long id) {
        return businessRepository.findById(id).orElse(null);
    }

    public void deleteBusiness(Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid business ID: " + id));

        // Fetch all charges associated with the business
        List<Charge> charges = chargeRepository.findUnpaidChargesByBusiness(id);

        for (Charge charge : charges) {
            // Set charge_id in payments to NULL
            for (Payment payment : charge.getPayments()) {
                payment.setCharge(null);
                paymentRepository.save(payment); // Save the payment with charge_id set to NULL
            }
            // Delete the charge
            chargeRepository.delete(charge);
        }

        // Break the association with Asset
        Asset asset = business.getAsset();
        if (asset != null) {
            asset.setBusiness(null);
            business.setAsset(null);
            businessRepository.save(business); // Save the updated business
        }

        // Delete the business itself
        businessRepository.delete(business);
    }


    public Business createOrUpdateBusiness(Business business) {

        if (business.getNextPaymentDate() == null) {
            business.setNextPaymentDate(calculateNextPaymentDate(business.getPaymentDay()));
        }

        Business savedBusiness = businessRepository.save(business);

        generateCharges(savedBusiness);

        return savedBusiness;
    }

    private LocalDate calculateNextPaymentDate(Integer paymentDay) {
        LocalDate today = LocalDate.now();
        LocalDate firstOfNextMonth = today.withDayOfMonth(1).plusMonths(1);
        int lastDayOfNextMonth = firstOfNextMonth.lengthOfMonth();
        int day = Math.min(paymentDay, lastDayOfNextMonth);
        return firstOfNextMonth.withDayOfMonth(day);
    }

    private void generateCharges(Business business) {
        List<Charge> charges = new ArrayList<>();
        LocalDate dueDate = business.getNextPaymentDate();

        for (int i = 0; i < business.getTermLength(); i++) {
            Charge charge = new Charge();
            charge.setClient(business.getClient());
            charge.setBusiness(business);
            charge.setPaymentAmount(business.getMonthlyPayment());
            charge.setRemainingBalance(business.getMonthlyPayment());
            charge.setDueDate(dueDate.plusMonths(i));
            charge.setOverdueInterestRate(business.getOverdueInterestRate());
            charge.setStatus("pending");

            charges.add(charge);
        }

        chargeRepository.saveAll(charges);
    }

}
