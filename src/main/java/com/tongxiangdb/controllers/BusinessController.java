package com.tongxiangdb.controllers;

import com.tongxiangdb.dto.AssetDTO;
import com.tongxiangdb.entities.Asset;
import com.tongxiangdb.entities.Business;
import com.tongxiangdb.entities.Charge;
import com.tongxiangdb.entities.Client;
import com.tongxiangdb.repositories.ChargeRepository;
import com.tongxiangdb.services.AssetService;
import com.tongxiangdb.services.BusinessService;
import com.tongxiangdb.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.round;

@Controller
@RequestMapping("/businesses")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private ChargeRepository chargeRepository;

    // Display all businesses
    @GetMapping
    public String getBusinesses(Model model) {
        List<Business> businesses = businessService.getAllBusinesses();
        model.addAttribute("businesses", businesses);
        return "businesses";
    }

    // Show form to add a new business
    @GetMapping("/add")
    public String showAddBusinessForm(Model model) {
        model.addAttribute("business", new Business());
        model.addAttribute("clients", clientService.getAllClients());
        model.addAttribute("assets", assetService.getAllAssets());
        return "add-business";
    }

    // Process form to add a new business
    @PostMapping("/add")
    public String addBusiness(@ModelAttribute("business") Business business, Model model) {
        // Fetch the Client
        Long clientId = business.getClient().getId();
        Client client = clientService.getClientById(clientId);
        business.setClient(client);

        // Fetch the Asset
        Long assetId = business.getAsset().getId();
        Asset asset = assetService.getAssetById(assetId);
        business.setAsset(asset);

        // Validate that principal does not exceed asset's value
        if (business.getPrincipal() > asset.getValue()) {
            model.addAttribute("business", business);
            model.addAttribute("clients", clientService.getAllClients());
            // Fetch available assets again to repopulate the dropdown
            model.addAttribute("assets", assetService.getAvailableAssetsByClient(clientId));
            model.addAttribute("errorMessage", "Principal cannot exceed the asset's value ($" + asset.getValue().toString() + ").");
            return "add-business";
        }

        // Maintain bidirectional relationship
        asset.setBusiness(business);

        // Calculate monthlyPayment if not set
        if (business.getMonthlyPayment() == null) {
            Double monthlyPayment = calculateMonthlyPayment(
                    business.getPrincipal(),
                    business.getInterestRate(),
                    business.getTermLength()
            );
            business.setMonthlyPayment(monthlyPayment);
        }

        // Set nextPaymentDate if not set
        if (business.getNextPaymentDate() == null) {
            LocalDate nextPaymentDate = calculateNextPaymentDate(business.getPaymentDay());
            business.setNextPaymentDate(nextPaymentDate);
        }
        business.setIsCleared(false);
        businessService.createOrUpdateBusiness(business);
        return "redirect:/businesses";
    }

    private LocalDate calculateNextPaymentDate(int paymentDay) {
        LocalDate today = LocalDate.now();
        LocalDate firstOfNextMonth = today.withDayOfMonth(1).plusMonths(1);
        int lastDayOfNextMonth = firstOfNextMonth.lengthOfMonth();

        int day = Math.min(paymentDay, lastDayOfNextMonth);
        return firstOfNextMonth.withDayOfMonth(day);
    }

    private Double calculateMonthlyPayment(Double principal, Double interestRate, Integer termLength) {
        Double monthlyPayment = (principal * interestRate/100 * termLength/12 + principal) / 12;
        return round(monthlyPayment * 100.0) / 100.0;
    }

    private void generateCharges(Business business) {
        List<Charge> charges = new ArrayList<>();
        // Calculate the due date for the first charge
        LocalDate dueDate = business.getNextPaymentDate();

        // Determine the number of periods (e.g., months)
        int termLength = business.getTermLength(); // Assuming termLength is in months

        for (int i = 0; i < termLength; i++) {
            Charge charge = new Charge();
            charge.setBusiness(business);
            charge.setPaymentAmount(business.getMonthlyPayment());
            charge.setDueDate(dueDate.plusMonths(i));
            charge.setOverdueInterestRate(business.getOverdueInterestRate());
            charge.setStatus("pending");

            charges.add(charge);
        }

        // Save all charges to the database
        chargeRepository.saveAll(charges);
    }

    @GetMapping("/delete/{id}")
    public String deleteBusiness(@PathVariable Long id, Model model) {
        businessService.deleteBusiness(id);
        return "redirect:/businesses";
    }

    @GetMapping("/available-assets")
    @ResponseBody
    public ResponseEntity<List<AssetDTO>> getAvailableAssets(@RequestParam Long clientId) {
        List<Asset> availableAssets = assetService.getAvailableAssetsByClient(clientId);
        List<AssetDTO> assetDTOs = availableAssets.stream()
                .map(asset -> new AssetDTO(asset.getId(), asset.getName(), asset.getValue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(assetDTOs);
    }
}

