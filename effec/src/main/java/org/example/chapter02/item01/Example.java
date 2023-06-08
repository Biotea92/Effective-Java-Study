package org.example.chapter02.item01;

import java.math.BigInteger;
import java.util.EnumSet;
import java.util.Random;

public class Example {

    public static void main(String[] args) {

        // 정적 팩터리 메서드의 예시
        Boolean isTrue = Boolean.valueOf(true);

        // 정적 팩터리 메서드의 다섯가지 장점
        // 1. 이름을 가질 수 있다.
        BigInteger primeNumber1 = new BigInteger(100, new Random());
        BigInteger primeNumber2 = BigInteger.probablePrime(100, new Random());
        // BigInteger.probablePrime(1, new Random()) 가 명시적으로 값이 소수인 BigInteger를 반환한다는 의미를 잘 나타냄

        // 2. 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다.

        // 3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.

        // 4. 입력 매개 변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
        // EnumSet.noneOf() 에서는 원소의 갯수에 따라 다른 하위타입의 인스턴스를 반환한다.

        // 5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.
        // 대표적인 예로 JDBC API가 있다.
        // Connection, Drivermanager.registerDriver, DriverManager.getConnection 등

        // 정적 팩터리 메서드의 단점
        // 1. 상속을 하려면 public이나  protected 생성자가 필요하니 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다.
        // 2. 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.
        // 정적 팩터리 메서드에서 흔히 사용하는 명명 방식들 예시
    }
}
