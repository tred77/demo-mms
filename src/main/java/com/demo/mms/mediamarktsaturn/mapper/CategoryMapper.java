package com.demo.mms.mediamarktsaturn.mapper;

import com.demo.mms.mediamarktsaturn.domain_data.Category;
import com.demo.mms.mediamarktsaturn.transfer_data.CategoryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A simple util class to map between {@link CategoryDTO} and {@link Category}
 */

@Slf4j
public class CategoryMapper {

    public static Category toCategory(CategoryDTO categoryDTO) {
        return new Category(
                categoryDTO.getId(),
                categoryDTO.getName(),
                categoryDTO.getParent() != null ? toCategory(categoryDTO.getParent()) : null);
    }

    public static CategoryDTO toCategoryDTO(Category category) {
        CategoryDTO.CategoryDTOBuilder categoryDTOBuilder = CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName());

        // protect from ORM closed session
        try {
            categoryDTOBuilder.parent(category.getParent() != null ? toCategoryDTO(category.getParent()) : null);
        } catch (Exception ex) {
            log.debug(ex.getMessage());
        }
        return categoryDTOBuilder.build();
    }

    public static Page<CategoryDTO> toCategoryPage(Page<Category> categoryPage, Pageable pageable) {
        List<CategoryDTO> categoryDTOS = toCategoryDTOCollection(categoryPage.getContent());
        return new PageImpl<>(categoryDTOS, pageable, categoryPage.getTotalElements());
    }


    public static Set<Category> toCategoryCollection(Collection<CategoryDTO> categoryDTOS) {
        if (categoryDTOS == null || categoryDTOS.isEmpty()) {
            return Collections.emptySet();
        }
        return categoryDTOS.stream()
                .map(CategoryMapper::toCategory)
                .collect(Collectors.toSet());
    }

    public static List<CategoryDTO> toCategoryDTOCollection(Collection<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }
        return categories.stream()
                .map(CategoryMapper::toCategoryDTO)
                .collect(Collectors.toList());
    }

}
