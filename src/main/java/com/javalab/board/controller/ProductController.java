package com.javalab.board.controller;

import com.javalab.board.dto.BasketDTO;
import com.javalab.board.dto.BasketProductDTO;
import com.javalab.board.dto.ProductDTO;
import com.javalab.board.entity.Category;
import com.javalab.board.entity.Product;
import com.javalab.board.service.CategoryService;
import com.javalab.board.service.ProductService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.util.List;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private CategoryService categoryService;
    
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // 상품목록 가져오기
    @GetMapping
    public String getAllProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("categories" , categories);
        model.addAttribute("products", products);
      
        return "product/allProducts";
    }
    // 상품 상세보기
    @GetMapping("/{productNo}")
    public String productDetail(@PathVariable Integer productNo, Model model, BasketDTO basketDTO, BasketProductDTO basketProductDTO) {
       ProductDTO product = productService.getProductDetail(productNo);
       model.addAttribute("product", product);
       return "product/productDetails";
    }
    
    // 상품 추가 페이지로 이동 어드민만
    @GetMapping("/add")
    public String showAddProductForm(Model model, HttpSession session) {
        boolean isAdmin = (boolean) session.getAttribute("isAdmin");
        if (isAdmin) {
            model.addAttribute("product", new Product());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/addProduct";
        } else {
            // Handle the case when the user is not an admin (e.g., show an error page, redirect to a different page)
            return "redirect:/products";
        }
    }

    // 상품추가
    @PostMapping("/add")
    public String addProduct(@ModelAttribute("product") Product product, @RequestParam("categoryId") int categoryId, 
    		List<MultipartFile> file) throws Exception {
        Category category = categoryService.getCategoryById(categoryId);
        product.setCategory(category);
        productService.saveProduct(product, file);
       
        
        return "redirect:/products";
    }
    // 상품 수정 페이지로 이동
    @GetMapping("/{productId}/edit")
    public String showEditProductForm(@PathVariable int productId, Model model) {
        Product product = productService.getProductById(productId);
        List<Category> categories = categoryService.getAllCategories(); // Retrieve the category list
        model.addAttribute("product", product);
        model.addAttribute("categories", categories); // Add the category list to the model
        return "product/editProduct";
    }
    
    // 상품추가 기능
    @PostMapping("/{productId}/edit")
    public String updateProduct(@PathVariable int productId, @ModelAttribute("product") Product updatedProduct) {
        productService.updateProduct(productId, updatedProduct);
        return "redirect:/products";
    }

    // 상품 삭제
    @PostMapping("/{productId}/delete")
    public String deleteProduct(@PathVariable int productId) {
        productService.deleteProduct(productId);
        return "redirect:/products";
    }
    
    // 상품 검색
    @GetMapping("/allProducts")
    public String productList(Model model, @RequestParam(required = false) String searchKeyword,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortField);
        Pageable pageable = (Pageable) PageRequest.of(page, size, sort);

        Page<Product> productList;

        if (searchKeyword == null || searchKeyword.isEmpty()) {
            productList = (Page<Product>) productService.getProductById(pageable);
        } else {
            productList = (Page<Product>) productService.searchProductsByKeyword(searchKeyword);
        }

        model.addAttribute("products", productList.getContent());
        model.addAttribute("categories", categoryService.getAllCategories());

        return "product/productList";
    }
}