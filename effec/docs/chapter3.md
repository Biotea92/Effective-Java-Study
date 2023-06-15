# 🎁 **모든 객체의 공통 메서드**

자바에서 상속은 필수적이다. 개발자가 상속하지 않더라도 상속을 하게 된다.  
자바에서 모든 클래스는 ```Object```를 암시적으로 상속 받고 있다.  
따라서 ```Object```는 모든 클래스가 공통으로 포함하는 기능을 제공한다. 그 중에서도 (```equals```, ```hashcode```, ```toString```, ```clone```, ```finalize```)는 모두 재정의(overriding)를 염두에 두고 설계 된 것이다.  
Effective Java 3장에서는 overriding시 규약을 준수하여 재정의 할 수 있도록 방향을 제시한다.

<br>

[오라클 Object 문서](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Object.html)

## **⭐️ 아이템 10 : equals는 일반 규약을 지켜 재정의하라**

**oracle 문서에서 ```equals(Object obj)``` 정의 : Indicates whether some other object is "equal to" this one.**    
- 다른 개체가 이 개체와 동일한지 여부를 나타낸다고 나타내고 있다. 반환값은 boolean이다. 

### 🤔 왜 equals를 재정의해야 하는 것인가.

```java
public class Car {

    private String brand;
    private String model;

    public Car(String brand, String model) {
        this.brand = brand;
        this.model = model;
    }

    public static void main(String[] args) {
        Car car1 = new Car("현대", "소나타");
        Car car2 = new Car("현대", "소나타");

        System.out.println(car1);
        System.out.println(car2);
        System.out.println(car1.equals(car2));
    }
}
```
위 코드는 equals를 재정의하지 않고 Object의 equals를 그대로 사용한 예이다. 출력문을 살펴보자
```
org.example.chapter03.item10.Car@56cbfb61
org.example.chapter03.item10.Car@1134affc
false
```
이렇게 출력이 되었다. 내가 원하는 것은 car1과 car2가 동일하기를 원한다. 하지만 false가 출력되었다.  
이처럼 Object의 equals는 객체 식별성(object identity: 두 객체가 물리적으로 같은가)을 확인하고 있다.  
따라서 개발자가 논리적 동치성을 비교하기 위해서는 equals를 재정의 해주어야 한다.  

_**쉽게 말하자면 객체가 같은지 비교하고자 할때 사용하는 것이 아니라 객체의 값이 같은지 알고 싶을 때 사용한다.**_ 

논리적 동치성을 비교하고 싶지 않거나 인스턴스가 논리적으로 같은 인스턴스가 2개 이상 만들어지지 않으면 굳이 equals를 재정의 할 이유가 없다. ex) Enum

<br>

### ⭐️ equals의 재정의 규약 

1. **반사성 (reflexivity)** : null이 아닌 모든 참조 값 x에 대해 x.equals(x) 는 true이다.
2. **대칭성 (symmetry)** : null이 아닌 모든 참조 값 x, y에 대해 x.equals(y)가 true이면 y.equals(x)도 true이다.
3. **추이성 (transitivity)** : null이 아닌 모든 참조 값 x, y, z에 대해 x.equals(y)가 true이고 y.equals(z)도 true이면, x.equals(z)도 true이다.
4. **일관성 (consistency)** : null이 아닌 모든 참조 값 x, y에 대해 x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환해야 한다.
5. **null 아님** : null이 아닌 모든 참조 값 x에 대하여 x.equals(null)은 false이다.

<br>

### **_반사성_** 
자기 자신과 같아야 한다는 것, 일부러 어기지 않는 이상 거의 일어날 일이 없다. 

### **_대칭성_** 
두 객체가 서로에 대한 동치 여부에 똑같이 답해야 한다는 뜻이다. 
```java
a.equals(b) // true
b.equals(a) // true
```
책의 예시인 다음 경우를 보자 
```java
public final class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    // 대칭성 위배!
    @Override public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString)
            return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
        if (o instanceof String)  // 한 방향으로만 작동한다!
            return s.equalsIgnoreCase((String) o);
        return false;
    }

    public static void main(String[] args) {
        CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
        String s = "polish";

        System.out.println("cis.equals(s) = " + cis.equals(s));
        System.out.println("s.equals(cis) = " + s.equals(cis));
    }
}
```
출력문을 보면 다음과 같다.
```
cis.equals(s) = true
s.equals(cis) = false
```
- equals의 메서드 내용을 보면 당연하다. ```s.equals(cis)```에서 String class의 equals메서드는 CaseInsensitiveString class의 존재를 모르기 때문이다.  
- String을 연동하고자 하는 마음이 대칭성을 위배하였다. 
```java
@Override 
public boolean equals(Object o) {
    return o instanceof CaseInsensitiveString && ((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
}
```
- 수정 된 equals 메서드

### **_추이성_** 
첫 번째 객체와 두 번째 객체가 같고, 두 번째 객체와 세 번째 객체가 같다면, 첫 번째 객체와 세 번째 객체가 같아야 된다.  
말이 길지만 결국 연역법의 삼단논법과 비슷하다.

