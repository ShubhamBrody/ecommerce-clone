package com.ecommerce.project.service;

import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService{
    ProductDTO addProduct(ProductDTO productDTO, Long categoryId);
    ProductResponse getAllProducts(String sortBy, String sortOrder, Integer pageSize);
    ProductResponse getProductsByCategory(String sortBy, String sortOrder, Integer pageSize, Integer pageNumber, Long categoryId);
    ProductResponse getProductsByKeyword(String sortBy, String sortOrder, Integer pageSize, Integer pageNumber, String keyword);
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
    ProductDTO deleteProduct(Long productId);
}
