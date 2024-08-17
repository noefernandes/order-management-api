package com.petize.ordermanagementapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.petize.ordermanagementapi.model.purchase.Purchase;
import com.petize.ordermanagementapi.model.purchase.PurchaseResponse;
import com.petize.ordermanagementapi.model.purchase.PurchaseStatusMessage;
import com.petize.ordermanagementapi.model.purchaseItem.PurchaseItem;
import com.petize.ordermanagementapi.service.PurchaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public Purchase createPurchase(@RequestBody Purchase purchase) {
        return this.purchaseService.createPurchase(purchase);
    }

    @PutMapping
    public Purchase updatePurchase(@RequestBody Purchase purchaseRequest) {
        return this.purchaseService.updatePurchase(purchaseRequest);
    }

    @DeleteMapping("/{id}")
    public void deletePurchaseById(@PathVariable Long id) {
        this.purchaseService.deletePurchaseById(id);
    }

    @GetMapping("/{id}")
    public Purchase getPurchaseById(@PathVariable Long id) {
        return this.purchaseService.getPurchaseById(id);
    }

    @RequestMapping("/all")
    public List<PurchaseResponse> getAllPurchases() {
        return this.purchaseService.getAllPurchases();
    }

    @RequestMapping("/item/{id}")
    public PurchaseItem getPurchaseItemById(@PathVariable Long id) {
        return this.purchaseService.getPurchaseItemById(id);
    }

    @PostMapping("/change-status")
    public void changePurchaseStatusRequest(@RequestBody PurchaseStatusMessage purchaseStatusMessage)
                                                                        throws JsonProcessingException {
        this.purchaseService.sendChangePurchaseStatusRequest(purchaseStatusMessage);
    }
}
