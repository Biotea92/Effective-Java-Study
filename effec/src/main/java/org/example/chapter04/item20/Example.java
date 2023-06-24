package org.example.chapter04.item20;

public abstract class Example {

    public void print() {
        System.out.println("test");
    }

    public abstract void printName();

    public void decoratePrintName() {
        System.out.println("**");
        printName();
        System.out.println("**");
    }

    public static void main(String[] args) {
        Example example = new Example() {
            @Override
            public void printName() {
                System.out.println("test");
            }
        };
        example.decoratePrintName();
    }
}
