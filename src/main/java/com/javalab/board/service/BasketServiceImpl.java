package com.javalab.board.service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javalab.board.dto.BasketDTO;
import com.javalab.board.dto.BasketProductDTO;

import com.javalab.board.dto.MemberDTO;
import com.javalab.board.dto.UpdateBasketDTO;
import com.javalab.board.entity.Basket;
import com.javalab.board.entity.BasketProduct;
import com.javalab.board.entity.Member;
import com.javalab.board.repository.BasketProductRepository;
import com.javalab.board.repository.BasketRepository;
import com.javalab.board.repository.MemberRepository;

@Service
public class BasketServiceImpl implements BasketService{

	
	@Autowired
	private BasketRepository basketRepository;
	
	@Autowired 
	private BasketProductRepository basketProductRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Override
	public void createBasket(BasketDTO basketDTO, 
			BasketProductDTO basketProductDTO,
			Object memberId) {
		
		
		Optional<Basket> basket =  basketRepository.findBasketByMemberMemberId(memberId);
		
		if (basket.isPresent()) {
			
			BasketProduct basketProduct = BasketProduct.builder()
					.basketProductNo(basketProductDTO.getBasketProductNo())
					.basketProductAmount(basketProductDTO.getBasketProductAmount())
					.basketQty(basketProductDTO.getBasketQty())
					.product(basketProductDTO.getProduct())
					.basket(basket.get())
					.build();
			
			Basket existingBasket = basket.get();
			existingBasket.setBasketAmount(existingBasket.getBasketAmount() + basketProductDTO.getBasketProductAmount());
			
			basketRepository.save(existingBasket);
			
			basketProductRepository.save(basketProduct);
			
		} else {
			
			Member member = memberRepository.findByMemberId((String) memberId);

			Basket newBasket = Basket.builder()
					.basketNo(basketDTO.getBasketNo())
					.basketAmount(basketDTO.getBasketAmount())
					.member(member)
					.build();
			
			Basket createdBasket = basketRepository.save(newBasket);
			
			BasketProduct basketProduct = BasketProduct.builder()
					.basketProductNo(basketProductDTO.getBasketProductNo())
					.basketProductAmount(basketProductDTO.getBasketProductAmount())
					.basketQty(basketProductDTO.getBasketQty())
					.product(basketProductDTO.getProduct())
					.basket(createdBasket)
					.build();
	
			basketProductRepository.save(basketProduct);
		}
		

		
		
		
//		System.out.println("createdBasket : " + createdBasket);
//		System.out.println("member : " + member);
//		System.out.println("memberDTO memberId " + memberDTO.getMemberId());
		 
		
		


		
//		List<BasketProduct> basketProducts = basketDTO.getBasketProducts();
//		
//		System.out.println("basketProducts : " + basketProducts);
		
//        List<OrderItem> orderItems = orderMasterDTO.getOrderItems().stream()
//                .map(item -> {
//                    OrderItem orderItem = orderItemDtoToEntity(item);
//                    // 저장된 엔티티를 할당, 거기에 orderId 있음, 
//                    // 그 orderId가 orderItem테이블에 들어감
//                    orderItem.setOrderMaster(savedOrderMaster); 
//                    return orderItem;
//                }).collect(Collectors.toList());
//
//        // OrderItem들 저장
//        orderItemRepository.saveAll(orderItems);
//		
		
	}

	@Override
	public List<BasketProductDTO> getBasketProducts(Object memberId) {
//		MemberDTO memberDTO =  (MemberDTO) member;
		Optional<Basket> basket =  basketRepository.findBasketByMemberMemberId(memberId);
		
		if (basket.isPresent()) {
			List<BasketProduct> basketProducts = basketProductRepository.findByBasketBasketNo(basket.get().getBasketNo());
			
			return basketProducts.stream()
					.map(entity -> BasketProductDTO.builder()
							.basketProductNo(entity.getBasketProductNo())
							.basketQty(entity.getBasketQty())
							.basketProductAmount(entity.getBasketProductAmount())
							.basket(entity.getBasket())
							.product(entity.getProduct())
							.build()).collect(Collectors.toList());
		} else {
			return null;
		}
		
	}

	@Override
	public void updateBasket(UpdateBasketDTO updateBasketDTO) {
		// 업데이트 할 basketProduct 가져옴
		Optional<BasketProduct> basketProduct = basketProductRepository.findById(updateBasketDTO.getBasketProductNo());		
		Optional<Basket> basket = basketRepository.findById(updateBasketDTO.getBasketNo());
		
        if (basketProduct.isPresent()) {
        	
            BasketProduct existingProduct = basketProduct.get();
            Basket existingBasket = basket.get();
            
            existingProduct.setBasketQty(updateBasketDTO.getBasketQty());
            existingProduct.setBasketProductAmount(updateBasketDTO.getBasketProductAmount());
            existingBasket.setBasketAmount(updateBasketDTO.getBasketAmount());
            
            basketRepository.save(existingBasket);
            basketProductRepository.save(existingProduct);
        }
	}

	@Override
	public BasketDTO getBasket(Object memberId) {
//		MemberDTO memberDTO =  (MemberDTO) member;
		Optional<Basket> basket =  basketRepository.findBasketByMemberMemberId(memberId);
		
		BasketDTO basketDTO = BasketDTO.builder()
				.basketAmount(basket.get().getBasketAmount())
				.build();
		
		return basketDTO;
	}

	@Override
	public void deleteBasketProduct(Integer basketProductNo) {
		basketProductRepository.deleteById(basketProductNo);
	}
	
	@Override
	public Integer getCurrentTotalPrice(Integer basketNo) {
	    Integer totalPrice = 0;
	    List<BasketProduct> basketProducts = basketProductRepository.findByBasketBasketNo(basketNo);
	      
	    for (BasketProduct basketProduct : basketProducts) {
			totalPrice += basketProduct.getBasketProductAmount();
		}   
	    return totalPrice;
	}

	@Override
	public void updateTotalPrice(Integer totalPrice, Integer basketNo) {
		Optional<Basket> basket = basketRepository.findById(basketNo);
		Basket updateBasket = basket.get();
		updateBasket.setBasketAmount(totalPrice);
		basketRepository.save(updateBasket);
	}

	@Override
	public List<BasketProductDTO> getBasketProducts2(Integer basketProductNo) {
		Optional<BasketProduct> basketProducts = basketProductRepository.findById(basketProductNo);

		return basketProducts.stream()
				.map(entity -> BasketProductDTO.builder()
						.basketProductNo(entity.getBasketProductNo())
						.basketQty(entity.getBasketQty())
						.basketProductAmount(entity.getBasketProductAmount())
						.basket(entity.getBasket())
						.product(entity.getProduct())
						.build()).collect(Collectors.toList());

	}
	
	@Override
	public BasketProductDTO getBasketProduct(Integer basketProductNo) {
		Optional<BasketProduct> basketProduct = basketProductRepository.findById(basketProductNo);

		return BasketProductDTO.builder()
				.basketProductNo(basketProduct.get().getBasketProductNo())
				.basketQty(basketProduct.get().getBasketQty())
				.basketProductAmount(basketProduct.get().getBasketProductAmount())
				.basket(basketProduct.get().getBasket())
				.product(basketProduct.get().getProduct())
				.build();

	}

	
	
	
	
	
	
	
	
	
	

}
