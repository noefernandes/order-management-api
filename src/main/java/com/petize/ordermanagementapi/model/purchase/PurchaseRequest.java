package com.petize.ordermanagementapi.model.purchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.petize.ordermanagementapi.model.purchaseItem.PurchaseItemRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PurchaseRequest {
    private Long id;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss", timezone = "GMT-3")
    private Date date;
    private PurchaseStatus purchaseStatus;
    private List<PurchaseItemRequest> purchaseItems;
}
