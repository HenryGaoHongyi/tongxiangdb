package com.tongxiangdb.repositories;

import com.tongxiangdb.entities.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByClientIdAndBusinessIsNull(Long clientId);

}
