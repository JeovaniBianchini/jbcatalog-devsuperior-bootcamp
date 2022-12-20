package com.bianchini.jbcatalog.services;

import com.bianchini.jbcatalog.dto.CategoryDto;
import com.bianchini.jbcatalog.dto.ProductDto;
import com.bianchini.jbcatalog.entities.Category;
import com.bianchini.jbcatalog.entities.Product;
import com.bianchini.jbcatalog.repositories.CategoryRepository;
import com.bianchini.jbcatalog.repositories.ProductRepository;
import com.bianchini.jbcatalog.services.exceptions.DataBaseException;
import com.bianchini.jbcatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDto> findAllPaged(Pageable pageable){
        Page<Product> list = productRepository.findAll(pageable);
        return list.map(x -> new ProductDto(x));
    }

    @Transactional(readOnly = true)
    public ProductDto findById(Long id){
        Optional<Product> productOptional = productRepository.findById(id);
        Product product = productOptional.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDto(product, product.getCategories());
    }

    @Transactional
    public ProductDto saveProduct(ProductDto productDto) {
        Product product = new Product();
        copyDtoToEntity(productDto, product);
        product = productRepository.save(product);
        return new ProductDto(product);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        try {
            Product product = productRepository.getReferenceById(id);
            copyDtoToEntity(productDto, product);
            product = productRepository.save(product);
            return new ProductDto(product);
        } catch (EntityNotFoundException e){
            throw  new ResourceNotFoundException("Id " + id + " not found ");
        }
    }

    public void deleteProduct(Long id) {
        try {
            productRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e){
            throw  new ResourceNotFoundException("Id " + id + " not found ");
        }
        catch (DataIntegrityViolationException e){
            throw new DataBaseException("Integration violation");
        }
    }

    private void copyDtoToEntity(ProductDto productDto, Product product) {

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setDate(productDto.getDate());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());

        product.getCategories().clear();
        for (CategoryDto catDto: productDto.getCategories()){
            Category category = categoryRepository.getReferenceById(catDto.getId());
            product.getCategories().add(category);
        }
    }
}
