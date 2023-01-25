package com.bianchini.jbcatalog.repositories;

import com.bianchini.jbcatalog.entities.Category;
import com.bianchini.jbcatalog.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /*
    Método que busca todos os produtos podendo ou não especificar a categoria.
    Consulta feita em JPQL
    SELECT =====> Selecionar valores.
    DISTINCT =====> Restringe para os valores não virem repetidos.
    FROM =====> Origem da tabela.
    INNER JOIN =====> Unir o que tem em comum entre as tableas.
    Product p =====> Nome da classe em java usando o "p" como apelido para ela.
    p.categorias cs =====> Através da tabela Product pode acessar as categorias, foi apelidado como "cs" .
    WHERE =====> Condição onde determina limites para a buscar.
    (:category IS NULL OR :category IN cs) =====> O category é a variavel que vem dos parametros do método. "IS NULL OR" é uma condição para uma coisa ou outra e "IN" define que pode estar dentro das lista de categorias.

     */
    @Query("SELECT DISTINCT p FROM Product p INNER JOIN p.categories cs WHERE (:category IS NULL OR :category IN cs)")
    Page<Product> findProducts(Category category, Pageable pageable);
}
