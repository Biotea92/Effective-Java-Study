# 🎁 **람다와 스트림**

이번 장에서는 자바 8에서 추가된 함수형 인터페이스, 람다, 메서드 참조, 스트림API를 효과적으로 사용하는 방법을 알아본다.

<br>

## **⭐️ 아이템 42 : 익명 클래스보다는 람다를 사용하라**
자바에서 함수 타입을 표현할 때 추상 메서드를 하나만 담은 인터페이스를 사용했다.  
이런 인터페이스의 인스턴스를 함수 객체라고 하여, 특정 함수나 동작을 나타내는 데 썼다.  
이렇게 자바8 이전에는 함수 객체를 만드는 주요 수단은 익명 클래스가 되었다.

- 익명클래스 예시
```java
Collections.sort(words, new Comparator<String>() {
    public int compare(String s1, String s2) {
        return Integer.compare(s1.length(), s2.length());
    }
});
```  

자바8부터는 추상 메서드 하나짜리 인터페이스는 특별한 의미를 인정받아 특별 대우를 받게 되었다.  
지금은 함수형 인터페이스라 부르는 이 인터페이스들의 인스턴스를 람다식이라고 한다.  
사식 익명 클래스와 개념은 비슷하지만 코드가 훨씬 간결하다.  


- 람다식 예시
```java
Collections.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length()));
```

여기서 람다, 매개변수(s1, s1), 반환값의 타입은 코드에서 전혀 언급이 없지만 컴파일러가 문맥을 살펴보고 타입을 추론해 줄 것이다. 상황에 따라 컴파일러가 타입을 결정하지 못할 때는 프로그래머가 직접 명시해야한다.  

***타입을 명시해야 코드가 더 명확할 때만 제외하고는, 람다의 모든 매개변수 타입은 생략하자.***  

***람다는 이름이 없고 문서화도 못 한다. 따라서 코드 자체로 동작이 명확히 설명되지 않거나 코드 줄 수가 많아지면 람다를 쓰지 말아야 한다.***  

람다의 this는 바깥인스턴스를 가리키는데 익명 클래스의 this는 익명 클래스의 인스턴스 자신을 가리킨다. 따라서 함수 객체가 자신을 참조해야 한다면 익명 클래스를 사용하자.

<br>

## **⭐️ 아이템 43 : 람다보다는 메서드 참조를 사용하라**

람다의 장점은 간결함이다. 자바에서 람다 보다 더 간결한 것이 있다면 ```메서드 참조```이다.

```java
public static void main(String[] args) {
    String[] args2 = {"a", "b", "c"};

    Map<String, Integer> frequencyTable = new TreeMap<>();
    frequencyTable.put("a", 1);
    frequencyTable.put("b", 2);

    for (String s : args2)
        frequencyTable.merge(s, 1, (count, incr) -> count + incr); // 람다
    System.out.println(frequencyTable);

    frequencyTable.clear();
    for (String s : args2)
        frequencyTable.merge(s, 1, Integer::sum); // 메서드 참조
    System.out.println(frequencyTable);
}
```

위 코드에서 람다와 메서드 참조를 비교해 보면 메서드 참조에서는 쓸데없는 count, incr가 제거 되고 있다.  
매개변수가 많아질수록 람다에서는 코드량이 늘어나지만 메서드 참조는 그렇지 않다.  
하지만 때때로 매개변수 명 자체가 프로그래머에게 가독성에 더 도움이 되기도 한다.  
또한 람다로 할 수 없는 일은 메서드 참조로도 할 수 없다.

### ***메서드 참조 유형***

| 메서드 참조 유형  |           예            |                     같은 기능의 람다                      |
|:----------:|:----------------------:|:--------------------------------------------------:|
|     정적     |   Integer::parseInt    |            str -> Integer.parseInt(str)            |
| 한정적(인스턴스)  | Instant.now()::isAfter | Instant then = Instant.now(); t -> then.isAfter(t) |
| 비한정적(인스턴스) |  String::toLowerCase   |              str -> str.toLowerCase()              |
|  클래스 생성자   |   TreeMap<K, V>::new   |              () -> new TreeMap<K, V>               |
|   배열 생성자   |       int[]::new       |                len -> new int[len]                 |

### ***결론***
메서드 참조 쪽이 짧고 명확하다면 메서드 참조를 쓰고, 그렇지 않을 때만 람다를 사용하라.

<br>

## **⭐️ 아이템 44 : 표준 함수형 인터페이스를 사용하라**
java.util.function 패키지에는 자주 사용하는 다양한 함수형 인터페이스가 정의되어 있다.  

