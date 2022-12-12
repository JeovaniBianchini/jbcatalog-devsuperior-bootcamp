package com.bianchini.jbcatalog.repositories;

import com.bianchini.jbcatalog.Factory;
import com.bianchini.jbcatalog.entities.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.Assert;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private Long existsId;
    private Long nonExistId;

    @BeforeEach
    void setUp() throws Exception{
        existsId = 1L;
        nonExistId = 1000L;
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull(){
        Product product = Factory.create();
        product.setId(null);

        Long expetedId = 26L;

        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(expetedId, product.getId());

    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists(){

        repository.deleteById(existsId);

        Optional<Product> result = repository.findById(existsId);

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void deleteShoulThrowEmptyResultDataAccesExceptionWhenIdDoesNotExist(){

        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            repository.deleteById(nonExistId);
        });
    }

    @Test
    public void findByIdShouldReturnProductNotEmptyWhenExistsId(){

        Optional<Product> product = repository.findById(existsId);

        Assertions.assertTrue(product.isPresent());
    }

    @Test
    public void findByIdShouldReturnEmptyProductWhenIdDoesNotExist(){

        Optional<Product> product = repository.findById(nonExistId);

        Assertions.assertTrue(product.isEmpty());
    }


}
