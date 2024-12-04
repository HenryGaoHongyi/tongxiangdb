package com.tongxiangdb.controllers;

import com.tongxiangdb.entities.Charge;
import com.tongxiangdb.repositories.ChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ChargeController {

    @Autowired
    private ChargeRepository chargeRepository;

    @GetMapping("/charges")
    public String getUpcomingCharges(Model model) {
        LocalDate today = LocalDate.now();
        List<Charge> upcomingCharges = chargeRepository.findUpcomingCharges(today);
        model.addAttribute("charges", upcomingCharges);
        return "charges";
    }

}
