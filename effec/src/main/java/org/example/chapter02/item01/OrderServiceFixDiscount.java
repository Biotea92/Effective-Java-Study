package org.example.chapter02.item01;

public class OrderServiceFixDiscount implements OrderService {

    public OrderServiceFixDiscount() {
    }

    @Override
    public String register() {
        return "고정 할인 주문 등록";
    }
}
