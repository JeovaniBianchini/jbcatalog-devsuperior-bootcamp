package com.bianchini.jbcatalog.repositories;

import com.bianchini.jbcatalog.entities.Category;
import com.bianchini.jbcatalog.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
