package com.tongxiangdb.services;

import com.tongxiangdb.entities.Asset;
import com.tongxiangdb.entities.Business;
import com.tongxiangdb.repositories.AssetRepository;
import com.tongxiangdb.repositories.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private BusinessRepository businessRepository;

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Asset getAssetById(Long id) {
        return assetRepository.findById(id).orElse(null);
    }

    public List<Asset> getAvailableAssetsByClient(Long clientId) {
        return assetRepository.findByClientIdAndBusinessIsNull(clientId);
    }

    public Asset createOrUpdateAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    public Asset updateAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    public void deleteAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid asset ID: " + id));
        assetRepository.deleteById(id);
    }

    public void deleteAssetAndRelatedBusinesses(Long id) {
        Asset asset = getAssetById(id);
        Business business = asset.getBusiness();
        businessRepository.delete(business);
        assetRepository.delete(asset);
    }
}
