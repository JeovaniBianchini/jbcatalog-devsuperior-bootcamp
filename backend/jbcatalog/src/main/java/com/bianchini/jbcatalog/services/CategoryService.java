package com.bianchini.jbcatalog.services;

import com.bianchini.jbcatalog.dto.CategoryDto;
import com.bianchini.jbcatalog.entities.Category;
import com.bianchini.jbcatalog.repositories.CategoryRepository;
import com.bianchini.jbcatalog.services.exceptions.DataBaseException;
import com.bianchini.jbcatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> findAll(){
        List<Category> list = categoryRepository.findAll();
        return list.stream().map(x -> new CategoryDto(x)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto findById(Long id){
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        Category category = categoryOptional.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new CategoryDto(category);
    }

    @Transactional
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        category = categoryRepository.save(category);
        return new CategoryDto(category);
    }
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        try {
            Category category = categoryRepository.getReferenceById(id);
            category.setName(categoryDto.getName());
            category = categoryRepository.save(category);
            return new CategoryDto(category);
        } catch (EntityNotFoundException e){
            throw  new ResourceNotFoundException("Id " + id + " not found ");
        }
    }


    public void deleteCategory(Long id) {
        try {
            categoryRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e){
            throw  new ResourceNotFoundException("Id " + id + " not found ");
        }
        catch (DataIntegrityViolationException e){
            throw new DataBaseException("Integration violation");
        }
    }
}
