package org.example.chapter04.item24;

public class OuterClass {
    private final String name;

    public OuterClass(String name) {
        this.name = name;
    }

    static class InnerClass {
        public void printName(OuterClass outerClass) {
            System.out.println(outerClass.name);
        }
    }

    public static void main(String[] args) {
        OuterClass outerClass = new OuterClass("test");
        InnerClass innerClass = new InnerClass();
        innerClass.printName(outerClass);
    }

    // outclass 도우미 역할을 하는 innerclass 예시
    // static class를 사용하면 outclass의 인스턴스에 대한 참조 없이도 innerclass의 인스턴스를 만들 수 있다.
    // outclass의 인스턴스를 사용하지 않는 innerclass는 static으로 만드는 것이 좋다.
    // outclass의 인스턴스를 사용하는 innerclass는 static으로 만들 수 없다.
    // static으로 만들 수 없는 innerclass는 outclass의 인스턴스를 사용하지 않는 것이 좋다.
    // outclass의 인스턴스를 사용하는 innerclass는 outclass의 인스턴스를 참조할 수 있는 필드를 추가하는 것이 좋다.
}
