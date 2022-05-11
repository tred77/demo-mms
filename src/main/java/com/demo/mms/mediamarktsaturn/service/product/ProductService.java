package com.demo.mms.mediamarktsaturn.service.product;

import com.demo.mms.mediamarktsaturn.domain_data.Category;
import com.demo.mms.mediamarktsaturn.domain_data.Product;
import com.demo.mms.mediamarktsaturn.exception.ConstraintsViolationException;
import com.demo.mms.mediamarktsaturn.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

public interface ProductService {

    /**
     * find a product by given Id
     *
     * @param id: product id
     * @return product
     * @throws EntityNotFoundException
     */
    Product find(Long id) throws EntityNotFoundException;

    /**
     * create a product with provided data
     *
     * @param product
     * @return persisted product
     * @throws ConstraintsViolationException
     */
    Product create(Product product) throws ConstraintsViolationException;

    /**
     * delete the product associated to given id
     *
     * @param productId
     * @throws EntityNotFoundException
     */
    void delete(Long productId) throws EntityNotFoundException;

    /**
     * update the product
     *
     * @param id
     * @param product
     * @throws EntityNotFoundException
     */
    void update(long id, Product product) throws EntityNotFoundException;

    /**
     * fetch the categories of product
     *
     * @param productId
     * @throws EntityNotFoundException
     */
    List<Category> findProductCategories(long productId) throws EntityNotFoundException;

    /**
     * add category to the product
     *
     * @param productId
     * @param categoryId
     * @throws EntityNotFoundException
     */
    void addCategoryToProduct(long productId, long categoryId) throws EntityNotFoundException;

    /**
     * delete category from the product
     *
     * @param productId
     * @param categoryId
     * @throws EntityNotFoundException
     */
    void deleteCategoryFromProduct(long productId, long categoryId) throws EntityNotFoundException;

    /**
     * search product with searchQuery
     *
     * @param name
     * @param shortDescription
     * @param pageable
     */
    Page<Product> searchProduct(String name, String shortDescription, Pageable pageable);

}
