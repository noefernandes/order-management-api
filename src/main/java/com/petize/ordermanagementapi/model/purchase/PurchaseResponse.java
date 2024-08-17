package com.petize.ordermanagementapi.model.purchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.petize.ordermanagementapi.model.purchaseItem.PurchaseItemResponse;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponse {
    private Long id;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss", timezone = "GMT-3")
    private Date date;
    private PurchaseStatus purchaseStatus;
    private List<PurchaseItemResponse> purchaseItems = new ArrayList<>();
}
