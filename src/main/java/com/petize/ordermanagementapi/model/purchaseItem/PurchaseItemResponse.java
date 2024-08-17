package com.petize.ordermanagementapi.model.purchaseItem;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseItemResponse {
    private Long id;
    private Integer quantity;
    private String productName;
    private String description;
    private Double price;
}
