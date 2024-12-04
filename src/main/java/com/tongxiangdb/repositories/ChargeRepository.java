package com.tongxiangdb.repositories;

import com.tongxiangdb.entities.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, Long> {

    @Query("SELECT c FROM Charge c WHERE c.business.id = :businessId AND c.remainingBalance > 0 ORDER BY c.dueDate ASC")
    List<Charge> findUnpaidChargesByBusiness(@Param("businessId") Long businessId);

    @Query("SELECT c FROM Charge c WHERE c.client.id = :clientId AND c.remainingBalance > 0 ORDER BY c.dueDate ASC")
    List<Charge> findUnpaidChargesByClient(@Param("clientId") Long clientId);

    @Query("SELECT c FROM Charge c WHERE c.dueDate >= :today ORDER BY c.dueDate ASC")
    List<Charge> findUpcomingCharges(@Param("today") LocalDate today);

    @Query("SELECT c FROM Charge c WHERE c.client.id = :clientId AND c.remainingBalance > 0 ORDER BY c.dueDate ASC")
    List<Charge> findNextUnpaidChargeByClient(@Param("clientId") Long clientId);

    @Query("SELECT c FROM Charge c WHERE c.dueDate <= :checkDate AND c.remainingBalance > 0")
    List<Charge> findOverdueCharges(LocalDate checkDate);

}
