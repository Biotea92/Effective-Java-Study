# 🎁 **예외**

<br>

## **⭐️ 아이템 69 : 예외는 진짜 예외 상황에만 사용하라**

다음 두 코드를 보자 
```JAVA
try {
    int i = 0;
    while (true)
        range[i++].climb();
} catch (ArrayIndexOutOfBoundsException e) {
}
```
```java
for (Mountain m : range)
    m.climb();
```

두 코드 모두 같은 동작을 하는데 첫 번째는 예외를 이용해서 루프를 종료하였고, 두 번째는 배열의 끝에 도달하면 종료한다. 둘 다 같은 기능을 하지만 첫 번째 방법은 전혀 직관적이지 못하고 끔찍하다.  
이러한 문제는 세가지 관점에서 잘못되었다. 

1. 예외는 예외 상황에 쓸 용도로 설계되었으므로 JVM 구현자 입장에서는 명확한 검사만큼 빠르게 만들어야 할 동기가 약하다(최적화에 별로 신경 쓰지않았을 가능성이 크다.)
2. 코드를 try-catch 브록 안에 넣으면 JVM이 적용할 수 있는 최적화가 제한 된다.
3. 배열을 순회하는 표준 관용구는 앞서 걱정한 중복 검사를 수행하지 않는다. JVM이 알아서 최적화해 없애준다.  

예외는 오직 예외 상황에서만 써야 한다. 절대로 일상적인 제어 흐름용으로 쓰여선 안 된다.  

잘 설계된 API라면 클라이언트가 정상적인 제어 흐름에서 예외를 사용할 일이 없게 해야 한다.

<br>

## **⭐️ 아이템 70 : 복구할 수 있는 상황에는 검사 예외를, 프로그래밍 오류에는 런타임 예외를 사용하라**

### 에러
에러는 크게 나누면 컴파일 에러와 런타임 에러가 있다. 컴파일 과정에서 에러가 일어나면 컴파일 에러이고, 실행과정에서 에러가 일어나면 런타임 에러이다.  

컴파일 에러는 컴파일러가 문법 검사를 통해 오류를 잡아낸다.  
컴파일 과정에서 에러가 나지 않더라도 Runtime시 오류가 발생할 수 있는데 Runtime시 오류는 프로그래머가 직접 핸들링할 수 있어야 한다. Java에서는 런타임 에러를 Exception와 Error로 구분하고 있다.  

Error는 StackOverflowError나 OutOfMemoryError 처럼 Jvm에서 문제가 생기거나 하드웨어 문제가 생기는 경우이다. 이 경우 프로그래머는 미리 대비하여야 한다.  

Exception은 프로그램의 문제로 발생하는 경우인데 마찬가지로 프로그래머는 미리 적절하게 대비하여야 한다.  

### ***Checked Exception vs Unchecked Exception***


<div style="background-color:white;">
    <img src="img/2019-03-02-java-checked-unchecked-exceptions-1.png" />
</div>

<br>

위 그림처럼 Exception을 포함한 파란색은 Checked Exception 이고, RuntimeException, Error를 포함한 초록색은 Unchecked Exception으로 볼 수 있다.  

두 가지의 차이는 복구 가능성이 있느냐, 복구 가능성이 없느냐의 차이로 볼 수 있다. 
따라서 Checked Exception은 복구 가능성이 있기 때문에 예외를 처리하는 코드를 작성해야 하고, Unchecked Exception은 복구 가능성이 없는 예외이므로 예외처리를 강제하지 않는다.

![exception.PNG](img%2Fexception.PNG)

위 그림과 같이 Checked Exception은 예외 처리를 강제하고, Unchecked Exception은 예외처리를 강제하지 않는 것을 볼 수 있다.

```java
public void throwException() throws Exception {
    throw new Exception();
}
```
따라서 위와 같이 예외를 바깥으로 던져주거나 그 자리에서 처리해주어야 한다.  

### ***언제 Checked Exception Unchecked Exception을 사용할까?***
호출하는 쪽에서 복구하리라 여겨지는 상황이라면 Checked Exception을 사용하자.  
프로그래밍 오류를 나타낼 때는 RuntimeException를 사용하자.  
Error 클래스를 상속해 하위 클래스를 만드는 일은 자제하자.  
Throwable은 절대 사용하지 말자.  

```
스프링 프레임워크가 제공하는 선언적 트랜잭션(@Transactional)안에서 에러 발생 시 Checked Exception는 롤백이 되지 않고, Unchecked Exception는 롤백이 된다.  
하지만 rollbackFor 옵션을 통해서 위 상황을 바꿀 수 있다.
```

<br>

