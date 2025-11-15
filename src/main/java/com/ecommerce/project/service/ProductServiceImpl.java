package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.DuplicateEntryFoundException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService{
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    @Value("${project.image}")
    String globalFilePath;

    @Autowired
    public ProductServiceImpl(ModelMapper modelMapper,
                              ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              FileService fileService) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileService = fileService;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Product product = modelMapper.map(productDTO, Product.class);
        Optional<Product> dbProduct = productRepository.findByProductName(product.getProductName());
        if (dbProduct.isPresent()) {
            throw new DuplicateEntryFoundException("Product",
                    "name",
                    product.getProductName(),
                    "productId",
                    dbProduct.get().getProductId());
        }
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("Category", "categoryId", categoryId));

        product.setCategory(category);
        Double specialPrice = product.getPrice() * (1 - product.getDiscount()*0.01);
        product.setSpecialPrice(specialPrice);
        product.setImage("default.png");

        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(String sortBy, String sortOrder, Integer pageSize) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        int pageNumber = 0;
        Pageable  pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageable);
        List<Product> products = productPage.getContent();

        if(products.isEmpty()) throw new APIException("Product List is Empty.");

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);

        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(String sortBy, String sortOrder, Integer pageSize, Integer pageNumber, Long categoryId) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable  pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAllByCategory_CategoryId(categoryId, pageable);
        List<Product> products = productPage.getContent();

        if(products.isEmpty()) throw new APIException("Product List is Empty.");

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);

        return productResponse;
    }

    @Override
    public ProductResponse getProductsByKeyword(String sortBy, String sortOrder, Integer pageSize, Integer pageNumber, String keyword) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable  pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAllByProductNameContainingIgnoreCase(keyword, pageable);
        List<Product> products = productPage.getContent();

        if(products.isEmpty()) throw new APIException("Product List is Empty.");

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);

        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product originalProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        originalProduct.setProductName(productDTO.getProductName());
        originalProduct.setDescription(productDTO.getDescription());
        originalProduct.setQuantity(productDTO.getQuantity());
        originalProduct.setPrice(productDTO.getPrice());
        originalProduct.setDiscount(productDTO.getDiscount());
        Double specialPrice = originalProduct.getPrice() * (1 - originalProduct.getDiscount()*0.01);
        originalProduct.setSpecialPrice(specialPrice);

        Product updatedProduct = productRepository.save(originalProduct);

        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product originalProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        String fileName = fileService.uploadImage(globalFilePath, image);
        originalProduct.setImage(fileName);

        Product updatedProduct = productRepository.save(originalProduct);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product originalProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        productRepository.delete(originalProduct);
        return modelMapper.map(originalProduct, ProductDTO.class);
    }
}
