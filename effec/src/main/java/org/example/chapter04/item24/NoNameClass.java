package org.example.chapter04.item24;

public interface NoNameClass {
    void printName();

    public static void main(String[] args) {
        NoNameClass noNameClass = () -> System.out.println("test");
        noNameClass.printName();
    }
}
