package com.luis.ifpark.repositories;

import com.luis.ifpark.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
