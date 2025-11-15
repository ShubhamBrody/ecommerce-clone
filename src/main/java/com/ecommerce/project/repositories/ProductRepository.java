package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByCategory_CategoryId(Long categoryId, Pageable pageable);
    Page<Product> findAllByProductNameContainingIgnoreCase(String keyword, Pageable pageable);
    Optional<Product> findByProductName(String productName);
}
