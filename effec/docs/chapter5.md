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

- **Type Erasure** - [오라클 문서](https://docs.oracle.com/javase/tutorial/java/generics/erasure.html) 내용 참조  
Generics were introduced to the Java language to provide tighter type checks at compile time and to support generic programming. To implement generics, the Java compiler applies type erasure to:
    - Replace all type parameters in generic types with their bounds or Object if the type parameters are unbounded. The produced bytecode, therefore, contains only ordinary classes, interfaces, and methods.
    - Insert type casts if necessary to preserve type safety.
    - Generate bridge methods to preserve polymorphism in extended generic types.
    - 내가 이해한 내용 : 기존의 타입을 버린다. unbounded type이면 버리고 Object로 bouned type이면 bound type으로 캐스팅 한다.

<br>

## **⭐️ 아이템 26 : 로 타입은 사용하지 말라**

각각의 제네릭 타입은 일련의 **매개변수화 타입**을 정의한다.  
```List<String>``` 에서 String은 정규 타입 매개 변수 E에 해당하는 실제 타입 매개변수이다.  

### 🤔 ***로 타입이란*** 

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
제네릭을 사용하기 시작하면 수 많은 컴파일러 경고를 마주하게 된다.  
비검사 형변환 경고, 비검사 메서드 호출 경고, 비검사 매개변수화 가변인수 타입 경고, 비검사 변환 경고 등  

이번 아이템에서는 할 수 있는 한 모든 비검사 경고를 제거하도록 제시한다.  
모든 경고를 제거한다면 런타임에 ClassCastException이 발생하지 않고 타입 안정성을 가질 수 있다.

### 📌 **비검사 경고 제거 방법**
- 대부분의 비검사 경고  
    다이아몬드 연산자만 추가하면 해결된다.
```JAVA
Set<Lark> exaltation = new HashSet();
Set<Lark> exaltation = new HashSet<>();
```
- 경고를 제거할 수는 없지만 타입 안전하다고 확신할 수 있으면 ```@SuppressWarnings("unchecked")``` 애노테이션을 달아 경고를 숨기자
    - 꼭 타입이 안전함을 검증하고 어노테이션을 사용해야한다.
    - ```@SuppressWarnings("unchecked")```은 꼭 가능한 한 좁은 범위에 사용하자. 지역변수, 클래스 전체 어디든 달 수 있는 어노테이션이기 때문에 좁게 설정이 가능하다.
    - 어노테이션을 사용할 때 경고를 무시해도 안전한 이유를 항상 주석을 남겨라

<br>

## **⭐️ 아이템 28 : 배열보다는 리스트를 사용하라**
배열과 제네릭 타입에서는 중요한 차이가 두 가지 있다.  

### 📌 ***차이 1*** 
배열은 공변이다.  
제네릭은 불공변이다.  
다음 코드를 보면 공변과 불공변의 차이를 쉽게 이해할 수 있다.  
```java
/* 런타임에 실패한다 */
Object[] objectArray = new Long[1];
objectArray[0] = "타입이 달라 넣을 수 없다."; // ArrayStoreException 예외 발생

/* 컴파일되지 않는다. */
List<Object> ol = new ArrayList<Long>();
ol.add("타입이 달라 넣을 수 없다."); // 컴파일 오류 발생
```
배열은 하위 타입을 상위 타입 배열에 넣을 수 있지만, 제네릭은 불가능하다.  
두 코드 모두 Long용 저장소에 String을 넣을 수 없지만 에러를 알아 내는 시점이 다르기 때문에 리스트가 더 좋다.

### 📌 ***차이 2***
배열은 실체화된다. -> 배열은 런타임 시에도 자신이 담기로 한 원소 타입을 인지하고 확인하여 예외를 던진다.  
제네릭은 타입 정보가 런타임에는 소거(Erasure)된다.  

### 📌 ***제네릭 배열 생성 오류***
제네릭과 배열은 같이 어우러지지 못한다. 배열은 제네릭 타입, 매개변수화 타입, 타입 매개변수로 사용할 수 없다. ```new List<E>[]```, ```new List<String>[]```, ```new E[]```식으로 작성하면 컴파일할 때 제네릭 배열 생성 오류를 일으킨다.  


```E. List<E>, List<String>``` 같은 타입은 실체화 불가 타입(non-reifiable-type)이다. 
따라서 실체화되지 않기 때문에 런타임에는 컴파일타임보다 타입 정보를 적게 가지는 타입이다.  
소거 메커니즘으로 실체화될 수 있는 타입은 ```List<?>, Map<?,?>``` 같은 비한정적 와일드카드 타입뿐이다.

### 📌 ***제네릭 타입과 가변인수***
제네릭 타입과 가변인수를 함께 쓰면 어려운 경고 메시지를 받게 된다.  
그 이유는 가변인수 메서드는 호출 될 때 가변인수 매개변수를 담을 배열이 하나 만들어지기 때문에 배열의 원소가 실체화 불가 타입이라면 경고가 발생한다.  
이 문제는 ```@SafeVarargs``` 어노테이션으로 해결할 수 있다


### 📌 결론
배열은 공변이고 실체화된다. 제네릭은 불공변이고 타입 정보가 소거된다.  
배열은 런타임에는 타입 안전, 컴파일 타임에는 불안전하고,  
제네릭은 런타임에는 불안전, 컴파일 타임에는 안전하다.  
따라서 둘을 혼합해서 사용하면 좋지 않다. 혹시라도 혼합해서 사용한다면 배열을 리스트로 대체하는 것을 고려해라.

<br>

## **⭐️ 아이템 29 : 이왕이면 제네릭 타입으로 만들라**
제네릭 타입과 메서드를 사용하는 것은 보통은 쉽지만 제네릭 타입 class를 새로 만드는 일은 조금 더 어렵다.

### 📌 ***제네릭 타입 만드는 방법***
- Object 기반 Stack - 이후 제네릭으로 변경 예정!
```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }
}
```
- 위 클래스를 제네릭 타입으로 순서대로 변경할 것이다. 
- 첫 번째 방법 - 클래스 선언 타입 매개변수를 추가한다. 타입 이름은 Element의 약자 E를 사용한다.
    ```java
    public class Stack<E> {
        private E[] elements;
        private int size = 0;
        private static final int DEFAULT_INITIAL_CAPACITY = 16;

        public Stack() {
            elements = new E[DEFAULT_INITIAL_CAPACITY];
        }

        public void push(E e) {
            ensureCapacity();
            elements[size++] = e;
        }

        public E pop() {
            if (size == 0)
                throw new EmptyStackException();
            E result = elements[--size];
            elements[size] = null; // 다 쓴 참조 해제
            return result;
        }

        ... // isEmpty와 ensureCapacity 메서드는 그대로
    }
    ```
    - 이 단계에서 오류나 경고가 발생한다. ```new E``` 부분 
    ```java
    elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    ```
    - 위 코드로 바꾸게 되면 컴파일 오류는 발생하지 않지만 이제는 경고를 띄운다.
    - 컴파일러가 타입이 안전한지 확인할 방법이 없지만 우리가 판단했을 때 이 비검사 형변환은 안전하다고 판단되면 ```@SuppressWarnings```를 달아 경고를 숨기자.
    ```JAVA
    @SuppressWarnings("unchecked")
    public Stack() {
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }
    ```
- 두번째 방법 - elements 필드의 타입을 E[]에서 Object[]로 바꾼다.
    ```java
    public class Stack<E> {
        private Object[] elements;
        private int size = 0;
        private static final int DEFAULT_INITIAL_CAPACITY = 16;
        
        public Stack() {
            elements = new Object[DEFAULT_INITIAL_CAPACITY];
        }

        public void push(E e) {
            ensureCapacity();
            elements[size++] = e;
        }

        // 비검사 경고를 적절히 숨긴다.
        public E pop() {
            if (size == 0)
                throw new EmptyStackException();

            // push에서 E 타입만 허용하므로 이 형변환은 안전하다.
            @SuppressWarnings("unchecked") E result = (E) elements[--size];

            elements[size] = null; // 다 쓴 참조 해제
            return result;
        }
    }
    ```
    - E는 실체화 불가 타입이므로 컴파일러는 런타임에 이뤄지는 형변환이 안전한지 증명할 방법이 없다. 
    - 위와 마찬가지로 안전하다고 증명되면 ```@SuppressWarnings``` 어노테이션으로 필드에서 경고를 숨기자
- 결국 두 가지 방법 모두 쓰인다. 현업에서는 첫 번째 방법을 선호하고, 힙 오염이 걱정되면 두 번째 방법을 쓰기도 한다. 

### 📌 ***결론***
제네릭 타입은 안전하고 쓰기가 편하지만 만들기는 쉽지않다. 그러므로 쓰기 편할 수 있게 제네릭 타입을 만들어 제공하자. 그렇다면 사용자가 훨씬 편하게 이용할 수 있다.  
ex ) 사용자가 직접 형변환하지 않아도됨  

