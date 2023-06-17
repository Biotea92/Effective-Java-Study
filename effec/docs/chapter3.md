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

### 📌 equals의 재정의 규약 

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

### 📌 양질의 equals 메서드 구현 방법
1. == 연산자를 사용해 입력이 자기 자신의 참조인지 확인한다. (성능 최적화 용도)
2. instanceof 연산자로 입력이 올바른 타입인지 확인한다. 그렇지 않다면 false를 반환한다.
3. 입력을 올바른 타입으로 형변환 한다. 
4. 입력 객체와 자기 자신의 대응되는 '핵심' 필드들이 모두 일치하는지 하나씩 검사한다.
    - primitive type은 == 으로 비교하되, float, double은 compare() 메서드를 사용하자
    - null 이 정상으로 취급되는 객체라면 Objects.equals()메서드를 이용하자. 이는 NullPointException을 예방한다. 

equals를 구현했다면 세 가지 질문을 던지자. 대칭적인가? 추이성이 있는가? 일관적인가? 

### 📌 주의사항
- equals를 재정의 할 때는 hashcode도 반드시 재정의하자.
- 너무 복잡하게 해결하려고 하지 말자. 필드들의 동치성만 검사해도 equals 규약을 어렵지 않게 지킬 수 있다.
- Object 외의 타입을 매개변수로 받는 equals메서드를 정의하지 말자. 해당 메서드는 Object.equals를 오버라이드 한 게 아니라 오버로딩 한 것에 불과하다.

### 개인적 생각
- 직접 구현하는 것보다 IDE나 Lombok의 도움을 받으면 쉽고 정확히 구현가능하다.

<br>

## **⭐️ 아이템 11 : equals를 재정의하려거든 hashCode도 재정의하라**

**oracle 문서에서 ```hashCode()``` 정의 : Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by HashMap.**    
- 개체의 해시 코드 값을 반환합니다. 이 방법은 HashMap에서 제공하는 것과 같은 해시 테이블의 이점을 위해 지원됩니다.
- 반환 되는 값은 integers로 개별 개체에 대해 개별 정수를 반환한다.

### 🤔 왜 hashCode를 재정의해야 하는 것인가.

- equals 비교에 사용되는 정보가 변경되지 않았다면, 그 객체의 hashCode 메서드는 몇 번을 호출해도 일관되게 항상 같은 값을 반환해야한다. 애플리케이션을 다시 실행하면 값이 달라져도 상관없다. 
- equals가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다.
- equals가 두 객체를 다르다고 판단했더라도, 두 객체의 hashCode가 서로 다른 값을 반환할 필요는 없다. 단, 다른 객체에 대해서는 다른 값을 반환해야 해시 테이블의 성능이 좋아진다. 

먼저 HashMap의 get(Object key) 메서드를 보면
```java
public V get(Object key) {
    Node<K,V> e;
    return (e = getNode(hash(key), key)) == null ? null : e.value;
}

final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        if ((e = first.next) != null) {
            if (first instanceof TreeNode)
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            do {
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}

static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```
> get(Object key) 설명 : 지정된 키가 매핑된 값을 반환하거나, 이 맵에 키에 대한 매핑이 포함되어 있지 않으면 null을 반환합니다. 보다 공식적으로 이 맵에 (key==null ? k==null : key.equals(k))와 같은 키 k에서 값 v로의 매핑이 포함되어 있으면 이 메서드는 v를 반환합니다. 그렇지 않으면 null을 반환합니다. (이러한 매핑은 최대 하나일 수 있습니다.) null 반환 값이 반드시 맵에 키에 대한 매핑이 포함되어 있지 않음을 나타내는 것은 아닙니다. 맵이 키를 null에 명시적으로 매핑할 수도 있습니다. containsKey 작업을 사용하여 이 두 가지 경우를 구분할 수 있습니다.  

객체의 hash값으로 value를 찾는 것을 볼 수 있다.  
따라서 hashCode를 재정의 하지 않게 되면 
```java
public static void main(String[] args) {
    Map<PhoneNumber, String> m = new HashMap<>();
    m.put(new PhoneNumber(707, 867, 5309), "제니");
    System.out.println(m.get(new PhoneNumber(707, 867, 5309)));
}
```
출력문이 기대하는 제니가 아니라 null이 반환 된다.  
이는 hashCode 메서드만 재정의 해주면 해결 된다.

<br>

### 📌 올바른 hashCode 구현방법

좋은 해시 함수는 서로 다른 인스턴스에 다른 해시코드를 반환한다. 

```java
@Override 
public int hashCode() {
    int result = Short.hashCode(areaCode);
    result = 31 * result + Short.hashCode(prefix);
    result = 31 * result + Short.hashCode(lineNum);
    return result;
}
```
전형적인 hashCode의 메서드이다.

```java
@Override public int hashCode() {
    return Objects.hash(lineNum, prefix, areaCode);
}
```
이 방법은 간단하지만 내부를 들여다 보면 속도가 더 느리다.

