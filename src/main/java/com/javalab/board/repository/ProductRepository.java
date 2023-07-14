package com.javalab.board.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.javalab.board.entity.Product;


public interface ProductRepository extends JpaRepository<Product, Integer> {
	
	// 카테고리ID로 상품 조회
	List<Product> findProductByCategoryCategoryNo(Integer categoryId);
	
	// 상품 이름 특정 키워드에 맞는 상품 조회
    List<Product> findByProductNameContaining(String searchKeyword);




}