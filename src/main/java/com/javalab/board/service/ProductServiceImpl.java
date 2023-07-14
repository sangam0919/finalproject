package com.javalab.board.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.javalab.board.dto.ProductDTO;
import com.javalab.board.entity.Product;
import com.javalab.board.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.awt.print.Pageable;
import java.io.File;
import java.io.IOException;

@Service
public class ProductServiceImpl implements ProductService {
   private final ProductRepository productRepository;
   


   public ProductServiceImpl(ProductRepository productRepository) {
      this.productRepository = productRepository;
   }
   
   @Override
   public List<Product> searchProductsByKeyword(String searchKeyword) {
       return productRepository.findByProductNameContaining(searchKeyword);
   }

   
   // 상품리스트
   @Override
   public List<Product> getAllProducts() {
      return productRepository.findAll();
   }
   
   
   // 상품확인
   @Override
   public Product getProductById(int productId) {
      return productRepository.findById(productId).orElse(null);
   }
   
   @Override
   public ProductDTO getProductDetail(Integer productNo) {
      Optional<Product> entity = productRepository.findById(productNo);
      return entity.isPresent() ? entityToDto(entity.get()) : null;
   }
   
   
   // 상품저장
      @Override
      public Product saveProduct(Product product, List<MultipartFile> files) throws Exception {
          String projectPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads";

          try {
              for (int i = 0; i < files.size(); i++) {
                  MultipartFile file = files.get(i);
                  UUID uuid = UUID.randomUUID();
                  String fileName = uuid + "_" + file.getOriginalFilename();
                  File saveFile = new File(projectPath, fileName);
                  file.transferTo(saveFile);

                  if (i == 0) {
                      product.setProductImage1(fileName);
                  } else if (i == 1) {
                      product.setProductImage2(fileName);
                  } else if (i == 2) {
                      product.setProductImage3(fileName);
                  }
              }

              return productRepository.save(product);
          } catch (IOException e) {
              // File saving exception handling
              throw new Exception("Failed to save image file.", e);
          }
      }

   
   // 상품 삭제
   @Override
   public void deleteProduct(int productId) {
      productRepository.deleteById(productId);
   }
   
   
   
   // 상품 업데이트
    @Override
    public void updateProduct(int productId, Product product) {
           Product existingProduct = getProductById(productId);
           // Update the existing product with the new details
           existingProduct.setProductName(product.getProductName());
           existingProduct.setProductPrice(product.getProductPrice());
           existingProduct.setProductDescription(product.getProductDescription());
           // Save the updated product
           productRepository.save(existingProduct);
       }
   
    
    
    public static ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .productNo(product.getProductNo())
                .productName(product.getProductName())
                .productDescription(product.getProductDescription())
                .productPrice(product.getProductPrice())
                .productImage1(product.getProductImage1())
                .productImage2(product.getProductImage2())
                .productImage3(product.getProductImage3())
                .category(product.getCategory())
                .basketProducts(product.getBasketProducts())
                .orderProducts(product.getOrderProducts())
                .build();
    }
    
    public static Product toEntity(ProductDTO productDTO) {
        return Product.builder()
                .productNo(productDTO.getProductNo())
                .productName(productDTO.getProductName())
                .productDescription(productDTO.getProductDescription())
                .productPrice(productDTO.getProductPrice())
                .productImage1(productDTO.getProductImage1())
                .productImage2(productDTO.getProductImage2())
                .productImage3(productDTO.getProductImage3())
                .category(productDTO.getCategory())
                .basketProducts(productDTO.getBasketProducts())
                .orderProducts(productDTO.getOrderProducts())
                .build();
    }
    
    // 모든 품목 조회
    @Override
	public List<Product> mainAllProducts() {
		return productRepository.findAll();
	}

    // 카테고리 품목별 조회
	@Override
	public List<Product> getAllProductsByCategory(Integer categoryId) {
		List<Product> products = productRepository.findProductByCategoryCategoryNo(categoryId);
		return products;
	}

	@Override
	public Product getProductById(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}