필요한 용도에 맞는게 있다면 직접 구현하지 말고 라이브러리에서 활용하자.

### ***함수형 인터페이스 대표적 예시***
java.util.function 패키지에는 총 43개의 함수형 인터페이스가 정의되어 있다.  
대표적인 6개만 기억하면 나머지는 충분히 유추 가능하다.

| 인터페이스 | 함수 시그니처 | 예 | 설명 |
|:----------:|:------------:|:--:|:----:|
| UnaryOperator<T> | T -> T | String::toLowerCase | 하나의 인수를 받아 같은 타입의 결과를 반환 |
| BinaryOperator<T> | (T, T) -> T | BigInteger::add | 두 인수를 받아 같은 타입의 결과를 반환 |
| Predicate<T> | T -> boolean | String::isEmpty | 주어진 인수가 조건을 만족하는지 검사 |
| Function<T, R> | T -> R | Arrays::asList | 주어진 인수를 결과로 매핑 |
| Supplier<T> | () -> T | Instant::now | 인수 없이 값을 제공 |
| Consumer<T> | T -> void | System.out::println | 주어진 인수를 소비 |


표준형 함수형 인터페이스 대부분은 기본 타입만 지원한다.  
기본 함수형 인터페이스에 박싱된 기본 타입을 넣어 사용하지는 말자.  
오토박싱이 박싱된 기본 타입을 사용하는 코드보다 훨씬 느리다는 사실을 기억하자.  

구조적으로 똑같은 표준 함수형 인터페이스가 있더라도 직접 작성해야 할 경우도 있다.  
예를 들면 Comparator 인터페이스이다. 구조적으로는 ToIntBiFunction<T, U>와 동일하지만 사용하면 안된다. 그 이유는 아래와 같다.  
다음 세 가지 특성중 하나이상을 만족한다면 전용 함수형 인터페이스를 구현하는 것을 고민해보자.
- 자주 쓰이며, 이름 자체가 용도를 명확히 설명해준다.
- 반드시 따라야 하는 규약이 있다.
- 유용한 디폴트 메서드를 제공할 수 있다.

### ***항상 직접 만든 함수형 인터페이스에는 @FunctionalInterface 애너테이션을 사용하자.***
- 해당 클래스의 코드나 설명 문서를 읽을 이에게, 그 인터페이스가 람다용으로 설계된 것임을 알려준다.
- @FunctionalInterface 애너테이션은 해당 인터페이스가 추상 메서드를 오직 하나만 가지고 있음을 컴파일러에게 알려주는 역할을 한다.
- @FunctionalInterface 애너테이션을 붙이면 누군가 실수로 메서드를 추가하지 못하게 막아준다.

### ***함수형 인터페이스를 API에서 사용할 때의 주의점***
서로 다른 함수형 인터페이스를 같은 위치의 인수로 받는 메서드들을 다중 정의해서는 안된다.

<br>

## **⭐️ 아이템 45 : 스트림은 주의해서 사용하라**
스트림은 다량의 데이터 처리 작업을 돕고자 자바 8에 추가되었다.  
스트림은 데이터 원소의 유한 혹은 무한 시퀀스를 뜻한다.  
스트림 파이프라인은 이 원소들로 수행하는 연산 단계를 표현하는 개념이다.

### ***스트림 파이프라인***
스트림 파이프라인은 원소들의 소스로 시작해서 최종 연산으로 끝난다.  
중간에는 0개 이상의 중간 연산이 있을 수 있다.  
중간 연산은 스트림을 반환하므로 스트림 파이프라인을 연결할 수 있게 해준다.  
최종 연산은 스트림 파이프라인에서 결과를 도출한다.  
결과 도출시에는 원소를 정렬해 컬렉션에 담거나, 특정 원소 하나를 선택하거나, 모든 원소를 출력하는 등의 결과를 만들어 낸다.

### ***지연평가***
스트림 파이프라인은 지연평가(lazy evaluation)된다.  
즉, 최종 연산이 수행되기 전까지는 중간 연산이 수행되지 않는다.  
따라서 중간 연산이 무한 스트림을 다룰 수 있게 해준다.  

### ***스트림 병렬처리***
스트림 파이프라인은 내부적으로 쉽게 병렬화 할 수 있다.    
parallel 메서드를 호출하면 된다.  
자세한 내용은 아이템48에서 다루겠다.

### ***스트림이 안성맞춤인 로직***
- 원소들의 시퀀스를 일관되게 변환한다.
- 원소들의 시퀀스를 필터링한다.
- 원소들의 시퀀스를 하나의 연산을 사용해 결합한다.
- 원소들의 시퀀스를 컬렉션에 모은다.
- 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾는다.  

