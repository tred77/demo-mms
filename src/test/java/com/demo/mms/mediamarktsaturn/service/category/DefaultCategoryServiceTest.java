package com.demo.mms.mediamarktsaturn.service.category;

import com.demo.mms.mediamarktsaturn.domain_data.Category;
import com.demo.mms.mediamarktsaturn.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// todo: cover more cases

@SpringBootTest
class DefaultCategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    void testLoadCategory_whenAbsent_shouldThrowNotFoundException() {
        // ARRANGE
        Long categoryId = 0L; // does not exist in the data

        // ACT
        assertThrows(EntityNotFoundException.class, () -> categoryService.findFullPathToRoot(categoryId));
    }

    @Test
    void testUpdateCategory_parentChanges_shouldFullPathChangesAccordingly() throws EntityNotFoundException {
        // ARRANGE
        Long categoryId = 308L;
        Category category = categoryService.findFullPathToRoot(categoryId);
        assertEquals(308L, category.getId());
        assertEquals("DAB+ Radios", category.getName());
        assertEquals("202;303;", category.getFullParentPath());

        // ACT
        Category newParent = new Category();
        newParent.setId(202L);
        category.setName("new name");
        category.setParent(newParent);
        categoryService.update(categoryId, category);

        // ASSERT
        Category updatedCategory = categoryService.findFullPathToRoot(categoryId);
        assertNotNull(updatedCategory);
        assertEquals("new name", updatedCategory.getName());
        assertEquals("202;", updatedCategory.getFullParentPath());
    }

    @Test
    void testLoadFullPath_fullPathFiledShouldRepresentParentsToRoot() throws EntityNotFoundException {
        // ARRANGE
        Long categoryId = 784L;

        // ACT
        Category category = categoryService.findFullPathToRoot(categoryId);

        // ARRANGE
        List<Category> allParentCategoriesUntilRoot = new ArrayList<>();
        Category parent = category.getParent();
        while (parent != null) {
            allParentCategoriesUntilRoot.add(parent);
            parent = parent.getParent();
        }

        assertEquals(2L, allParentCategoriesUntilRoot.size());
        assertEquals("202;259;", allParentCategoriesUntilRoot.get(1).getId() + ";" + allParentCategoriesUntilRoot.get(0).getId() + ";");
    }
}