package com.demo.mms.mediamarktsaturn.repository;

import com.demo.mms.mediamarktsaturn.domain_data.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Database Access Object to category table
 */

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Long>, JpaSpecificationExecutor<Category> {


    static Specification<Category> buildSpecificationBySearchQuery(String name) {
        return (Specification) (root, query, criteriaBuilder) -> {
            if (!StringUtils.isEmpty(name)) {
                return criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
            return null;
        };
    }


}
