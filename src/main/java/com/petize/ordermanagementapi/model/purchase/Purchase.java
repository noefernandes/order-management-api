package com.petize.ordermanagementapi.model.purchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.petize.ordermanagementapi.model.purchaseItem.PurchaseItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss", timezone = "GMT-3")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PurchaseStatus purchaseStatus;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PurchaseItem> purchaseItems = new ArrayList<>();
}
