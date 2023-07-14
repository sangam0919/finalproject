package com.javalab.board.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javalab.board.dto.OrderDTO;
import com.javalab.board.dto.OrderProductDTO;
import com.javalab.board.dto.ProductDTO;
import com.javalab.board.entity.Order;
import com.javalab.board.entity.OrderProduct;
import com.javalab.board.entity.Product;
import com.javalab.board.entity.Member;
import com.javalab.board.repository.MemberRepository;
import com.javalab.board.repository.OrderProductRepository;
import com.javalab.board.repository.OrderRepository;
import com.javalab.board.repository.ProductRepository;

@Service
public class OrderServiceImpl implements OrderService{

	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderProductRepository orderProductRepository;

	@Override
	public void createOrder(OrderDTO orderDTO, OrderProductDTO orderProductDTO, String userId) {
		
		
		
		Optional<Member> member = memberRepository.findById(userId);
		
		Optional<Product> product = productRepository.findById(orderProductDTO.getProduct().getProductNo());
		
//		List<OrderProduct> orderProduct = orderProductDTO.stream()
//				.map(dto -> OrderProduct.builder()
//						.orderProductNo(dto.getOrderProductNo())
//						.build())
//				.collect(Collectors.toList());
		
			
		if (member.isPresent()) {
			Order order = Order.builder()
					.orderReceiverName(orderDTO.getOrderReceiverName())
					.orderReceiverAddress(orderDTO.getOrderReceiverAddress())
					.orderPaymentAmount(orderDTO.getOrderPaymentAmount())
					.orderReceiverPhone(orderDTO.getOrderReceiverPhone())
//					.orderDate(orderDTO.getOrderDate()) @Builder.Default 를 사용해서 안해줘도 됨(기본값이라)
					.member(member.get())
					.build();
			
			Order createdOrder = orderRepository.save(order);
			
			
			OrderProduct orderProduct = OrderProduct.builder()
					.orderProductAmount(orderProductDTO.getOrderProductAmount())
					.orderQty(orderProductDTO.getOrderQty())
					.order(createdOrder)
					.product(orderProductDTO.getProduct())
					.build();
			
			orderProductRepository.save(orderProduct);
			
		}
		
		
	}

	@Override
	@Transactional
	public void createOrders(OrderDTO orderDTO, String userId) {
		
		Member member = memberRepository.findByMemberId(userId);
	
		Order order = Order.builder()
				.orderReceiverName(orderDTO.getOrderReceiverName())
				.orderReceiverAddress(orderDTO.getOrderReceiverAddress())
				.orderReceiverPhone(orderDTO.getOrderReceiverPhone())
				.orderPaymentAmount(orderDTO.getOrderPaymentAmount())
				.orderDate(orderDTO.getOrderDate())
				.member(member)
				.build();
		
		Order savedOrder =  orderRepository.save(order);
		
		List<OrderProduct> orderProducts = orderDTO.getOrderProducts().stream()
				.map(product -> {
					OrderProduct orderProduct = dtoToEntity(product);
					orderProduct.setOrder(savedOrder);
					return orderProduct;
				}).collect(Collectors.toList());
		
		orderProductRepository.saveAll(orderProducts);
		

		
	}
	




}
