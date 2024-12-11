package com.tongxiangdb.repositories;

import com.tongxiangdb.dto.ClientOverdueDTO;
import com.tongxiangdb.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("SELECT new com.tongxiangdb.dto.ClientOverdueDTO(c.name, c.phone, c.email, SUM(ch.remainingBalance)) " +
            "FROM Client c JOIN c.charges ch " +
            "WHERE ch.status = 'Overdue' " +
            "GROUP BY c.id, c.name, c.phone, c.email")
    List<ClientOverdueDTO> findClientsWithOverduePayments();
}
