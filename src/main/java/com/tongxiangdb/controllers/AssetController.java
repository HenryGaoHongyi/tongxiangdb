package com.tongxiangdb.controllers;

import com.tongxiangdb.entities.Asset;
import com.tongxiangdb.services.AssetService;
import com.tongxiangdb.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private ClientService clientService;

    // Display all assets
    @GetMapping
    public String getAssets(Model model) {
        List<Asset> assets = assetService.getAllAssets();
        model.addAttribute("assets", assets);
        return "assets";
    }

    // Show form to add a new asset
    @GetMapping("/add")
    public String showAddAssetForm(Model model) {
        model.addAttribute("asset", new Asset());
        model.addAttribute("clients", clientService.getAllClients());
        return "add-asset";
    }

    // Process form to add a new asset
    @PostMapping("/add")
    public String addAsset(@ModelAttribute("asset") Asset asset) {
        assetService.createOrUpdateAsset(asset);
        return "redirect:/assets";
    }

    @GetMapping("/delete/{id}")
    public String deleteAsset(@PathVariable Long id, Model model) {
        Asset asset = assetService.getAssetById(id);
        if (asset.getBusiness() != null) {
            // Redirect to confirmation page
            model.addAttribute("asset", asset);
            return "confirm-delete-asset";
        } else {
            // No businesses associated, delete directly
            assetService.deleteAsset(id);
            return "redirect:/assets";
        }
    }

    @PostMapping("/delete/{id}")
    public String confirmDeleteAsset(@PathVariable Long id) {
        assetService.deleteAssetAndRelatedBusinesses(id);
        return "redirect:/assets";
    }
}

