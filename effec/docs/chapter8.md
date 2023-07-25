# 🎁 **메서드**

이번 챕터에서는 메서드 설계시 주의할 점에 대해서 알아본다.  
매개변수와 반환값, 메서드 시그니처, 문서화 등에 대해 구체적으로 알아보자. 

<br>

## **⭐️ 아이템 49 : 매개변수가 유효한지 검사하라**

메서드와 생성자의 대부분은 입력된 매개변수의 값이 특정 조건을 만족하기를 바란다. 그렇기 때문에 "오류는 가능한 발생한 곳에서 잡아야한다"라는 일반 원칙으로 제약 조건들은 반드시 문서화하자.  

메서드가 실행되기 전에 매개변수를 확인한다면 잘못된 값이 넘어오면 즉각 예외를 던질 수 있다.  
매개변수 검사를 제대로 하지 못하면 여러가지 문제가 생길 수 있다.  
첫번째, 메서드 수행 중간에 모호한 예외를 던지며 실패할 수 있다. 더 나아가서 메서드가 잘 수행 되지만 잘못된 결과를 반환할 수 있다. 이로 인해 먼 미래에 실패 원자성을 어기를 결과를 낳을 수 있다.  

public이나 protected 메서드는 매개변수 값이 잘못됬을 때 던지는 예외를 문서화해야한다. 전형적인 Exception은 NullPointException, IllegalArgumentException, IndexOutOfBoundsException이 있다. 

- 전형적인 문서 예시
```java
/**
 * 현재 값 mod m을 반환한다. 이 메서드는 항상 음이 아닌 BigInteger를 반환한다.
 *
 * @param m 계수 (양수여야한다.)
 * @return 현재 값 mod m
 * @throws ArithmeticException if the modulus value is less than or equal to zero
 */
public BigInteger mode(BigInteger m) {
    if (m.signum() <= 0)
        throw new ArithmeticException("Modulus <= 0: " + m);
    // ... 계산수행
}
```

자바7에서 추가된 ```Objects.requireNonNull``` 메서드는 유연하고 사용하기 편하기 더이상 null 검사를 수동으로 하지 않아도 된다.
```java
this.strategy = Objects.requireNonNull(strategy, "전략");
```

자바9에서는 Objects에 범위 검사 기능도 더해졌다.
```java
Objects.checkIndex(index, list.size());
Objects.checkFromToIndex(fromIndex, toIndex, list.size());
Objects.checkFromIndexSize(fromIndex, size, list.size());
```

또한 private 메서드에서는 assert를 사용해 매개변수 유효성을 검증할 수 있다.

```java
private static void sort(long a[], int offset, int length) {
    assert a != null;
    assert offset >= 0 && offset <= a.length;
    assert length >= 0 && length <= a.length - offset;
    // ... 계산 수행
}
```

생성자는 "나중에 쓰려고 저장하는 매개변수의 유효성을 검사하라"는 원칙의 특수한 사례이다. 생성자 매개변수의 유효성 검사는 클래스 불변식을 어기는 객체가 만들어지지 않게 하는데 꼭 필요하다.  

### 결론
메서드나 생성자를 작성할 때 매개변수들에 어떤 제약이 있을지 생각하고, 메서드 시작 부분에서 명시적으로 검사하고, 문서화하자. 이러한 습관은 이후 실제 오류를 걸러낼 때 보상받을 것이다.

<br>

## **⭐️ 아이템 50 : 적시에 방어적 복사본을 만들라**
자바는 안전한 언어이다. 하지만 아무리 자바라 해도 모든 침범을 아무런 노력없이 다 막을 수 있는 것은 아니다. 그러니 **클라이언트가 언제든 클래스의 불변식을 깨뜨리고, 보안을 뚫으려는 시도를 한다고 가정하고 방어적으로 프로그래밍해야한다.**  

어떤 객체든 그 객체의 허락없이는 외부에서 내부를 수정하는 일은 불가능하다. 하지만 주의를 기울이지 않으면 자기도 모르게 내부를 수정하도록 허락할 수도 있다.
```java
// 기간을 표현하는 클래스 - 불변식을 지키지 못했다. 
public final class Period {
    private final Date start;
    private final Date end;

    /**
     * @param  start 시작 시각
     * @param  end 종료 시각. 시작 시각보다 뒤여야 한다.
     * @throws IllegalArgumentException 시작 시각이 종료 시각보다 늦을 때 발생한다.
     * @throws NullPointerException start나 end가 null이면 발생한다.
     */
    public Period(Date start, Date end) {
        if (start.compareTo(end) > 0)
            throw new IllegalArgumentException(
                    start + "가 " + end + "보다 늦다.");
        this.start = start;
        this.end   = end;
    }

    public Date start() {
        return start;
    }
    public Date end() {
        return end;
    }
}
```

