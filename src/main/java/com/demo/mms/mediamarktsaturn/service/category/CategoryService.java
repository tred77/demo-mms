package com.demo.mms.mediamarktsaturn.service.category;

import com.demo.mms.mediamarktsaturn.domain_data.Category;
import com.demo.mms.mediamarktsaturn.domain_data.Product;
import com.demo.mms.mediamarktsaturn.exception.ConstraintsViolationException;
import com.demo.mms.mediamarktsaturn.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {


    /**
     * find a category by given Id
     *
     * @param id: category id
     * @return category
     * @throws EntityNotFoundException
     */
    Category find(Long id) throws EntityNotFoundException;

    /**
     * find a category by given Id with it's full path to the root
     *
     * @param id: category id
     * @return category
     * @throws EntityNotFoundException
     */
    Category findFullPathToRoot(Long id) throws EntityNotFoundException;

    /**
     * create a category with provided data
     *
     * @param category
     * @return persisted category
     * @throws ConstraintsViolationException
     */
    Category create(Category category) throws ConstraintsViolationException;

    /**
     * delete the category associated to given id
     *
     * @param categoryId
     * @throws EntityNotFoundException
     */
    void delete(Long categoryId) throws EntityNotFoundException, ConstraintsViolationException;

    /**
     * update the category
     *
     * @param category
     * @throws EntityNotFoundException
     */
    void update(long categoryId, Category category) throws EntityNotFoundException;

    /**
     * load the path for each category in the list until the root
     * @param categories
     * @return
     */
    List<Category> loadAncestorOfCategories(List<Category> categories);


    /**
     * search product with searchQuery
     *
     * @param name
     * @param pageable
     */
    Page<Category> searchCategory(String name, Pageable pageable);


}
