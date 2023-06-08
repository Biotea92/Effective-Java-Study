package org.example.chapter02.item01;

import java.util.Optional;
import java.util.ServiceLoader;

public class OrderServiceFactory {

    public static void main(String[] args) {
        System.out.println(OrderService.createRateDiscount().register());
        System.out.println(OrderService.createFixDiscount().register());
    }
}