이 클래스는 불변처럼 보이고 start가 end보다 늦을 수 없을 것 같지만. Date가 가변이기 때문에 불변식을 깨뜨릴 수 있다.

```java
// Period 인스턴스의 내부를 공격해보자. 
Date start = new Date();
Date end = new Date();
Period p = new Period(start, end);
end.setYear(78);  // p의 내부를 변경했다!
```

다행히 자바 8 이후로는 쉽게 해결할 수 있다. Date 대신 불변인 객체를 사용하면된다. 예를들면 LocalDateTime, ZonedDateTime, Instant  

외부공격으로부터 Period 인스턴스의 내부를 보호하려면 생성자에서 받은 가변 매개변수 각각을 방어적으로 복사해야한다.  
```java
// 수정한 생성자 - 매개변수의 방어적 복사본을 만든다. 
public Period(Date start, Date end) {
    this.start = new Date(start.getTime());
    this.end   = new Date(end.getTime());

    if (this.start.compareTo(this.end) > 0)
        throw new IllegalArgumentException(
                this.start + "가 " + this.end + "보다 늦다.");
}
```

매개변수의 유효성 검사를 하기 전에 방어적 복사본을 만들고, 이 복사본으로 유효성을 검사했다. 그 이유는 유효성 검사한 후 복사본을 만드는 찰나 취약한 순간에 다른 스레드가 원본 객체를 수정할 가능성이 있기 때문이다. 이를 검사시점/사용시점 공격 TOCTOU공격이라한다.  

또한 방어적 복사에 clone메서드를 사용할 수도 있지만 clone이 악의를 가진 하위 클래스의 인스턴스를 반환할 수도 있다. 따라서 **매개변수가 제3자에 의해 확장될 수 있는 타입이라면 방어적 복사본을 만들 때 clone을 사용해서는 안된다.**  

생성자를 수정하면 앞선 공격은 막아낼 수 있지만 접근자 메서드로 아직 변경이 가능하다.

```java
// Period 인스턴스를 향한 두 번째 공격
start = new Date();
end = new Date();
p = new Period(start, end);
p.end().setYear(78);  // p의 내부를 변경했다!
```

접근자가 가변 필드의 방어적 복사본을 반환하면 두번째 공격도 막아낼 수 있다.

```java
// 수정한 접근자 - 필드의 방어적 복사본을 반환한다. 
public Date start() {
    return new Date(start.getTime());
}

public Date end() {
    return new Date(end.getTime());
}
```

이렇게 모든 필드가 객체 안에 완벽하게 캡슐화가 되었다.    
우리는 이런 작업을 하면서 "되도록 불변 객체를 사용해 객체를 구성해야 방어적 복사를 할 일이 줄어든다"라는 것을 깨달았을 것이다. 위 예제는 사실 LocalDateTime으로 사용하면 방어적 복사를 안해도 되었을 것이다.  

방어적 복사에는 복사 비용이 들어간다. 그렇기 때문에 비용이 너무 크다면 방어적 복사를 수행하는 대신 해당 객체를 수정했을 때 책임이 클라이언트에 있음을 문서에 명시하자.

<br>

## **⭐️ 아이템 51 : 메서드 시그니처를 신중히 설계하라**

1. 메서드 이름은 신중히 짓자. 
    - 항상 표준 명명 규칙을 따르고, 일관되게 짓자.
    - 개발자 커뮤니티에서 널리 받아들여지는 이름을 사용하자.
2. 편의 메서드를 너무 많이 만들지 말자.
    - 메서드가 너무많은 클래스는 익히고, 사용하고, 문서화하고, 테스트하고, 유지보수하기 힘들다.
3. 매개변수 목록은 짧게 유지하자.
    - 4개 이하가 좋다.
    - 같은 타입의 매개변수가 연달아 나오는 경우가 특히 해롭다.
    - 매개변수를 줄여주는 기술
        - 여러 메서드로 쪼갠다.
        - 매개변수 여러 개를 묶어주는 도우미 클래스를 만든다.
        - 앞 선 두 기법을 혼합한다. 매개변수가 많을 때, 그 중에서도 일부는 생략해도 괜찮을 때 사용.
    - 매개변수 타입으로는 클래스보다는 인터페이스가 더 낫다.
    - boolean보다는 원소 2개짜리 열거 타입이 낫다.
        - 코드를 읽기가 쉬워진다.
    

<br>

## **⭐️ 아이템 52 : 다중정의는 신중히 사용하라**

재정의(오버라이딩)한 메서드는 동적으로 선택되고, 다중정의(오버로딩)한 메서드는 정적으로 선택된다.  

