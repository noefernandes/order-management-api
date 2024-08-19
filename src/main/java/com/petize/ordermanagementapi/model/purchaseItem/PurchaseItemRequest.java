package com.petize.ordermanagementapi.model.purchaseItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseItemRequest {
    private Long id;
    private Integer quantity;
    private Long productId;
}
