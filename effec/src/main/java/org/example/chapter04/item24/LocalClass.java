package org.example.chapter04.item24;

public class LocalClass {

    private final String name;

    public LocalClass(String name) {
        this.name = name;
    }

    public void printName() {
        class InnerClass {
            public void printName() {
                System.out.println(name);
            }
        }

        InnerClass innerClass = new InnerClass();
        innerClass.printName();
    }

    public static void main(String[] args) {
        LocalClass localClass = new LocalClass("test");
        localClass.printName();
    }
}
