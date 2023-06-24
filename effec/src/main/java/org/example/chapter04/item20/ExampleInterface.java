package org.example.chapter04.item20;

public interface ExampleInterface {

    void printName();

    default void decoratePrintName() {
        System.out.println("**");
        printName();
        System.out.println("**");
    }

    public static void main(String[] args) {
        ExampleInterface example = new ExampleInterface() {
            @Override
            public void printName() {
                System.out.println("test");
            }
        };
        example.decoratePrintName();
    }
}
