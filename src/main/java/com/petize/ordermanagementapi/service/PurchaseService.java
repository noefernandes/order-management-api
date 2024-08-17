package com.petize.ordermanagementapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petize.ordermanagementapi.exception.PurchaseItemNotFoundException;
import com.petize.ordermanagementapi.exception.PurchaseNotFound;
import com.petize.ordermanagementapi.model.purchase.Purchase;
import com.petize.ordermanagementapi.model.purchase.PurchaseResponse;
import com.petize.ordermanagementapi.model.purchase.PurchaseStatus;
import com.petize.ordermanagementapi.model.purchase.PurchaseStatusMessage;
import com.petize.ordermanagementapi.model.purchaseItem.PurchaseItem;
import com.petize.ordermanagementapi.model.purchaseItem.PurchaseItemResponse;
import com.petize.ordermanagementapi.repository.PurchaseItemRepository;
import com.petize.ordermanagementapi.repository.PurchaseRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    public PurchaseService(PurchaseRepository purchaseRepository, PurchaseItemRepository purchaseItemRepository,
                           ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseItemRepository = purchaseItemRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Purchase createPurchase(Purchase purchase) {
        purchase.setPurchaseStatus(PurchaseStatus.PENDING);
        return this.purchaseRepository.save(purchase);
    }

    public void deletePurchaseById(Long id) {
        this.purchaseRepository.findById(id).orElseThrow(
                () -> new PurchaseNotFound("Purchase not found with id: " + id)
        );
        this.purchaseRepository.deleteById(id);
    }

    public List<PurchaseResponse> getAllPurchases() {
        List<Purchase> purchases = this.purchaseRepository.findAll();

        List<PurchaseResponse> purchaseResponseList = new ArrayList<>();

        for (Purchase purchase : purchases) {
            PurchaseResponse purchaseResponse = new PurchaseResponse();
            purchaseResponse.setId(purchase.getId());
            purchaseResponse.setDate(purchase.getDate());
            purchaseResponse.setPurchaseStatus(purchase.getPurchaseStatus());

            for (PurchaseItem item : purchase.getPurchaseItems()) {
                PurchaseItemResponse purchaseItemResponse = new PurchaseItemResponse();

                purchaseItemResponse.setId(item.getId());
                purchaseItemResponse.setQuantity(item.getQuantity());
                purchaseItemResponse.setProductName(item.getProduct().getName());
                purchaseItemResponse.setDescription(item.getProduct().getDescription());
                purchaseItemResponse.setPrice(item.getProduct().getPrice());

                purchaseResponse.getPurchaseItems().add(purchaseItemResponse);
            }

            purchaseResponseList.add(purchaseResponse);
        }

        return purchaseResponseList;
    }

    public Purchase getPurchaseById(Long id) {
        return this.purchaseRepository.findById(id).orElseThrow(
                () -> new PurchaseNotFound("Purchase not found with id: " + id)
        );
    }

    public Purchase updatePurchase(Purchase purchase) {
        Purchase newPurchase = this.getPurchaseById(purchase.getId());

        if(newPurchase.getDate() != null) {
            newPurchase.setDate(purchase.getDate());
        }

        if(newPurchase.getPurchaseStatus() != null) {
            newPurchase.setPurchaseStatus(purchase.getPurchaseStatus());
        }

        return this.purchaseRepository.save(purchase);
    }

    public PurchaseItem getPurchaseItemById(Long id) {
        return this.purchaseItemRepository.findById(id).orElseThrow(
                () -> new PurchaseItemNotFoundException("Purchase item not found with id: " + id)
        );
    }

    public void sendChangePurchaseStatusRequest(PurchaseStatusMessage purchaseStatusMessage) throws JsonProcessingException {
        String requestChangeStatus = objectMapper.writeValueAsString(purchaseStatusMessage);
        this.rabbitTemplate.convertAndSend("purchase_queue", requestChangeStatus);
    }

    @RabbitListener(queues = "purchase_queue")
    public void changePurchaseStatus(String purchaseStatusMessage) throws JsonProcessingException {
        PurchaseStatusMessage message = objectMapper.readValue(purchaseStatusMessage, PurchaseStatusMessage.class);
        Purchase purchase;
        try {
            purchase = this.getPurchaseById(message.getPurchaseId());
        } catch (PurchaseNotFound e) {
            return;
        }

        if (message.getStatus() == null) {
            return;
        }

        if(!message.getStatus().equals(PurchaseStatus.PENDING.toString()) &&
            !message.getStatus().equals(PurchaseStatus.PROCESSING.toString()) &&
            !message.getStatus().equals(PurchaseStatus.COMPLETED.toString())
        ) {
            return;
        }

        purchase.setPurchaseStatus(PurchaseStatus.valueOf(message.getStatus()));
        this.purchaseRepository.save(purchase);
    }
}
