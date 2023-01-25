package com.bianchini.jbcatalog.resources;

import com.bianchini.jbcatalog.Factory;
import com.bianchini.jbcatalog.TokenUtil;
import com.bianchini.jbcatalog.dto.ProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existId;
    private Long nonExistsId;
    private Long countTotalProducts;
    private ProductDto productDto;
    private String name;
    private String password;

    @BeforeEach
    void setUp() throws Exception {
        existId = 1L;
        nonExistsId = 1000L;
        countTotalProducts = 25L;
        productDto = Factory.createDto();
        name = "maria@gmail.com";
        password = "123456";
    }

    @Test
    public void pageadAllShouldReturnSortedPageWhenSortByName() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/products?page=0&size=12&sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(countTotalProducts))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Macbook Pro"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("PC Gamer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test
    public void updateShouldReturnProductDtoWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, name, password);

        String jsonBody = objectMapper.writeValueAsString(productDto);

        String expetedName = productDto.getName();
        String expetedDescription = productDto.getDescription();


        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", existId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(existId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expetedName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(expetedDescription));

    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, name, password);

        String jsonBody = objectMapper.writeValueAsString(productDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", nonExistsId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
