package org.example.chapter04.item23.hierarchy;

// 태그 달린 클래스를 클래스 계층구조로 변환 (145쪽)
class Square extends Rectangle {
    Square(double side) {
        super(side, side);
    }

    @Override double area() {
        return length * length;
    }

    // 둘레를 구하는 메서드
    double perimeter() {
        return 4 * length;
    }

    // 이 메서드는 사각형이 아닌 정사각형을 반환한다!
    static Square newSquare(double side) {
        return new Square(side);
    }
}
