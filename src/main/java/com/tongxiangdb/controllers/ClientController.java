package com.tongxiangdb.controllers;

import com.tongxiangdb.dto.ClientOverdueDTO;
import com.tongxiangdb.entities.Charge;
import com.tongxiangdb.entities.Client;
import com.tongxiangdb.services.ClientService;
import com.tongxiangdb.repositories.ChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ChargeRepository chargeRepository;

    // Display all clients
    @GetMapping
    public String getClients(Model model) {
        List<Client> clients = clientService.getAllClients();
        model.addAttribute("clients", clients);
        return "clients"; // Corresponds to clients.html
    }

    // Show form to add a new client
    @GetMapping("/add")
    public String showAddClientForm(Model model) {
        model.addAttribute("client", new Client());
        return "add-client"; // Corresponds to add-client.html
    }

    // Process form to add a new client
    @PostMapping("/add")
    public String addClient(@ModelAttribute("client") Client client) {
        clientService.createOrUpdateClient(client);
        return "redirect:/clients";
    }

    @GetMapping("/edit/{id}")
    public String showEditClientForm(@PathVariable("id") Long id, Model model) {
        Client client = clientService.getClientById(id);
        if (client == null) {
            // Handle client not found scenario
            model.addAttribute("errorMessage", "Client not found.");
            return "error"; // Thymeleaf error page
        }
        model.addAttribute("client", client);
        return "edit-client"; // Thymeleaf template name
    }

    @PostMapping("/edit/{id}")
    public String editClient(@PathVariable("id") Long id,
                             @ModelAttribute("client") Client client,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "edit-client"; // Return to edit form with validation errors
        }
        client.setId(id); // Ensure the ID is set
        clientService.updateClient(client);
        return "redirect:/clients"; // Redirect to the clients list
    }

    @GetMapping("/{id}/charges")
    public String getClientCharges(@PathVariable Long id, Model model) {
        List<Charge> charges = chargeRepository.findUnpaidChargesByClient(id);
        model.addAttribute("charges", charges);
        Client client = clientService.getClientById(id);
        model.addAttribute("client", client);
        return "client-charges";
    }

    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id, Model model) {
        Client client = clientService.getClientById(id);
        model.addAttribute("client", client);
        return "confirm-delete-client";
    }

    @PostMapping("/delete/{id}")
    public String confirmDeleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return "redirect:/clients";
    }

    @GetMapping("/overdue")
    public String viewOverdueClients(Model model) {
        // Fetch clients with overdue payments
        java.util.List<ClientOverdueDTO> overdueClients = clientService.getClientsWithOverduePayments();
        model.addAttribute("overdueClients", overdueClients);
        return "overdue-clients"; // Name of the Thymeleaf template (overdue-clients.html)
    }

}
