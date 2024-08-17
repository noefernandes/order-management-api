package com.petize.ordermanagementapi.repository;

import com.petize.ordermanagementapi.model.purchaseItem.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {
}
