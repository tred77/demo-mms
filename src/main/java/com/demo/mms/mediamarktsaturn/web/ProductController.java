package com.demo.mms.mediamarktsaturn.web;

import com.demo.mms.mediamarktsaturn.domain_data.Product;
import com.demo.mms.mediamarktsaturn.exception.ConstraintsViolationException;
import com.demo.mms.mediamarktsaturn.exception.EntityNotFoundException;
import com.demo.mms.mediamarktsaturn.mapper.CategoryMapper;
import com.demo.mms.mediamarktsaturn.mapper.ProductMapper;
import com.demo.mms.mediamarktsaturn.service.product.ProductService;
import com.demo.mms.mediamarktsaturn.transfer_data.CategoryDTO;
import com.demo.mms.mediamarktsaturn.transfer_data.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("v1/products")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ProductDTO findProduct(@PathVariable Long productId) throws EntityNotFoundException {
        return ProductMapper.toProductDTO(productService.find(productId));
    }

    @GetMapping("/{productId}/categories")
    public List<CategoryDTO> findAllCategoriesOfProduct(@PathVariable Long productId) throws EntityNotFoundException {
        return CategoryMapper.toCategoryDTOCollection(productService.findProductCategories(productId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@Valid @RequestBody ProductDTO productDTO) throws ConstraintsViolationException {
        return ProductMapper.toProductDTO(productService.create(ProductMapper.toProduct(productDTO)));
    }


    @DeleteMapping("/{productId}")
    public String deleteProduct(@PathVariable long productId) throws EntityNotFoundException {
        productService.delete(productId);
        return String.format("Product with id = %d deleted successfully!", productId);
    }

    @DeleteMapping("/{productId}/categories/{categoryId}")
    public void deleteCategoryFromProduct(@PathVariable long productId, @PathVariable long categoryId) throws EntityNotFoundException {
        productService.deleteCategoryFromProduct(productId, categoryId);
    }


    @PutMapping("/{id}")
    public String updateProduct(@PathVariable long id, @Valid @RequestBody ProductDTO productDTO) throws EntityNotFoundException {
        productService.update(id, ProductMapper.toProduct(productDTO));
        return String.format("Product with id = %d updated successfully!", id);
    }

    @PutMapping("/{productId}/categories/{categoryId}")
    public void addCategoryToProduct(@PathVariable long productId, @PathVariable long categoryId) throws EntityNotFoundException {
        productService.addCategoryToProduct(productId, categoryId);
    }


    @GetMapping("/search")
    Page<ProductDTO> searchProduct(@RequestParam(required = false) String name, @RequestParam(required = false) String shortDescription,
                                   @PageableDefault(direction = Sort.Direction.ASC, size = 20, sort = {"id"}) Pageable pageable){
        Page<Product> products = productService.searchProduct(name, shortDescription, pageable);
        return ProductMapper.toProductDTOPage(products, pageable);
    }

}