## **⭐️ 아이템 71 : 필요 없는 검사 예외 사용은 피하라**
검사 예외를 싫어하는 자바 프로그래머가 많지만 제대로 활용하면 API와 프로그램의 질을 높일 수 있다. 검사 예외는 발생한 문제를 프로그래머가 처리하여 안전성을 높이게끔 해준다.  

하지만 검사 예외를 과하게 쓰면 오히려 쓰기 불편한 API가 된다. 검사 예외를 쓰면 이를 호출하는 코드는 예외를 직접 처리하거나 바깥으로 던져야만 한다. 또한 검사 예외를 던지는 메서드는 스트림 안에서 직접 사용할 수 없다.

프로그래머는 왠만하면 비검사 예외를 쓰는 쪽이 좋다.  
검사 예외를 회피하기 위해 빈 옵셔널을 반환하거나 boolean을 반환하여 비검사 예외를 활용하자.

<br>

## **⭐️ 아이템 72 : 표준 예외를 사용하라**

| 예외                              | 설명                                                        |
|---------------------------------|-----------------------------------------------------------|
| IllegalArgumentException        | 허용하지 않는 값이 인수로 건네졌을 때(null은 따로 NullPointerException으로 처리) |
| IllegalStateException           | 객체가 메서드를 수행하기에 적절하지 않은 상태일 때                              |
| NullPointerException            | null을 허용하지 않는 메서드에 null을 건넸을 때                            |
| IndexOutOfBoundsException       | 인덱스의 범위를 넘어섰을 때                                           |
| ConcurrentModificationException | 허용하지 않는 동시 수정이 발견되었을 때                                    |
| UnsupportedOperationException   | 호출한 메서드를 지원하지 않을 때                                        |

Exception, RuntimeException, Throwable, Error는 직접 재사용하지 말자, 이 클래스들은 추상 클래스라고 생각하자. 이 예외들은 다른 예외들의 상위 클래스이므로, 즉 여러 성격의 예외들을 포괄하는 클래스이므로 안정적으로 테스트할 수 없다.  

나도 그랬듯이 항상 IllegalArgumentException과 IllegalStateException의 사용을 헷갈렸는데, 인수 값이 무엇이었든 어차피 실패했을거라면 IllegalStateException을, 그렇지 않으면 IllegalArgumentException을 던지자.

<br>

## **⭐️ 아이템 73 : 추상화 수준에 맞는 예외를 던지라**

수행하려는 일과 관련 없어 보이는 예외가 튀어나오면 당황스럽다. 메서드가 저수준 예외를 처리하지 않고 바깥으로 전파해버릴 때 종종 일어난다.  

이런 경우는 프로그래머를 당황시키고, 내부 구현 방식을 드러내어 윗 레벨 API를 오염시킨다. 또한 다음 릴리즈에서 구현 방식을 바꾸면 다른 예외가 튀어나와 기존 클라이언트를 프로그램을 깨지게 한다.

### ***예외 번역***

위와 같은 문제를 피하려면 상위 계층에서는 저수준 예외를 잡아 자신의 추상화 수준에 맞는 예외로 바꿔 던져야 한다. 이를 예외 번역이(exception translation)라고 한다.

```java
try {
    ... // 저수준 추상화를 이용한다.
} catch (LowerLevelException e) {
    // 추상화 수준에 맞게 번역한다.
    throw new HigherLevelException(...);
}
```

다음은 AbstractSequentialList에서 수행하는 예외 번역의 예이다.
```java
public E get(int index) {
    try {
        return listIterator(index).next();
    } catch (NoSuchElementException exc) {
        throw new IndexOutOfBoundsException("Index: "+index);
    }
}
```

### ***예외 연쇄***

예외를 번역할 때, 저수준 예외가 디버깅에 도움이 된다면 예외 연쇄를 사용하자.  
예외 연쇄란 문제의 원인(cause)의 저수준 예외를 고수준 예외에 실어 보내는 방식이다.  
이 방법으로 필요하면 언제든 저수준 예외를 꺼내 볼 수 있다.

```java
try {
    ... // 저수준 추상화를 이용한다.
} catch (LowerLevelException cause) {
    // 저수준 예외를 고수준 예외에 실어 보낸다.
    throw new HigherLevelException(cause);
}
```

```java
// 예외 연쇄용 생성자
class HigherLevelException extends Exception {
    HigherLevelException(Throwable cause) {
        super(cause);
    }
}
```

예외를 전파하는 것보다야 예외 번역이 우수한 방법이지만, 남용해서는 안된다.  
가능하면 저수준 메서드가 반드시 성공하도록하여 아래 계층에서는 예외가 발생하지 않도록 하는 것이 최선이다. 때로는 상위 계층의 메서드에서 매개변수 값을 아래로 보내기전 미리 검사할 수도 있다.  

