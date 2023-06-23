package org.example.chapter04.item24;

public class NoneStaticInnerClass {
    private final String name;

    public NoneStaticInnerClass(String name) {
        this.name = name;
    }

    private class InnerClass {
        public void printName() {
            System.out.println(name);
        }
    }

    public static void main(String[] args) {
        NoneStaticInnerClass outerClass = new NoneStaticInnerClass("test");
        InnerClass innerClass = outerClass.new InnerClass();
        innerClass.printName();
    }
}
