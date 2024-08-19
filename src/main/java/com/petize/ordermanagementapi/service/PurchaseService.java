package com.petize.ordermanagementapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petize.ordermanagementapi.exception.PurchaseItemNotFoundException;
import com.petize.ordermanagementapi.exception.PurchaseNotFound;
import com.petize.ordermanagementapi.model.product.Product;
import com.petize.ordermanagementapi.model.purchase.*;
import com.petize.ordermanagementapi.model.purchaseItem.PurchaseItem;
import com.petize.ordermanagementapi.model.purchaseItem.PurchaseItemRequest;
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
    private final ProductService productService;

    public PurchaseService(PurchaseRepository purchaseRepository, PurchaseItemRepository purchaseItemRepository,
                           ObjectMapper objectMapper, RabbitTemplate rabbitTemplate, ProductService productService) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseItemRepository = purchaseItemRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.productService = productService;
    }

    public PurchaseResponse createPurchase(PurchaseRequest purchaseRequest) {
        Purchase purchase = new Purchase();
        purchase.setDate(purchaseRequest.getDate());
        purchase.setPurchaseStatus(PurchaseStatus.PENDING);

        List<PurchaseItem> purchaseItems = new ArrayList<>();

        for(PurchaseItemRequest p: purchaseRequest.getPurchaseItems()) {
            PurchaseItem item = new PurchaseItem();

            Product product = this.productService.getProductById(p.getProductId());
            item.setProduct(product);
            item.setQuantity(p.getQuantity());

            item.setProduct(product);

            purchaseItems.add(item);
        }

        purchase.setPurchaseItems(purchaseItems);
        Purchase savedPurchase = this.purchaseRepository.save(purchase);

        return mountingPurchaseResponse(savedPurchase);
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
            PurchaseResponse purchaseResponse = mountingPurchaseResponse(purchase);
            purchaseResponseList.add(purchaseResponse);
        }

        return purchaseResponseList;
    }

    public PurchaseResponse getPurchaseById(Long id) {
        Purchase purchase = this.purchaseRepository.findById(id).orElseThrow(
                () -> new PurchaseNotFound("Purchase not found with id: " + id)
        );

        return mountingPurchaseResponse(purchase);
    }

    public PurchaseResponse updatePurchase(PurchaseRequest purchaseRequest) {
        Purchase newPurchase = this.purchaseRepository.findById(purchaseRequest.getId()).orElseThrow(
                () -> new PurchaseNotFound("Purchase not found with id: " + purchaseRequest.getId())
        );

        newPurchase.setDate(purchaseRequest.getDate());
        newPurchase.setPurchaseStatus(purchaseRequest.getPurchaseStatus());

        newPurchase.getPurchaseItems().clear();

        for(PurchaseItemRequest p: purchaseRequest.getPurchaseItems()) {
            PurchaseItem item = new PurchaseItem();

            Product product = this.productService.getProductById(p.getProductId());
            item.setProduct(product);
            item.setQuantity(p.getQuantity());

            item.setProduct(product);

            newPurchase.getPurchaseItems().add(item);
        }

        newPurchase.setPurchaseItems(newPurchase.getPurchaseItems());
        Purchase savedPurchase = this.purchaseRepository.save(newPurchase);

        return mountingPurchaseResponse(savedPurchase);
    }

    public PurchaseItemResponse getPurchaseItemById(Long id) {
        PurchaseItem item = this.purchaseItemRepository.findById(id).orElseThrow(
                () -> new PurchaseItemNotFoundException("Purchase item not found with id: " + id)
        );

        return new PurchaseItemResponse(item.getId(), item.getQuantity(), item.getProduct().getName(),
                item.getProduct().getDescription(), item.getProduct().getPrice());
    }

    public void sendChangePurchaseStatusRequest(PurchaseStatusMessage purchaseStatusMessage) throws JsonProcessingException {
        String requestChangeStatus = objectMapper.writeValueAsString(purchaseStatusMessage);
        this.rabbitTemplate.convertAndSend("purchase_queue", requestChangeStatus);
    }

    // PRECISA VERIFICAR AS EXCEÇÕES
    @RabbitListener(queues = "purchase_queue")
    public void changePurchaseStatus(String purchaseStatusMessage) throws JsonProcessingException {
        PurchaseStatusMessage message = objectMapper.readValue(purchaseStatusMessage, PurchaseStatusMessage.class);

        Purchase purchase = this.purchaseRepository.findById(message.getPurchaseId()).orElse(null);

        if (purchase == null || message.getStatus() == null) {
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

    private List<PurchaseItemResponse> formatPurchaseItem(List<PurchaseItem> purchaseItems) {
        List<PurchaseItemResponse> purchaseItemResponses = new ArrayList<>();
        for (PurchaseItem purchaseItem : purchaseItems) {
            PurchaseItemResponse purchaseItemResponse = new PurchaseItemResponse();
            purchaseItemResponse.setId(purchaseItem.getId());
            purchaseItemResponse.setQuantity(purchaseItem.getQuantity());
            purchaseItemResponse.setProductName(purchaseItem.getProduct().getName());
            purchaseItemResponse.setDescription(purchaseItem.getProduct().getDescription());
            purchaseItemResponse.setPrice(purchaseItem.getProduct().getPrice());
            purchaseItemResponses.add(purchaseItemResponse);
        }
        return purchaseItemResponses;
    }

    private PurchaseResponse mountingPurchaseResponse(Purchase purchase) {
        PurchaseResponse purchaseResponse = new PurchaseResponse();
        purchaseResponse.setId(purchase.getId());
        purchaseResponse.setDate(purchase.getDate());
        purchaseResponse.setPurchaseStatus(purchase.getPurchaseStatus());

        List<PurchaseItemResponse> purchaseItemResponses = formatPurchaseItem(purchase.getPurchaseItems());
        purchaseResponse.setPurchaseItems(purchaseItemResponses);

        return purchaseResponse;
    }
}