- 책에서는 해시의 키로 자주 사용되는 경우는 캐싱하는 방법을 고려하라고 한다.  
- 해시의 키로 사용되지 않을것 같은 경우 지연 초기화 전략을 권장한다.
- 성능을 높인답시고 해시코드를 계산할 때 핵심 필드를 생략해서는 안된다. 

<br>

## **⭐️ 아이템 12 : toString을 항상 재정의하라**

**oracle 문서에서 ```toString()``` 정의 : Returns a string representation of the object.**   
- 개체의 문자열 표현을 반환합니다.
- ```getClass().getName() + '@' + Integer.toHexString(hashCode())```
- toString()을 재정의하면 프로그래머가 디버깅하고 읽기 쉽다. 재정의하지 않으면 위의 코드 처럼 반환해 값을 알아차리기 힘들다. 

- 정적 유틸리티 클래스나 Enum Class는 따로 toString을 재정의 할 필요없다. 

<br>

## **⭐️ 아이템 13 : clone 재정의는 주의해서 진행하라**

**oracle 문서에서 ```clone()``` 정의**
- 이 객체의 복사본을 생성해 반환한다. 복사의 정확한 뜻은 그 객체를 구현한 클래스에 따라 다를 수 있다. 
```java
x.clone() != x // true
```
```java
x.clone().getClass() != x.getClass() // true
```
```java
x.clone().getClass() != x.getClass() 
// 보통 참이지만 필 수는 아니다.
```
- 관례상, 이 메서드가 반환하는 객체는 super.clone을 호출해 얻어야 한다. 이 객체의 모든 상위 클래스가 이 관례를 따른다면 다음의 식은 참이다.
```java
x.clone().getClass() == x.getClass()
```
- 관례상, 반환된 객체와 원본 객체는 독립적이어야 한다. 이를 만족하려면 super.clone으로 얻은 객체의 필드 중 하나 이상을 반환 전에 수정해야 할 수도 있다.


### Cloneable
```java
public interface Cloneable {
}
```
Cloneable은 아무 메서드도 없는 메서드이다. 

clone 메서드를 사용하려면 Cloneable 인터페이스를 구현해 주어야 한다.  
하지만, 예상과 다르게 Object에 메서드가 선언되어있고 그마저도 protected이다. 

### clone메서드를 제대로 사용하기

clone 메서드를 제대로 사용하려면 일단 Cloneable을 상속하고
```java
@Override
public PhoneNumber clone() { // 접근제어자를 public으로 제공
    try {
        return (PhoneNumber) super.clone(); // Object의 clone() 메서드 호출
    } catch (CloneNotSupportedException e) {
        throw new AssertionError();  // 일어날 수 없는 일이다.
    }
}
```
하지만 구현이 가변 객체를 참조하는 순간 재앙이 발생한다. 
```java
public class Stack implements Cloneable {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        this.elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    @Override
    public Stack clone() {
        try {
            Stack result = (Stack) super.clone();
            result.elements = elements.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
````

만약 위 처럼 ```clone()``` 메서드를 재정의하는게 아니라 단순히 ```super.clone()```으로 객체를 반환하게 되면 elements의 배열은 같은 배열을 참조하기 때문에 두 객체중 하나를 수정하면 다른 하나도 수정될 것이다.  
따라서 위 ```clone()``` 메서드와 같이 사용하면 재귀적으로 호출되어 문제를 해결할 수 있다.  
하지만 elements가 final로 선언되어 있으면 이 방법도 통하지 않는다.  
또한 LinkedList를 이용한 방법도 있지만 완벽하지 않다. 

책에서는 결국 복사  팩터리나 복사 생성자를 이용하라고 말하고 있다.  

### 결론

배열만 clone 메서드를 이용해서 복사하고, 나머지는 팩터리나 생성자를 이용하자. 

<br>

## **⭐️ 아이템 14 : Comparable을 구현할지 고려하라**

```java
public interface Comparable<T> {
    public int compareTo(T o);
}
```
Comparable은 유일하게 compareTo() 메서드만을 제공한다.  
보통 해당 메서드는 동치성 혹은 순서를 비교할 때 이용한다.  

이 객체가 주어진 객체보다 작으면 음수, 같으면 0, 크면 양수를 반환하고 비교할 수 없는 타입의 객체는 예외를 던진다.

### comapareTo 규약

1. 두 객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야 한다. 
2. 첫번 째가 두번째코다 크고 두번째가 세 번째보다 크면, 첫 번째는 세 번째보다 커야 한다.
3. compareTo메서드로 수행한 동치성 테스트의 결과가 equals와 같아야 한다. 

- compareTo 메서드에서 필드의 값을 비교할 때 <, > 연산자는 쓰지 말자
- 박싱된 기본 타입 클래스가 제공하는 정적 compare 메서드나 Comparator인터페이스가 제공하는 비교자 생성 메서드를 사용하라.