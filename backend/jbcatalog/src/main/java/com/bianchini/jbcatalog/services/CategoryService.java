package com.bianchini.jbcatalog.services;

import com.bianchini.jbcatalog.dto.CategoryDto;
import com.bianchini.jbcatalog.entities.Category;
import com.bianchini.jbcatalog.repositories.CategoryRepository;
import com.bianchini.jbcatalog.services.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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
        Category category = categoryOptional.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        return new CategoryDto(category);
    }
}
