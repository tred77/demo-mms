package com.demo.mms.mediamarktsaturn.domain_data;

import com.demo.mms.mediamarktsaturn.domain_value.OnlineStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 100)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_category",
            joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id")}
    )
    private Set<Category> categories;

    @Enumerated(EnumType.STRING)
    private OnlineStatus onlineStatus;

    @Column(length = 225)
    private String shortDescription;

    // todo: this field is loaded all the time but rarely needed, could be moved to another table and fetch it when needed
    private String longDescription;

    public Product(Long id, String name, OnlineStatus onlineStatus, String shortDescription, String longDescription) {
        this.id = id;
        this.name = name;
        this.onlineStatus = onlineStatus;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
    }
}