### ***스트림이 적합하지 않은 로직***
- 계산 과정이 복잡한 알고리즘을 구현할 때
- 코드 자체가 복잡할 때
- 코드가 무엇을 하는지 명확히 설명하기 어려울 때
- 이미 작성된 라이브러리가 잘 수행해주는 일을 할 때
- 프로그램의 동작을 바꿀 수 있는 훅을 제공할 때
- 스트림을 사용하면 가독성이 떨어지거나 유지보수가 어려워지는 경우
- 스트림을 사용하면 성능이 떨어지는 경우

### ***결론***
스트림을 사용해야 좋은 코드도 많지만, 반복문을 써야 좋은 코드도 많다.  
스트림과 반복문 중 어느 쪽이 나은지 확신하기 어려우면 둘 다 해보고 더 나은 쪽을 택하면 된다.

<br>

## **⭐️ 아이템 46 : 스트림에서는 부작용 없는 함수를 사용하라**

```java
List<String> words = List.of("apple", "banana", "orange", "papaya", "apple", "orange", "apple", "banana", "papaya", "orange", "apple", "kiwi");

// 스트림 패러다임을 이해하지 못한 채 API만 사용했다 - 따라 하지 말 것!
Map<String, Long> freq = new HashMap<>();
words.forEach(word -> {
    freq.merge(word.toLowerCase(), 1L, Long::sum);
});
```

위 코드는 스트림을 사용한 코드이다.  
하지만 스트림을 사용한 이유는 단순히 반복문을 사용하지 않기 위함이다.  
forEach 연산은 스트림 계산 결과를 보고할 때만 사용하고, 계산할 때는 쓰지 말자.

```java
// 스트림을 제대로 활용해 빈도표를 초기화한다.
freq = words.stream()
        .collect(groupingBy(String::toLowerCase, counting()));
```

위 코드는 스트림을 제대로 활용한 코드이다.  
Collector를 사용해서 스트림을 배열이나 리스트로 모을 때는 toList, toSet, toMap 같은 메서드를 사용하자.


```java
// 빈도표에서 가장 흔한 단어 10개를 뽑아내는 파이프라인
List<String> topTen = freq.keySet().stream()
        .sorted(comparing(freq::get).reversed())
        .limit(10)
        .collect(toList());
```

### ***Collectors를 사용한 메서드 예시***

|메서드|설명|
|---|---|
|toCollection|원하는 컬렉션에 수집|
|toMap|키와 값으로 수집|
|toSet|집합으로 수집|
|toList|리스트로 수집|
|toConcurrentMap|병렬 스트림에서만 사용 가능|
|groupingBy|분류|
|partitioningBy|분할|
|joining|문자열 연결|

<br>

## **⭐️ 아이템 47 : 반환 타입으로는 스트림보다 컬렉션이 낫다**

java7까지는 메서드의 반환 타입으로 Collection, Set, List 같은 컬렉션 인터페이스, 혹은 Iterable이나 배열을 썼다.  
for-each문에서만 쓰이거나 반환된 원소 시퀀스(주로contains(Object) 같은)가 일부 일부 Collection 메서드를 구현할 수 없을 때는 Iterable을 썼다.  

java8부터는 스트림이 추가되면서 컬렉션과 스트림 중 어느 것을 반환 타입으로 사용해야 할지 고민이 생겼다.  
스트림은 반복(Iteration)을 지원하지 않는다.  
따라서 스트림을 반환하면 클라이언트는 for-each문이나 for문을 사용할 수 없다. 이런 경우는 Iterable을 반환하는 것이 좋다.
따라서 스트림과 반복을 알맞게 조합해야 좋은 코드가 나온다.  

사실 Stream 인터페이스는 Iterable 인터페이스가 정의한 추상 메서드를 전부 포함하고,  
Iterable인터페이스가 정의한 방식대로 작동하지만,  
for-each로 스트림을 반복할 수 없는 이유는 Stream이 Iterable을 확장하지 않아서 이다.  

```java
// Stream<E>를 Iterable<E>로 중개해주는 어댑터
public static <E> Iterable<E> iterableOf(Stream<E> stream) {
        return stream::iterator;
}

// Iterable<E>를 Stream<E>로 중개해주는 어댑터
public static <E> Stream<E> streamOf(Iterable<E> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
}
```
필요하다면 위와 같은 어뎁터를 이용하자.

Collection 인터페이스는 Iterable의 하위 타입이고 stream 메서드도 제공 하니 반복과 스트림을 동시에 지원한다.  
따라서 API 반환타입으로 컬렉션이나 그 하위 타입을 쓰는 것이 최선이다.

