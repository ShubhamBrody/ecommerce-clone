package com.ecommerce.ecom.service;

import com.ecommerce.ecom.model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final List<Category> categories = new ArrayList<>();
    private Long nextCategoryId = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        category.setCategoryId(nextCategoryId++);
        categories.add(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categories.stream().filter(
                c -> c.getCategoryId()
                        .equals(categoryId))
                .findFirst().orElse(null);

        if (category == null) {
            return "Category with categoryId " + categoryId + " not found";
        }

        categories.remove(category);
        return "Category with categoryId " + category.getCategoryId() + " deleted successfully";
    }
}
