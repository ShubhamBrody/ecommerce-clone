package com.ecommerce.ecom.service;

import com.ecommerce.ecom.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category with categoryId " + categoryId + " not found"
                ));

        categories.remove(category);
        return "Category with categoryId "
                + category.getCategoryId()
                + " deleted successfully";
    }

    @Override
    public Category updateCategory(Long categoryId, Category category) {
        Optional<Category> optionalCategory = categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst();

        if(optionalCategory.isPresent()) {
            Category matchingCategory = optionalCategory.get();
            matchingCategory.setCategoryName(category.getCategoryName());
            return matchingCategory;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Category with categoryId " + categoryId + " not found"
            );
        }

    }
}
