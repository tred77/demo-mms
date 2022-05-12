package com.demo.mms.mediamarktsaturn.web;

import com.demo.mms.mediamarktsaturn.domain_data.Category;
import com.demo.mms.mediamarktsaturn.exception.ConstraintsViolationException;
import com.demo.mms.mediamarktsaturn.exception.EntityNotFoundException;
import com.demo.mms.mediamarktsaturn.mapper.CategoryMapper;
import com.demo.mms.mediamarktsaturn.service.category.CategoryService;
import com.demo.mms.mediamarktsaturn.transfer_data.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("v1/categories")
public class CategoryController {

    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{categoryId}")
    public CategoryDTO findCategory(@PathVariable Long categoryId) throws EntityNotFoundException {
        return CategoryMapper.toCategoryDTO(categoryService.findFullPathToRoot(categoryId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO createCategory(@Valid @RequestBody CategoryDTO categoryDTO) throws ConstraintsViolationException {
        return CategoryMapper.toCategoryDTO(categoryService.create(CategoryMapper.toCategory(categoryDTO)));
    }


    @DeleteMapping("/{categoryId}")
    public void deleteCategory(@PathVariable long categoryId) throws EntityNotFoundException, ConstraintsViolationException {
        try {
            categoryService.delete(categoryId);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintsViolationException("Category with Id = " + categoryId + " can't be deleted because of Data Integrity Violation!");
        }
    }


    @PutMapping("/{id}")
    public void updateCategory(@PathVariable long id, @Valid @RequestBody CategoryDTO categoryDTO) throws EntityNotFoundException {
        categoryService.update(id, CategoryMapper.toCategory(categoryDTO));
    }

    @GetMapping("/search")
    Page<CategoryDTO> searchProduct(@RequestParam(required = false) String name,
                                   @PageableDefault(direction = Sort.Direction.ASC, size = 20, sort = {"id"}) Pageable pageable){
        Page<Category> products = categoryService.searchCategory(name, pageable);
        return CategoryMapper.toCategoryPage(products, pageable);
    }

}