차선책으로는 아래 계층에서의 예외를 피할 수 없다면, 상위 계층에서 그 예외를 조용히 처리하여 문제를 API 호출자에까지 전파하지 않는 방법이 있다. 이 경우 발생한 예외는 logging으로 적절히 기록해 두면좋다. 이 방법은 클라이언트 코드와 사용자에게 문제를 전파하지 않으면서도 프로그래머가 로그를 분석해 추가 조치를 취할 수 있게 해준다.

<br>

## **⭐️ 아이템 74 : 메서드가 던지는 모든 예외를 문서화하라**

검사 예외는 항상 따로따로 선언하고, 각 예외가 발생하는 상황을 자바독의 @thorws 태그를 사용하여 정확히 문서화하자. 
- 공통 상위 클래스 하나로 뭉뚱그려 선언하지 말자.
- Exception, Throwable을 던진다고 선언하지말자. 
- 위 규칙에 유일한 예외는 main 메서드이다. main은 오직 JVM만이 호출하므로 Exception을 던지도록 선언해도 된다. 

비검사 예외도 검사 예외처럼 정성껏 문서화해두면 좋다.  
메서드가 던질 수 있는 예외를 각각 @thorw 태그로 문서화하되, 비검사 예외는 메서드 선언의 thorws 목록에 넣지 말자.  

한 클래스에 정의된 많은 메서드가 같은 이유로 같은 예외를 던진다면 그 예외를 (각각의 메서드가 아닌) 클래스 설명에 추가하는 방법도 있다.

<br>

## **⭐️ 아이템 75 : 예외의 상세 메시지에 실패 관련 정보를 담으라**

실패 순간을 포착하려면 발생한 예외에 관여된 모든 매개변수와 필드의 값은 실패 메시지에 담아야 한다.  
현상들은 모두 원인이 다르므로, 현상을 보면 무엇을 고쳐야 할지 분석하는데 큰 도움이 된다.  

- 예외의 실패 관련 정보 예시  
```java
// 실패 상황을 온전히 포착하도록 수정해본 IndexOutOfBoundsException
public class IndexOutOfBoundsException extends RuntimeException {
    private final int lowerBound;
    private final int upperBound;
    private final int index;

    /**
     * IndexOutOfBoundsException을 생성한다.
     *
     * @param lowerBound 인덱스의 최솟값
     * @param upperBound 인덱스의 최댓값 + 1
     * @param index      인덱스의 실젯값
     */
    public IndexOutOfBoundsException(int lowerBound, int upperBound,
                                     int index) {
        // 실패를 포착하는 상세 메시지를 생성한다.
        super(String.format(
                "최솟값: %d, 최댓값: %d, 인덱스: %d",
                lowerBound, upperBound, index));

        // 프로그램에서 이용할 수 있도록 실패 정보를 저장해둔다.
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.index = index;
    }
}
```  

접근자 메서드는 비검사 예외보다는 검사예외에서 빛을 발하지만, 비검사 예외라도 상세 정보를 알려주는 접근자 메서드를 제공하면 좋다.

<br>

## **⭐️ 아이템 76 : 가능한 한 실패 원자적으로 만들라**

호출된 메서드가 실패하더라도 해당 객체는 메서드 호출 전 상태를 유지해야 한다.

### ***메서드를 실패 원자적으로 만드는 방법***
1. 불변 객체로 설계 한다.
2. 가변 객체의 메서드를 실패 원자적으로 만드는 가장 흔한 방법은 작업 수행에 앞서 매개변수의 유효성을 검사한다.
    - 객체의 내부 상태를 변경하기 전에 잠재적 예외의 가능성 대부분을 걸러낼 수 있는 방법이다.
3. 객체의 임시 복사본에서 작업을 수행한 다음, 작업이 성공적으로 완료되면 원래 객체와 교체한다. 
    - 데이터를 임시 자료구조에 저장해 작업하는 게 더 빠를 때 적용하기 좋은 방식이다.
4. 작업 도중 발생하는 실패를 가로채는 복구 코드를 작성하여 작업 전 상태로 되돌리는 방법이다.
    - (디스크 기반의) 내구성을 보장해야 하는 자료구조에 쓰인다.


<br>

## **⭐️ 아이템 77 : 예외를 무시하지 말라**

이번 주제는 너무 뻔하지만 많은 사람들이 어긴다. 나 또한 어긴적이 있다.  


예외를 무시하는 방법은 try문으로 감싼 후 catch 블록에서 아무 일도 하지 않으면 끝이다.  

예외는 문제 상황에 잘 대처하기 위해 존재하는데 catch블록을 비워두면 예외가 존재할 이유가 없어진다.  

예외를 무시해야할 때도 있다. 예외를 무시하기로 했다면 catch 블록 안에 그러헥 결정한 이유를 주석으로 남기고 예외변수의 이름도 ignored로 바꿔놓자.