```java
public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;
        Point p = (Point) o;
        return p.x == x && p.y == y;
    }
}
```
Point Class를 상속해서 Color 추가한다.
```java
public class ColorPoint extends Point {
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }
}

public enum Color {
    RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, VIOLET
}
```
여기서 ColorPoint의 equals 메서드를 재정의 해주지 않으면 Point의 메서드가 상속된다. 그렇게 되면 Color를 비교하지 못한다.
```java
// 잘못된 코드 - 대칭성 위배!
@Override 
public boolean equals(Object o) {
    if (!(o instanceof ColorPoint))
        return false;
    return super.equals(o) && ((ColorPoint) o).color == color;
}
```
```java
Point p = new Point(1, 2);
ColorPoint cp = new ColorPoint(1, 2, Color.RED);
System.out.println(p.equals(cp) + " " + cp.equals(p));
// will print (true false)
```
해당 코드는 당연히 대칭성을 위배한다. 책의 예시라서 넣어보았다.  
다음은 추이성이 위배된 코드를 보자.

```java
@Override
public boolean equals(Object o) {
    if (!(o instanceof Point))
        return false;

    // o가 일반 Point면 색상을 무시하고 비교한다.
    if (!(o instanceof ColorPoint))
        return o.equals(this);

    // o가 ColorPoint면 색상까지 비교한다.
    return super.equals(o) && ((ColorPoint) o).color == color;
}
```
```java
ColorPoint p1 = new ColorPoint(1, 2, Color.RED);
Point p2 = new Point(1, 2);
ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE);
System.out.println(p1.equals(p2)); // true
System.out.println(p2.equals(p3)); // true
System.out.println(p1.equals(p3)); // false
```
대칭성은 만족하지만 추이성을 만족하지 못하는 코드가 된다.  
모든 객체 지향 언어의 동치관계에서는 **구체 클래스를 확장해 새로운 값을 추가하면서 equals규약을 만족시킬 방법은 존재하지 않는다.**  
이를 해결하기 위해 intanceof 대신 getClass()를 쓰게되면 같은 구현 클래스를 비교할 때만 true를 반환할 수 있다. 이는 리스코프 치환원칙이 깨진다. 

> 리스코프 치환원칙(LSP)  
SOLID 중 L  
올바른 상속 관계의 특징을 정의하기 위해 발표한 원칙  
부모 객체와 자식 객체가 있을 때 부모 객체를 호출하는 동작에서 자식 객체가 부모 객체를 완전히 대체할 수 있다는 원칙  

위 코드의 예로 들면 ColorPoint는 여전히 Point이므로 Point로써 활용 될 수 있어야 한다. 

위 코드 처럼 구체클래스의 하위 클래스에 값을 추가할 방법은 없지만 상속 대신 컴포지션을 사용하면 해결 가능하다. 

### **_일관성_** 
두 객체가 같다면 (둘 중 어느 하나도 수정되지 않았을 때) 앞으로도 영원히 같아야 한다.  

가변 객체는 상태가 바뀌면 equals 결과가 달라질 수 있지만, 불변 객체인 경우 그래선 안 된다.

### **_null 아님_** 
모든 객체가 null과 같지 않아야 한다.  
null 검사를 할 수는 있으나 필요하지 않다.  
instancof 연산자로 null 일시 false가 반환 되게 하면 묵시적으로 null 검사를 할 수 있다. 
```java
@Override 
public boolean equals(Object o) {
	if (!(o instanceof MyType))
		return false;
	MyType mt = (MyType) o;
	...
}
```

### ⭐️ 양질의 equals 메서드 구현 방법
1. == 연산자를 사용해 입력이 자기 자신의 참조인지 확인한다. (성능 최적화 용도)
2. instanceof 연산자로 입력이 올바른 타입인지 확인한다. 그렇지 않다면 false를 반환한다.
3. 입력을 올바른 타입으로 형변환 한다. 
4. 입력 객체와 자기 자신의 대응되는 '핵심' 필드들이 모두 일치하는지 하나씩 검사한다.
    - primitive type은 == 으로 비교하되, float, double은 compare() 메서드를 사용하자
    - null 이 정상으로 취급되는 객체라면 Objects.equals()메서드를 이용하자. 이는 NullPointException을 예방한다. 

equals를 구현했다면 세 가지 질문을 던지자. 대칭적인가? 추이성이 있는가? 일관적인가? 

### 주의사항
- equals를 재정의 할 때는 hashcode도 반드시 재정의하자.
- 너무 복잡하게 해결하려고 하지 말자. 필드들의 동치성만 검사해도 equals 규약을 어렵지 않게 지킬 수 있다.
- Object 외의 타입을 매개변수로 받는 equals메서드를 정의하지 말자. 해당 메서드는 Object.equals를 오버라이드 한 게 아니라 오버로딩 한 것에 불과하다.

### 개인적 생각
- 직접 구현하는 것보다 IDE나 Lombok의 도움을 받으면 쉽고 정확히 구현가능하다.