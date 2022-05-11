package com.demo.mms.mediamarktsaturn.repository;

import com.demo.mms.mediamarktsaturn.domain_data.Product;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Database Access Object to product table
 */

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query(
            value = "select p from Product p " +
                    "join fetch p.categories c " +
                    "join fetch c.parent " +
                    "where p.id = :productId"
    )
    Product findProductWithCategories(long productId);


    static Specification<Product> buildSpecificationBySearchQuery(String name, String shortDescription) {
        return (Specification) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(name)) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            if (!StringUtils.isEmpty(shortDescription)) {
                predicates.add(criteriaBuilder.like(root.get("shortDescription"), "%" + shortDescription + "%"));
            }
            if (predicates.isEmpty()) {
                return null;
            }
            if (predicates.size() == 1) {
                return predicates.get(0);
            } else {
                return criteriaBuilder.and(predicates.get(0), predicates.get(1));
            }
        };
    }

}
