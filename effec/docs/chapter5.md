# 🎁 **제네릭**
제네릭(generic)은 자바 5부터 사용할 수 있다. 제네릭을 사용하게 되면 컬렉션이 담을 수 있는 타입을 컴파일러에게 알려준다.  
그 효과로 컴파일러가 형변환 코드를 추가하여 안전하고 명확하게 프로그래밍을 할 수 있다.

- 제네릭 예시 ( List )
```java
public interface List<E> extends Collection<E> {
    int size();
    boolean isEmpty();
    //...
    Iterator<E> iterator();
    E get(int index);
}
```
위 인터페이스 처럼 클래스와 인터페이스 선언에 타입 매개변수가 쓰이면, 이를 **제네릭 클래스** 혹은 **제네릭 인터페이스**라 한다. 

List 인터페이스는 원소의 타입을 나타내는 타입 매개변수 ```E```를 받는다. 이런 제네릭 클래스와 제네릭 인터페이스를 통틀어 **제네릭 타입**이라고 한다.  

- **네이밍** : 보통 제네릭 타입은 'T'를 사용하는 것을 권장한다. 하지만 어떤 문자를 사용해도 된다.  

    - E : 요소 (Element, 자바 컬렉션에서 주로 사용됨)
    - K : 키
    - N : 숫자
    - T : 타입
    - V : 값
    - S,U,V : 두번 째, 세 번째, 네 번째에 선언된 타입

<br>

## **⭐️ 아이템 26 : 로 타입은 사용하지 말라**

각각의 제네릭 타입은 일련의 **매개변수화 타입**을 정의한다.  
```List<String>``` 에서 String은 정규 타입 매개 변수 E에 해당하는 실제 타입 매개변수이다.  

### ***🤔 로 타입*** 

로 타입(raw type)은 제네릭 타입에서 타입 매개변수를 전혀 사용하지 않을 때를 말한다. 
예를 들어 List<E>에서 로 타입은 ```List```이다.  
로 타입은 타입 선언에서 제네릭 타입 정보가 전부 지워진 것 처럼 동작한다.

```java
// Stamp 인스턴스만 취급하기를 기대한다.
private final Collection stamps = ...;

// 정상적으로 컴파일 되지만 unchecked call 경고를 뱉는다.
stamps.add(new Coin(...));

// 이후에 Collection에서 동전을 꺼내면서 형 변환시 예외가 발생된다. -> 컴파일 시점까지는 모른다.
```
가장 좋은 예외는 컴파일 시점에 발견되는 예외이다.  
위의 예는 런타임시 발생하기 때문에 에러를 잡기 힘들다.
```JAVA
// 타입 매개변수 선언
private final Collection<Stamp> stamps = ...;

// 컴파일 시 에러 
stamps.add(new Coin(...));
```

이렇게 로 타입을 쓰게 되면 제네릭이 안겨주는 타입 안정성과 표현력을 모두 잃게 된다.  
java에서 이렇게 쓸데 없는 로 타입을 만든 이유는 제네릭이 생기기 전의 코드들과의 호환성 때문에 만들어 놓았다. 또한 이런 호환성을 위한 제네릭 구현에는 소거(erasure)를 방식을 사용한다.  

List와 같은 로타입 대신 List<Object> 같은 매개변수화 타입은 사용해도 된다.

### ***📌 비 한정적 와일드 카드***
이 쯤 되면 원소의 타입을 몰라도 되는 로 타입을 쓰고 싶어 진다.
- 잘 못된 예시(로타입) - 안전하지 않다.
```JAVA
static int numElementsInCommon(Set s1, Set s2) {
    int result = 0;
    for (Object o1 : s1)
        if (s2.contains(o1))
            result++;
    return result;
}
```
- 옳은 예시 (비한정적 와일드 카드 타입) - 타입 안전하며 유연하다.
```JAVA
static int numElementsInCommon(Set<?> s1, Set<?> s2) {
    ...
}
``` 

### ***📌 로 타입을 써야할 때***
1. class 리터럴에는 로 타입을 써야 한다.
2. instanceof 연산자를 사용 할때는 로 타입을 써야한다. 
```java
if (o instanceof Set) { // 로타입
    Set<?> s = (Set<?>) o; // 와일드카드 타입
    // ...
}
```

### 📌 **용어 정리**

| 한글 용어         | 영문 용어                   | 예                                      |
|---------------|-------------------------|----------------------------------------|
| 매개변수화 타입      | parameterized type      | ```List<String>```                     |
| 실제 타입 매개변수    | actual type parameter   | ```String```                           |
| 제네릭 타입        | generic type            | ```List<E>```                          |
| 정규 타입 매개변수    | formal type parameter   | ```E```                                |
| 비한정적 와일드카드 타입 | unbounded wildcard type | ```List<?>```                          |
| 로 타입          | raw type                | ```List```                             |
| 한정적 타입 매개변수   | bounded type parameter  | ```<E extends Number>```               |
| 한정적 와일드카드 타입  | bounded wildcard type   | ```List<? extends Number>```           |
| 제네릭 메서드       | generic method          | ```static <E> List<E> asList(E[] a)``` |
| 타입 토큰         | type token              | ```String.class```                     |


<br>

## **⭐️ 아이템 27 : 비검사 경고를 제거하라**

<br>

## **⭐️ 아이템 28 : 배열보다는 리스트를 사용하라**

<br>

## **⭐️ 아이템 29 : 이왕이면 제네릭 타입으로 만들라**

<br>

## **⭐️ 아이템 30 : 이왕이면 제네릭 메서드로 만들라**

<br>

## **⭐️ 아이템 31 : 한정적 와일드카드를 사용해 API 유연성을 높이라**

<br>

## **⭐️ 아이템 32 : 제네릭과 가변인수를 함께 쓸 때는 신중하라**
