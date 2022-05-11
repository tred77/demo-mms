package com.demo.mms.mediamarktsaturn.mapper;

import com.demo.mms.mediamarktsaturn.domain_data.Product;
import com.demo.mms.mediamarktsaturn.transfer_data.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple util class to map between {@link ProductDTO} and {@link Product}
 */

public class ProductMapper {

    public static Product toProduct(ProductDTO productDTO) {
        return new Product(productDTO.getId(),
                productDTO.getName(),
                productDTO.getOnlineStatus(),
                productDTO.getShortDescription(),
                productDTO.getLongDescription());
    }

    public static ProductDTO toProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .onlineStatus(product.getOnlineStatus())
                .shortDescription(product.getShortDescription())
                .longDescription(product.getLongDescription())
                .build();
    }

    public static Page<ProductDTO> toProductDTOPage(Page<Product> productPage, Pageable pageable) {
        List<ProductDTO> productDTOS = toProductDTOCollection(productPage.getContent());
        return new PageImpl<>(productDTOS, pageable, productPage.getTotalElements());
    }

    public static List<ProductDTO> toProductDTOCollection(Collection<Product> products) {
        if (products == null || products.isEmpty()) {
            return Collections.emptyList();
        }
        return products.stream()
                .map(ProductMapper::toProductDTO)
                .collect(Collectors.toList());
    }

}