### ***컬렉션 반환 메서드의 장점***
- 컬렉션을 반환하는 메서드는 스트림과 반대로 지연 평가가 불가능하지만, 클라이언트는 원하는 시점에 원소들을 얻을 수 있다.
- 컬렉션을 반환하는 메서드는 스트림을 반환하는 메서드보다 유연하다.
- 컬렉션을 반환하는 메서드는 스트림을 반환하는 메서드보다 성능이 더 좋을 가능성이 크다.
- 컬렉션을 반환하는 메서드는 스트림을 반환하는 메서드보다 사용하기 훨씬 쉽다.
- 컬렉션을 반환하는 메서드는 스트림을 반환하는 메서드보다 호환성이 더 좋다.

### ***결론***
원소 시퀀스를 반환하는 메서드를 작성할 때는, 이를 스트림으로 처리하기를 원하는 사용자와 반복으로 처리하길 원하는 사용자가 모두 있을 수 있음을 떠올리고, 양쪽을 다 만족시키려 노력하자. 컬렉션을 반환할 수 있다면 그렇게 하라. 반환 전부터 이미 원소들을 컬렉션에 담아 관리하고 있거나 컬렉션을 하나 더 만들어도 될 정도로 원소 개수가 적다면 ArrayList 같은 표준 컬렉션에 담아 반환하라. 그렇지 않으면 전용 컬렉션을 구현할지 고민하라. 컬렉션을 반환하는 게 불가능하면 스트림과 Iterable 중 더 자연스러운 것을 반환하라. 만약 나중에 Stream 인터페이스가 Iterable을 지원하도록 자바가 수정된다면, 그때는 안심하고 스트림을 반환하면 될 것이다(스트림 처리와 반복 모두에 사용할 수 있으니)

<br>

## **⭐️ 아이템 48 : 스트림 병렬화는 주의해서 적용하라**

스트림의 병렬화는 성능상의 이점을 제공할 수 있지만 신중하게 적용해야 한다.

```java
public static void main(String[] args) {
    primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
            .parallel() // 스트림 병렬화
            .filter(mersenne -> mersenne.isProbablePrime(50))
            .limit(20)
            .forEach(System.out::println);
}

static Stream<BigInteger> primes() {
    return Stream.iterate(TWO, BigInteger::nextProbablePrime);
}
```
위 코드는 병렬 스트림을 사용해 처음 20개의 메르센 소수를 생성한다.  
안타깝게도 parallel()을 넣어줌으로써 무한 로딩 상태가 된다.  

### ***스트림 병렬화의 성능 문제 원인***
데이터 소스가 Stream.iterate거나 중간 연산으로 limit를 쓰면 파이프라인 병렬화로는 성능 개선을 기대할 수 없다.
이유는 iterate 연산이 고정 상태로 유지되어야 병렬화가 효과를 발휘할 수 있는데,
iterate 연산은 이전 연산의 결과에 의존하기 때문에 병렬화가 불가능하다.

### ***스트림 병렬화의 성능***
대체로 스트림 파이프라인의 소스가 ArrayList, HashMap, HashSet, ConcurrentHashMap의 인스턴스거나 배열, int 범위, long 범위일 때 병렬화의 효과가 가장 좋다.  
이런 자료구조들은 데이터를 원하는 크기로 정확하고 손쉽게 나눌 수 있기 때문이다.  
반면에 LinkedList는 분할이 불가능하고, Stream.iterate와 같이 이전 결과에 의존하는 상황에서는 병렬화의 효과를 기대하기 어렵다.  

### ***스트림 병렬화의 옳게 된 예시***
조건이 잘 갖춰지면 parallel()을 호출하는 것만으로도 성능이 몇 배는 증가할 수 있다. 
```java
// 소수 계산 스트림 파이프라인 - 병렬화 버전
static long pi(long n) {
    return LongStream.rangeClosed(2, n)
            .parallel()
            .mapToObj(BigInteger::valueOf)
            .filter(i -> i.isProbablePrime(50))
            .count();
}
```
위 코드는 LongStream.rangeClosed를 이용해 소수를 계산하는 코드이다.  
이 코드는 병렬화가 잘 되어있어서 4코어 프로세서에서 4배의 성능 향상을 보여준다.  

### ***결론***
스트림 병렬화는 오직 성능 최적화 수단임을 기억하자.  
스트림을 잘못 병렬화하면 프로그램이 오동작하거나 성능이 심각하게 떨어질 수 있다.  
병렬화를 고려한다면 직접 해보고 성능이 좋아지는지 운영 환경과 유사항 상황에서 측정해보자.  
성능이 좋아졌음이 확실하면 운영 코드에 반영하자.