<br>

## **⭐️ 아이템 30 : 이왕이면 제네릭 메서드로 만들라**
- 메서드를 제네릭 메서드로 만들면 일반 메서드 보다 활용도가 높아진다.
```java
public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
    Set<E> result = new HashSet<>(s1);
    result.addAll(s2);
    return result;
}
```
- 단순한 제네릭 메서드라면 이 정도로 충분하지만 때때로 불변 객체를 여러 타입으로 활용할 수 있게 만들어야 할 때도 있다.
- 제네릭은 런타임에 타입 정보가 소거되므로 하나의 객체를 어떤 타입으로든지 매개변수화 할 수 있다. 
- 하지만 앞선 방식을 사용하기 위해서는 요청한 타입 매개 변수에 맞게 매번 그 객체의 타입을 바꿔주는 정적 팩터리를 만들어야한다. 이러한 패턴을 ```제네릭 싱글턴 팩터리```라고 한다. ```Collections.reverseOrder```나 ```Collections.emptySet``` 같은 컬렉션용으로 사용한다. 
```JAVA
public class Collections {

    // ... 
    
    @SuppressWarnings("unchecked")
    public static <T> Comparator<T> reverseOrder() {
        return (Comparator<T>) ReverseComparator.REVERSE_ORDER;
    }

    private static class ReverseComparator
        implements Comparator<Comparable<Object>>, Serializable {
        
        static final ReverseComparator REVERSE_ORDER = new ReverseComparator();
        // ...
    }

    // ...

    @SuppressWarnings("unchecked")
    public static <T> Comparator<T> reverseOrder(Comparator<T> cmp) {
        if (cmp == null) {
            return (Comparator<T>) ReverseComparator.REVERSE_ORDER;
        } else if (cmp == ReverseComparator.REVERSE_ORDER) {
            return (Comparator<T>) Comparators.NaturalOrderComparator.INSTANCE;
        } else if (cmp == Comparators.NaturalOrderComparator.INSTANCE) {
            return (Comparator<T>) ReverseComparator.REVERSE_ORDER;
        } else if (cmp instanceof ReverseComparator2) {
            return ((ReverseComparator2<T>) cmp).cmp;
        } else {
            return new ReverseComparator2<>(cmp);
        }
    }

    // ...

    @SuppressWarnings("rawtypes")
    public static final Set EMPTY_SET = new EmptySet<>();

    @SuppressWarnings("unchecked")
    public static final <T> Set<T> emptySet() {
        return (Set<T>) EMPTY_SET;
    }

    // ...
}
```

