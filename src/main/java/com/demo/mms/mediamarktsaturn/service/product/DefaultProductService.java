package com.demo.mms.mediamarktsaturn.service.product;

import com.demo.mms.mediamarktsaturn.domain_data.Category;
import com.demo.mms.mediamarktsaturn.domain_data.Product;
import com.demo.mms.mediamarktsaturn.exception.ConstraintsViolationException;
import com.demo.mms.mediamarktsaturn.exception.EntityNotFoundException;
import com.demo.mms.mediamarktsaturn.repository.ProductRepository;
import com.demo.mms.mediamarktsaturn.service.category.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The default implementation of {@link ProductService}
 */

@Service
@Slf4j
@Transactional
public class DefaultProductService implements ProductService {

    private ProductRepository productRepository;
    private CategoryService categoryService;

    @Autowired
    public DefaultProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    @Override
    public Product find(Long id) throws EntityNotFoundException {
        return fetchProduct(id);
    }

    @Override
    public Product create(Product product) throws ConstraintsViolationException {
        try {
            return productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            log.error("ConstraintsViolationException to create product {}", product, e);
            throw new ConstraintsViolationException(e.getMessage());
        }
    }

    @Override
    public void delete(Long productId) throws EntityNotFoundException {
        Product product = this.fetchProduct(productId);
        productRepository.delete(product);
    }

    @Override
    public void update(long id, Product product) throws EntityNotFoundException {
        Product storedProduct = this.fetchProduct(id);
        storedProduct.setName(product.getName());
        storedProduct.setShortDescription(product.getShortDescription());
        storedProduct.setLongDescription(product.getLongDescription());
        storedProduct.setOnlineStatus(product.getOnlineStatus());
        productRepository.save(storedProduct);
    }

    @Override
    public void addCategoryToProduct(long productId, long categoryId) throws EntityNotFoundException {
        Product storedProduct = this.fetchProduct(productId);
        Category category = categoryService.find(categoryId);
        storedProduct.getCategories().add(category);
        productRepository.save(storedProduct);
    }

    @Override
    public void deleteCategoryFromProduct(long productId, long categoryId) throws EntityNotFoundException {
        Product storedProduct = this.fetchProduct(productId);
        Set<Category> categories = storedProduct.getCategories();
        if (categories != null && !categories.isEmpty()) {
            categories.removeIf(category -> category.getId().equals(categoryId));
        }
        productRepository.save(storedProduct);
    }

    @Override
    public List<Category> findProductCategories(long productId) {
        Product product = productRepository.findProductWithCategories(productId);
        return categoryService.loadAncestorOfCategories(new ArrayList<>(product.getCategories()));
    }

    @Override
    public Page<Product> searchProduct(String name, String shortDescription, Pageable pageable) {
        Specification<Product> productSpecification = ProductRepository.buildSpecificationBySearchQuery(name, shortDescription);
        return productRepository.findAll(productSpecification, pageable);
    }

    private Product fetchProduct(Long id) throws EntityNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Product with id: %d not found!", id)));
    }
}
