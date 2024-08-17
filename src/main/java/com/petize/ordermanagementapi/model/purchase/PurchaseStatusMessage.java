package com.petize.ordermanagementapi.model.purchase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseStatusMessage {
    private Long purchaseId;
    private String status;
}