- 항등함수를 담은 클래스를 만들고 싶다면 다음과 같이 제네릭 싱글턴을 이용해 만들자

```java
private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

@SuppressWarnings("unchecked")
public static <T> UnaryOperator<T> identityFunction() {
    return (UnaryOperator<T>) IDENTITY_FN;
}
```

- 다음은 재귀적 타입 한정을 이용해 상호 비교할 수 있음을 표현한 것이다.

```java
// 최대값을 반환하는 메서드
public static <E extends Comparable<E>> E max(Collection<E> c) {
    if (c.isEmpty())
        throw new IllegalArgumentException("컬렉션이 비어 있습니다.");

    E result = null;
    for (E e : c)
        if (result == null || e.compareTo(result) > 0)
            result = Objects.requireNonNull(e);

    return result;
}
```

- ```<E extends Comparable<E>>```는 "모든 타입 E는 자기 자신과 비교할 수 있다"라고 읽는다.

<br>

## **⭐️ 아이템 31 : 한정적 와일드카드를 사용해 API 유연성을 높이라**

### 📌 ***한정적 와일드카드를 사용해야 하는 이유***
- 예를 들어 앞서 제시간 Stack class에 메서드를 추가한다고 생각 해보자 
```java
// 와일드카드 타입을 사용하지 않은 pushAll 메서드 - 결함이 있다!
public void pushAll(Iterable<E> src) {
    for (E e : src)
        push(e);
}
```
- 위 메서드는 src의 원소 타입이 Stack의 원소타입과 일치하면 잘 작동한다. 하지만

