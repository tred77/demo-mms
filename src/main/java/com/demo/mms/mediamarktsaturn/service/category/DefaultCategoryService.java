package com.demo.mms.mediamarktsaturn.service.category;

import com.demo.mms.mediamarktsaturn.domain_data.Category;
import com.demo.mms.mediamarktsaturn.exception.ConstraintsViolationException;
import com.demo.mms.mediamarktsaturn.exception.EntityNotFoundException;
import com.demo.mms.mediamarktsaturn.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Slf4j
@Service
@Transactional
public class DefaultCategoryService implements CategoryService {

    private CategoryRepository categoryRepository;

    @Autowired
    public DefaultCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category find(Long id) throws EntityNotFoundException {
        return this.findCategory(id);
    }

    @Override
    public Category findFullPathToRoot(Long id) throws EntityNotFoundException {
        Category category = findCategory(id);
        return this.loadAncestorOfCategories(singletonList(category)).get(0);
    }

    @Override
    public Category create(Category category) throws ConstraintsViolationException {
        try {
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            log.error("ConstraintsViolationException to create category {}", category, e);
            throw new ConstraintsViolationException(e.getMessage());
        }
    }

    @Override
    public void delete(Long categoryId) throws EntityNotFoundException {
        Category category = this.findCategory(categoryId);
        categoryRepository.delete(category);
    }

    @Override
    public void update(long categoryId, Category category) throws EntityNotFoundException {
        Category storedCategory = this.findCategory(categoryId);
        storedCategory.setName(category.getName());
        if (category.getParent() != null && category.getParent().getId() != null
                && !category.getParent().getId().equals(storedCategory.getParent().getId())) {
            // parent of the category is changed
            Category newParent = findCategory(category.getParent().getId());
            StringBuilder newFullPath = new StringBuilder();
            if (newParent.getFullParentPath() != null) {
                newFullPath.append(newParent.getFullParentPath());
            }
            newFullPath.append(newParent.getId() + ";");
            storedCategory.setParent(newParent);
            storedCategory.setFullParentPath(newFullPath.toString());
        }
        categoryRepository.save(storedCategory);
    }

    @Override
    public List<Category> loadAncestorOfCategories(List<Category> categories) {
        List<Long> involvedCategories = categories.stream()
                .filter(category -> category.getFullParentPath() != null)
                .flatMap(category -> Arrays.stream(category.getFullParentPath().split(";"))
                        .map(Long::valueOf)
                ).distinct()
                .collect(Collectors.toList());

        // this loads all involved categories with one query
        Iterable<Category> allInvolvedParents = categoryRepository.findAllById(involvedCategories);
        Map<Long, Category> loadedParentsMap = new HashMap<>();
        for (Category loadedCategory : allInvolvedParents) {
            loadedParentsMap.put(loadedCategory.getId(), loadedCategory);
        }
        categories.stream().forEach(category -> setCategoryParent(category, loadedParentsMap));
        return categories;
    }

    @Override
    public Page<Category> searchCategory(String name, Pageable pageable) {
        Specification<Category> productSpecification = CategoryRepository.buildSpecificationBySearchQuery(name);
        return categoryRepository.findAll(productSpecification, pageable);
    }

    /**
     * recursive way of setting parent for the categories
     *
     * @param category
     * @param parents
     */
    private void setCategoryParent(Category category, Map<Long, Category> parents) {
        if (category.getParent() == null) {
            return;
        } else {
            category.setParent(parents.get(category.getParent().getId()));
            setCategoryParent(category.getParent(), parents);
        }
    }

    private Category findCategory(Long id) throws EntityNotFoundException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id: %d not found!", id)));
    }

}
