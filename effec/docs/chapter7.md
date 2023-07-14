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

위 코드에서 람다와 메서드 참조를 비교해 보면 메서드 참조에서는 쓸데없는 count, incr 가 제거 되고 있다.  
매개변수가 많아질수록 람다에서는 코드량이 늘어나지만 메서드 참조는 그렇지 않다.  
하지만 때때로 매개변수 명 자체가 프로그래머에게 가독성에 더 도움이 되기도 한다.  
또한 람다로 할 수 없는 일은 메서드 참조로도 할 수 없다.

<br>

## **⭐️ 아이템 44 : 표준 함수형 인터페이스를 사용하라**

<br>

## **⭐️ 아이템 45 : 스트림은 주의해서 사용하라**

<br>

## **⭐️ 아이템 46 : 스트림에서는 부작용 없는 함수를 사용하라**

<br>

## **⭐️ 아이템 47 : 반환 타입으로는 스트림보다 컬렉션이 낫다**

<br>

## **⭐️ 아이템 48 : 스트림 병렬화는 주의해서 적용하라**