```java
Stack<Number> numberStack = new Stack<>();
Iterable<Integer> integers = Arrays.asList(3, 1, 4, 1, 5, 9);
numberStack.pushAll(integers);
```
- 논리적으로 Integer는 Number의 하위 타입이라 잘 작동할 것 같지만
- 매개변수화 타입이 불공변이기 때문에 위 코드는 에러가 난다. 
- 해결 방법은 한정적 와일드카드를 사용하는 것이다.

```java
// E 생산자(producer) 매개변수에 와일드카드 타입 적용
public void pushAll(Iterable<? extends E> src) {
    for (E e : src)
        push(e);
}
```

- 뜻을 해석하면 ```E의 하위타입의 Iterable이어야한다``` 이다..
- 반대로 popAll 메서드을 작성해보자.
```java
// 와일드카드 타입을 사용하지 않은 popAll 메서드 - 결함이 있다! 
public void popAll(Collection<E> dst) {
    while (!isEmpty())
        dst.add(pop());
}
```

- 위 코드도 문제 없어보이지만 결함이 있다. ```Stack<Number>```의 원소를 ```Object용 컬렉션```으로 옮기려 한다고 해보자.

```java
Stack<Number> numberStack = new Stack<>();
Collection<Object> objects = new ArrayList<>();
numberStack.popAll(objects);
```

- 이번에도 불공변이기 때문에 에러가 발생한다. 

```java
// E 소비자(consumer) 매개변수에 와일드카드 타입 적용
public void popAll(Collection<? super E> dst) {
    while (!isEmpty())
        dst.add(pop());
}
```

- 뜻을 해석하면 ```E의 상위 타입의 Collection이어야 한다.``` 이다.
- 결국 유연성을 극대화 하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하라.
- PECS 공식 : producer-expends, consumer-super
- iteam 30 에서의 max를 한정적 와일드 카드를 적용해 보자.(매우 어렵)
```java
public static <E extends Comparable<? super E>> E max(List<? extends E> list) {
    if (list.isEmpty())
        throw new IllegalArgumentException("빈 리스트");

    E result = null;
    for (E e : list)
        if (result == null || e.compareTo(result) > 0)
            result = e;

    return result;
}
```
- 입력 매개 변수는 E 인스턴스를 생산하므로 ```List<? extends E>```로 설정한다.
- ```Comparable<E>```는 E 인스턴스를 소비한다. 따라서 ```Comparable<? super E>```로 설정했다. 
- 사실 Comparable이나 Comparator은 언제나 소비자이므로 ```Comparable<? super E>```로 설정하면 된다.

### 📌 ***결론***
와일드카드 타입을 적용하면 API가 훨씬 유연해진다.  
PECS공식을 기억하여 producer는 extends를 consumer는 super를 사용하자.

<br>

## **⭐️ 아이템 32 : 제네릭과 가변인수를 함께 쓸 때는 신중하라**
아이템 28에서 봤듯이 제네릭 varargs 배열 매개변수에 값을 저장하는 것은 타입 안전성을 깨뜨린다. 또한 경고 메시지를 던진다.  

하지만 자바는 가변인수와 제네릭을 같이 사용하는 것을 허용하고 있고 자바라이브러리```(Arrays.asList(T... a), Collections.addAll(Collection<? super T> c, T... elements), EnumSet.of(E first, E... rest)```에서도 이런 메서드를 제공한다. 그 이유는 실무에서 유용하기 때문이다.  

