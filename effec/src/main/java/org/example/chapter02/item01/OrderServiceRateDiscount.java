package org.example.chapter02.item01;

public class OrderServiceRateDiscount implements OrderService {

    public OrderServiceRateDiscount() {
    }

    @Override
    public String register() {
        return "비율 할인 주문 등록";
    }
}