다중정의한 메서드는 어느 메서드를 호출할지가 컴파일 타임에 정해지고, 재정의한 메서드는 객체의 런타임 타입이 어떤 메서드를 호출할지의 기준이 된다.  

일반적으로 매개변수의 수가 같을 때는 다중정의를 피하자. 특히나 같은 위치의 매개변수가 형변환하여 같은 타입이 될 수 있는 상황이면 다중정의를 피하는 것이 좋다.  
만약 어쩔 수 없이 다중정의를 한다면 형변환하여 정확한 다중정의 메서드가 선택되도록 해야한다.  
따라서 근본적으로 다르면 헷갈릴 일이 없으니 다중정의를 해도 좋다.  
함수형 인터페이스의 경우 서로 다른 함수형 인터페이스라도 같은 위치의 인수로 받아서는 안 된다.

<br>

## **⭐️ 아이템 53 : 가변인수는 신중히 사용하라**

인수 개수가 일정하지 않은 메서드를 정의해야 한다면 가변인수가 반드시 필요하다. 메서드를 정의할 때 필수 매개변수는 가변인수 앞에 두고, 가변인수를 사용할 때는 성능 문제까지 고려하자.

<br>

## **⭐️ 아이템 54 : null이 아닌, 빈 컬렉션이나 배열을 반환하라**

메서드에서 null을 반환하면 null 상황을 처리하는 코드를 추가적으로 작성해야한다. 때로는 null이 아닌 빈 컬렉션이나 배열을 반환하는 것도 비용이 드니 null을 사용해한다고 주장하기도하지만, 굳이 새로 할당하지 않아도된다.  

Collection.emptyList, Collection.emptySet, Collection.emptyMap 을 사용하면 매번 똑같은 빈 불변 컬렉션을 반환한다.  

배열도 마찬가지로 길이 0짜리 배열을 미리 선언해서 사용하면 된다.

```java
private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

public Cheese[] getCheeses() {
    return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
}
```

<br>

## **⭐️ 아이템 55 : 옵셔널 반환은 신중히 하라**

Java에서는 값이 없음을 반환하기위해서 null을 반환한다. 이는 NullPointerException을 발생하고 코드 오류가 발생하여 유지관리하기가 쉽지 않다.

java8 이후로는 Optional이라는 선택지가 생겨 null을 반환하는 대신 Optional을 반환하여 값이 없는 경우를 비었다로 표현 가능하다. 

```java
// 컬렉션에서 최댓값을 구해 Optional<E>로 반환한다.
public static <E extends Comparable<E>>
Optional<E> max(Collection<E> c) {
    if (c.isEmpty())
        return Optional.empty();

    E result = null;
    for (E e : c)
        if (result == null || e.compareTo(result) > 0)
            result = Objects.requireNonNull(e);

    return Optional.of(result);
}
```

위 코드와 같이 사용하면 null 대신 옵셔널을 반환하여 반환값이 없을 수 있음을 API사용자에게 명확히 알려준다.  

때로는 매개변수나 배열 요소로 옵셔널을 사용하는 경우가 있다. 이는 옳지 않다. 반환을 하기 위해 옵셔널을 사용해야한다.  

orElse나 orElseGet을 사용하여 Optional에 값이 비어있는 경우 기본값을 설정할 수 있다.  

orElseThrow는 Optional이 비어있는 경우 예외를 터트릴 수 있다.  

Optional이 비어있는지 검사하지 않고 get을 사용하는 것은 옳지 않다. NoSuchElementException이 발생한다. 필요하다면 isPresent와 같은 boolean을 반환하는 메서드로 검사하고 get을 사용하자.

<br>

## **⭐️ 아이템 56 : 공개된 API요소에는 항상 문서화 주석을 작성하라**

Java에서는 문서를 자동화하여 만들어주는 Javadoc 유틸리티가 있어 주석을 통해 API문서를 생성할 수 있다.  

물론 필수는 업계 표준이 정해져 있으니 한번쯤은 익혀놓아도 좋을 것 같다.   

Oracle에서 공식적으로 제공하는 [How to Write Doc Comments](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html) 문서 자바독을 사용하는 법을 설명한다.  
자바4 이후로는 갱신되지 않은 페이지라 그 이후에 나온 javadoc 기능은 빠져 있다. 대표적으로 @code, @implSpec, @index 이다.  

javadoc 주석은 API를 문서화하는 가장 훌륭하고 효과적인 방법이다. 공개 API라면 빠짐없이 설명을 달아야 한다. 표준 규약을 일관되게 지키자. javadoc주석에 임의의 HTML태그를 사용할 수 있음을 기억하라. 단 HTML 메타문자는 특별하게 취급해야한다. 