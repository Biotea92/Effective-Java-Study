package org.example.chapter02.item01;

public interface OrderService {

    String register();

    // 불가능
/*
    private static final String serviceName;

    private static class OrderServiceImpl implements OrderService {

    }
*/

    static OrderService createRateDiscount() {
        return new OrderServiceRateDiscount();
    }

    static OrderService createFixDiscount() {
        return new OrderServiceFixDiscount();
    }
}
