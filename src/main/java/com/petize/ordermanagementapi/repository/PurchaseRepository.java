package com.petize.ordermanagementapi.repository;

import com.petize.ordermanagementapi.model.purchase.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
