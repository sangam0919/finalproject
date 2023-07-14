package com.javalab.board.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javalab.board.dto.BasketDTO;
import com.javalab.board.dto.BasketProductDTO;
import com.javalab.board.dto.OrderDTO;
import com.javalab.board.dto.OrderProductDTO;
import com.javalab.board.dto.UpdateBasketDTO;
import com.javalab.board.entity.BasketProduct;
import com.javalab.board.entity.Member;
import com.javalab.board.entity.Product;
import com.javalab.board.service.BasketService;
import com.javalab.board.service.MemberService;

@Controller
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private BasketService basketService;
	
	@Autowired
	private MemberService memberService;
	
    @GetMapping("/cart")
    public String cartView(HttpSession session, Model model, BasketProductDTO basketProductDTO) {
    	Object member = session.getAttribute("loggedInUser");
    	List<BasketProductDTO> basketProducts = basketService.getBasketProducts(member);
    	if (basketProducts != null) {
    		
    		BasketDTO basket = basketService.getBasket(member);

        	model.addAttribute("basketProducts" , basketProducts);
        	model.addAttribute("basket", basket);
    	} else {
    		model.addAttribute("msg", "장바구니가 비어있습니다");
    	}
    	
    	return "account/cart";
    }
	
	
	@PostMapping("/create")
	public String basketCreate(BasketDTO basketDTO, BasketProductDTO basketProductDTO, HttpSession session) {
		Object memberId = session.getAttribute("loggedInUser");
		basketService.createBasket(basketDTO, basketProductDTO, memberId);
		return "redirect:/cart/cart";
	}
	
	@PostMapping("/order")
	public String productOrder(BasketDTO basketDTO, BasketProductDTO basketProductDTO, Model model, HttpSession session) {
		Object memberId = session.getAttribute("loggedInUser");
		Member member = memberService.getMemberById(memberId.toString());
		
		model.addAttribute("basketDTO" ,basketDTO);
		model.addAttribute("basketProductDTO" ,basketProductDTO);
		model.addAttribute("member", member);
		
		return "account/order";
	}
	
	@PostMapping("/update")
	// json으로 받아서 자동 바인딩됨.
	public String updateCart(@RequestBody UpdateBasketDTO updateBasketDTO) {
		basketService.updateBasket(updateBasketDTO);
		return "redirect:/user/cart";
	}
	
	@GetMapping("/delete/{basketNo}/{basketProductNo}")
	public String deleteBasketProduct(@PathVariable Integer basketProductNo, @PathVariable Integer basketNo) {
		basketService.deleteBasketProduct(basketProductNo);
		
	    // Basket 엔티티의 총 가격에서 삭제한 상품의 가격을 차감합니다.
	    Integer totalPrice = basketService.getCurrentTotalPrice(basketNo);
//	    Integer updatedTotalPrice = currentTotalPrice - deletedProductPrice;
	    basketService.updateTotalPrice(totalPrice, basketNo);
//	    basketService.updateTotalPrice(updatedTotalPrice);
	    
	    
		return "redirect:/cart/cart";
	}
	@PostMapping("/buy") 
	public String buy(BasketDTO basketDTO, Model model, HttpSession session) {
		Object memberId = session.getAttribute("loggedInUser");
		Member member = memberService.getMemberById(memberId.toString());
		
		
		List<BasketProductDTO> allBasketProducts = new ArrayList<>();
		for (BasketProduct d : basketDTO.getBasketProducts()) {

			
			BasketProductDTO basketProducts = basketService.getBasketProduct(d.getBasketProductNo());
			allBasketProducts.add(basketProducts);
			
		}
		model.addAttribute("basketProducts", allBasketProducts);
		model.addAttribute("member", member);
		return "account/orders";
	}
	

}