따라서 이를 사용할 때는 자바 7에서 추가된 ```@SafeVarargs``` 어노테이션으로 경고 메시지를 숨기자. 단 신중하게 타입이 안전함이 보장되어야만 어노테이션을 써야한다. 그리고 타입 안정성을 보장할 수 없다면 item28 처럼 List를 고려하자.

### 📌 ***결론***
가변인수와 제네릭은 그 자체로는 쓸모 있지만 함께 쓰기에는 적합하지 않다. 가변인수 기능은 배열을 노출하여 추상화가 완벽하지 못하고, 배열과 제네릭의 타입 규칙이 서로 다르기 때문이다.  
제네릭 varargs 매개변수는 타입 안전하지는 않지만, 허용된다. 메서드에 제네릭(혹은 매개변수화된) varargs 매개변수를 사용하고자 한다면, 먼저 그 메서드가 타입 안전한지 확인한 다음 @SafeVarargs 어노테이션을 달아 사용하는 데 불편함이 없게끔하자.

<br>

## **⭐️ 아이템 33 : 타입 안전 이종 컨테이너를 고려하라**
보통 제네릭은 ```Set<E>, Map<K,V>``` 와 같이 단일원소 컨테이너에서 흔히쓰인다.  
하지만 종종 컨테이너 자체가 아닌 키를 가져야 할 때가 있다. 이 때 고려되는 방식이 타입 안전 이종 컨테이너 패턴이다.  

- 코드를 보면 쉽게 이해할 수 있다.
```java
// 타입 안전 이종 컨테이너 패턴
public class Favorites {
    private Map<Class<?>, Object> favorites = new HashMap<>();

    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), instance);
    }

    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));
    }

    public static void main(String[] args) {
        Favorites f = new Favorites();
        
        f.putFavorite(String.class, "Java");
        f.putFavorite(Integer.class, 0xcafebabe);
        f.putFavorite(Class.class, Favorites.class);
       
        String favoriteString = f.getFavorite(String.class);
        int favoriteInteger = f.getFavorite(Integer.class);
        Class<?> favoriteClass = f.getFavorite(Class.class);
        
        System.out.printf("%s %x %s%n", favoriteString,favoriteInteger, favoriteClass.getName());
    }
}
```
- Class 객체를 매개변수화한 키 역할로 사용해서 타입에 따른 값을 저장한다.
- 이로써 일반적인 Map과 다르게 여러 타입의 원소를 담을 수 있게 되었다.  
- 하지만 문제가 있다면 Favorites클래스에는 두 가지 제약이 있다.
    - 첫번째 

        사용자가 일부로 로 타입으로 Class객체를 넘기면 instance의 타입 안정성이 깨진다.  
        이때는 동적 형변환을 사용해 런타임 타입 안전성을 확보하자.
        ```java
        public <T> void putFavorite(Class<T> type, T instance) {
            favorites.put(Objects.requireNonNull(type), type.cast(instance));
        }
        ```
    - 두번째

        실체화 불가 타입에는 사용 할 수 없다.  
        ```String, String[]```는 저장 되도 ```List<String>```은 저장할 수 없다.  
        만족스럽게 우회할 수 있는 방법도 없는 편이다.
        - 스프링에서는 슈퍼 타입 토큰으로 해결하기 위해 ParameterizedTypeReference라는 클래스로 구현해놓았다.
    
### 한정적 타입 토큰을 활용하기

```java
static <T extends Annotation> T getAnnotation(
        AnnotatedElement element, Class<T> annotationType) {
    return element.getAnnotation(annotationType);
}
```
- 위 코드는 어노테이션 타입이 키로 된 타입 안전 이종 컨테이너이다.
- Class<?>를 사용해서 한정적 타입 토큰을 받는 메서드를 넘기기 위해서는 asSubclass 메서드로 형변환 하는 과정이 필요하다.
```java
static Annotation getAnnotation(AnnotatedElement element,
                                String annotationTypeName) {
    Class<?> annotationType = null; // 비한정적 타입 토큰
    try {
        annotationType = Class.forName(annotationTypeName);
    } catch (Exception ex) {
        throw new IllegalArgumentException(ex);
    }
    return element.getAnnotation(annotationType.asSubclass(Annotation.class));
}
```
