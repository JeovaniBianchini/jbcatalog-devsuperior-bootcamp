package com.bianchini.jbcatalog.services;

import com.bianchini.jbcatalog.dto.ProductDto;
import com.bianchini.jbcatalog.repositories.ProductRepository;
import com.bianchini.jbcatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServicesIT {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existsId;
    private Long nonExistsId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existsId = 1L;
        nonExistsId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteProductWhenIdExists() {

        service.deleteProduct(existsId);

        Assertions.assertEquals(countTotalProducts - 1, repository.count());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteProduct(nonExistsId);
        });
    }

    @Test
    public void findAllPageadShouldReturnPageWhenPage0Size10(){

        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<ProductDto> dto = service.findAllPaged(pageRequest);

        Assertions.assertFalse(dto.isEmpty());
        Assertions.assertEquals(0, dto.getNumber());
        Assertions.assertEquals(10, dto.getSize());
        Assertions.assertEquals(countTotalProducts, dto.getTotalElements());
    }

    @Test
    public void findAllPageadShouldReturnPageEmptyWhenPageDoesNotExists(){

        PageRequest pageRequest = PageRequest.of(50, 10);

        Page<ProductDto> dto = service.findAllPaged(pageRequest);

        Assertions.assertTrue(dto.isEmpty());
    }

    @Test
    public void findAllPageadShouldReturnSortPageWhenPageSortByName(){

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

        Page<ProductDto> dto = service.findAllPaged(pageRequest);

        Assertions.assertFalse(dto.isEmpty());
        Assertions.assertEquals("Macbook Pro", dto.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", dto.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", dto.getContent().get(2).getName());
        Assertions.assertEquals("PC Gamer Foo", dto.getContent().get(8).getName());
    }

}
