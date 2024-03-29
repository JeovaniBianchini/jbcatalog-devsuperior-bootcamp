package com.bianchini.jbcatalog.resources;

import com.bianchini.jbcatalog.Factory;
import com.bianchini.jbcatalog.TokenUtil;
import com.bianchini.jbcatalog.dto.ProductDto;
import com.bianchini.jbcatalog.services.ProductService;
import com.bianchini.jbcatalog.services.exceptions.DataBaseException;
import com.bianchini.jbcatalog.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceTests {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private Long existsId;
    private Long nonExistsId;
    private Long dependentId;

    private String name;
    private String password;

    private ProductDto productDto;
    private PageImpl<ProductDto> page;

    @BeforeEach
    void setUp() throws Exception {
        existsId = 1L;
        nonExistsId = 2L;
        dependentId = 3L;

        name = "maria@gmail.com";
        password = "123456";

        productDto = Factory.createDto();
        page = new PageImpl<>(List.of(productDto));

        Mockito.when(productService.findAllPaged(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(productService.findById(existsId)).thenReturn(productDto);
        Mockito.when(productService.findById(nonExistsId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(productService.updateProduct(Mockito.eq(existsId), Mockito.any())).thenReturn(productDto);
        Mockito.when(productService.updateProduct(Mockito.eq(nonExistsId), Mockito.any())).thenThrow(ResourceNotFoundException.class);

        Mockito.doNothing().when(productService).deleteProduct(existsId);
        Mockito.doThrow(ResourceNotFoundException.class).when(productService).deleteProduct(nonExistsId);
        Mockito.doThrow(DataBaseException.class).when(productService).deleteProduct(dependentId);

        Mockito.when(productService.saveProduct(Mockito.any())).thenReturn(productDto);
    }

    @Test
    public void findAllPageadShouldReturnPage() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductDtoWhenIdExists() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", existsId).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", nonExistsId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    public void updateShouldReturnProductDtoIdExists() throws Exception{
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, name, password);

        String jsonBody = objectMapper.writeValueAsString(productDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", existsId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.name").exists())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());

    }

    @Test
    public void updateShouldReturnNotFoundIdDoesNotExists() throws Exception{
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, name, password);

        String jsonBody = objectMapper.writeValueAsString(productDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", nonExistsId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, name, password);


        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", existsId)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, name, password);

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", nonExistsId)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteShouldThrowDataBaseExceptionWhenIdDependent() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, name, password);

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", dependentId)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void saveShouldInsertProduct() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, name, password);

        String jsonBody = objectMapper.writeValueAsString(productDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
