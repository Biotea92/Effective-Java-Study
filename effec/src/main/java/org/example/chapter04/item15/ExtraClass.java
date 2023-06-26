package org.example.chapter04.item15;

public class ExtraClass {

    public static void main(String[] args) {
        String hello = InnerClass.hello;
    }

    // ExtraClass에서만 사용가능
    private static class InnerClass {
        private static final String hello = "hello";
    }
}
