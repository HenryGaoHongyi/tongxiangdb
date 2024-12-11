package com.tongxiangdb.controllers;
import com.tongxiangdb.entities.Charge;
import com.tongxiangdb.entities.Payment;
import com.tongxiangdb.repositories.ChargeRepository;
import com.tongxiangdb.repositories.PaymentRepository;
import com.tongxiangdb.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ChargeRepository chargeRepository;

    @GetMapping
    public String getAllPayments(Model model) {
        List<Payment> payments = paymentRepository.findAllByOrderByPaymentDatetimeDesc();
        model.addAttribute("payments", payments);
        return "payments";
    }

    @GetMapping("/add")
    public String showAddPaymentForm(Model model, @RequestParam Long clientId) {
        model.addAttribute("payment", new Payment());

        List<Charge> charges = chargeRepository.findUnpaidChargesByClient(clientId);
        model.addAttribute("charges", charges);

        Double maxPayableAmount = charges.stream()
                .mapToDouble(Charge::getRemainingBalance)
                .sum();
        model.addAttribute("maxPayableAmount", maxPayableAmount);

        // Identify the next unpaid charge (earliest due date)
        Optional<Charge> nextChargeOpt = charges.stream()
                .min(Comparator.comparing(Charge::getDueDate));

        if (nextChargeOpt.isPresent()) {
            Charge nextCharge = nextChargeOpt.get();
            model.addAttribute("nextCharge", nextCharge);
        } else {
            model.addAttribute("nextCharge", null);
        }

        model.addAttribute("clientId", clientId);
        return "add-payment";
    }

    @PostMapping("/add")
    public String addPayment(@ModelAttribute("payment") Payment payment,
                             @RequestParam Long clientId,
                             Model model) {
        // Validate payment amount
        List<Charge> charges = chargeRepository.findUnpaidChargesByClient(clientId);
        Double maxPayableAmount = charges.stream()
                .mapToDouble(Charge::getRemainingBalance)
                .sum();
        if (payment.getPaymentAmount() > maxPayableAmount) {
            model.addAttribute("payment", payment);
            model.addAttribute("charges", charges);
            model.addAttribute("maxPayableAmount", maxPayableAmount);
            model.addAttribute("clientId", clientId);

            Optional<Charge> nextChargeOpt = charges.stream()
                    .min(Comparator.comparing(Charge::getDueDate));
            if (nextChargeOpt.isPresent()) {
                Charge nextCharge = nextChargeOpt.get();
                model.addAttribute("nextCharge", nextCharge);
            } else {
                model.addAttribute("nextCharge", null);
            }

            model.addAttribute("errorMessage", "Payment amount exceeds the maximum payable amount.");
            return "add-payment";
        }

        payment.setPaymentDatetime(LocalDateTime.now());
        paymentService.processPayment(payment, clientId);
        return "redirect:/clients/" + clientId + "/charges";
    }
}
