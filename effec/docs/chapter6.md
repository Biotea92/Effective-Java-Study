# 🎁 **열거 타입과 애너테이션**

java의 특수한 목적의 참조 타입인 enum과 annotation에 대해 올바르게 사용하는 방법을 알아보자

<br>

## **⭐️ 아이템 34 : int 상수 대신 열거 타입을 사용하라**

enum이 나오기전에는 보통 상수를 정수 열거 패턴 또는 문자열 열거 패턴을 사용했다.  

```java
// 정수 열거 패턴 - 상당히 취약하다.
public static final int APPLE_FUJI = 0;
public static final int APPLE_PIPPIN = 1;
public static final int APPLE_GRANNY_SMITH = 2;

public static final int ORANGE_NAVEL = 0;
public static final int ORANGE_TEMPLE = 1;
public static final int ORANGE_BLOOD = 2;
```
- 정수 열거 패턴의 단점
    - 타입 안전을 보장할 방법이 없으며 표현력도 좋지않다.  
    예를 들어 사과와 오렌지를 (==)으로 비교하더라도 아무 에러를 던지지 않는다.
    - 정수 열거 패턴을 사용하면 프로그램이 깨지기 쉽다.  
    - 정수 상수는 문자열로 출력하기 까다롭다.  
    출력하거나 디버그하면 숫자로만 보여서 알기가 어렵다.
- 정수 대신 문자를 이용하기도 하지만 프로그래머들을 하드코딩하게 만든다.
- 이러한 이유들로 열거 타입이 대안으로 제시 된다.

```java
// 가장 단순한 열거 타입
public enum Apple { FUJI, PIPPIN, GRANNY_SMITH }
public enum Orange { NAVEL, TEMPLE, BLOOD }
```
- enum type 특징
    - 클래스이다.
    - 상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final 필드로 공개한다.
    - 밖에서 접근할 생성자를 제공하지 않아 final이다.
    - 따라서 싱글턴이다.
    - 타입 안전성을 제공한다. 위 코드의 Apple의 경우 세 가지 외에는 컴파일 오류가 난다. 무조건 Apple은 세가지만 쓸 수 있다.
    - 출력하기 적합하다. toString()메서드를 이용.
    - 임의의 메서드나 필드를 추가할 수 있다. 

```java
// 데이터와 메서드를 갖는 열거 타입
public enum Planet {
    MERCURY(3.302e+23, 2.439e6),
    VENUS  (4.869e+24, 6.052e6),
    EARTH  (5.975e+24, 6.378e6),
    MARS   (6.419e+23, 3.393e6),
    JUPITER(1.899e+27, 7.149e7),
    SATURN (5.685e+26, 6.027e7),
    URANUS (8.683e+25, 2.556e7),
    NEPTUNE(1.024e+26, 2.477e7);

    private final double mass;           // 질량(단위: 킬로그램)
    private final double radius;         // 반지름(단위: 미터)
    private final double surfaceGravity; // 표면중력(단위: m / s^2)

    // 중력상수(단위: m^3 / kg s^2)
    private static final double G = 6.67300E-11;

    // 생성자
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }

    public double mass()           { return mass; }
    public double radius()         { return radius; }
    public double surfaceGravity() { return surfaceGravity; }

    public double surfaceWeight(double mass) {
        return mass * surfaceGravity;  // F = ma
    }
}
```
- 열거 타입 상수 각각을 특정 데이터와 연결지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면된다.  

```java
// 책에 나온 내용을 토대로 좀 더 가독성을 좋게 만들어 본 예제
public enum Operation2 {
    PLUS("+", (x, y) -> x + y),
    MINUS("-", (x, y) -> x - y),
    TIMES("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> x / y);

    private final String symbol;
    private final BiFunction<Double, Double, Double> function;

    Operation2(String symbol, BiFunction<Double, Double, Double> function) {
        this.symbol = symbol;
        this.function = function;
    }

    public String symbol() {
        return symbol;
    }

    public double apply(double x, double y) {
        return function.apply(x, y);
    }

    @Override
    public String toString() {
        return symbol;
    }
    
    public static void main(String[] args) {
        double x = Double.parseDouble("2");
        double y = Double.parseDouble("4");
        for (Operation2 op : Operation2.values())
            System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
    }
}
```
- 이런식으로 하나의 메서드가 상수별로 다르게 동작해야 할때는 function을 람다식으로 지정하여 상수별로 메서드 구현도 가능하다.

```java
private static final Map<String, Operation2> stringToEnum = Stream.of(values()).collect(toMap(Object::toString, e -> e));
    
public static Operation2 fromString(String symbol) {
    return stringToEnum.get(symbol);
}
```
- 또한 이런식으로 지정한 symbol의 열거타입을 반환하게 하는 방법도 가능하다

```java
// 전략 열거 타입 패턴
public enum PayrollDay2 {
    MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY),
    THURSDAY(WEEKDAY), FRIDAY(WEEKDAY),
    SATURDAY(WEEKEND), SUNDAY(WEEKEND);

    private final PayrollDay.PayType payType;

    PayrollDay2(PayrollDay.PayType payType) {
        this.payType = payType;
    }

    public int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }

    private static final int MINS_PER_SHIFT = 8 * 60;

    // 전략 열거 타입
    enum PayType {
        WEEKDAY((minsWorked, payRate) -> minsWorked <= MINS_PER_SHIFT ? 0 : (minsWorked - MINS_PER_SHIFT) * payRate / 2),
        WEEKEND((minsWorked, payRate) -> minsWorked * payRate / 2);

        private final BiFunction<Integer, Integer, Integer> overtimeFunction;

        PayType(BiFunction<Integer, Integer, Integer> overtimeFunction) {
            this.overtimeFunction = overtimeFunction;
        }

        int overtimePay(int mins, int payRate) {
            return overtimeFunction.apply(mins, payRate);
        }

        int pay(int minsWorked, int payRate) {
            int basePay = minsWorked * payRate;
            return basePay + overtimePay(minsWorked, payRate);
        }
    }

    public static void main(String[] args) {
        for (PayrollDay2 day : values())
            System.out.printf("%-10s%d%n", day, day.pay(8 * 60, 1));
    }
}
```
- 이런식으로 열거 타입 상수 일부가 같은 동작을 공유한다면 전략 열거 타입 패턴을 사용하여 switch문을 쓰는 것보다 가독성을 좋게하고 오류발생 가능성을 낮출 수 있다.

<br>

## **⭐️ 아이템 35 : ordinal 메서드 대신 인스턴스 필드를 사용하라**

열거 타입은 해당 상수가 그 열거 타입에서 몇 번째 위치인지 반환하는 ordinal이라는 메서드를 제공한다.  

```java
// ordinal을 잘못 사용한 예 
public enum Ensemble0 {
    SOLO, DUET, TRIO, QUARTET, QUINTET,
    SEXTET, SEPTET, OCTET, NONET, DECTET;
    
    // 합주단 종류의 연주자 수 반환
    public int numberOfMusicians() {
        return ordinal() + 1;
    }
}
```
- 이런식으로 만들게 되면 최초에는 괜찮지만 상수선언 순서를 바꾸는 순간 numberOfMusicians 메서드가 오동작하고, 이미 사용중인 정수는 추가할 수가 없다. 예를 들면 복4중주(double quartet)은 8명이므로 추가 할 수가 없다.

```java
// 인스턴스 필드에 정수 데이터를 저장하는 열거 타입
public enum Ensemble {
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
    SEXTET(6), SEPTET(7), OCTET(8), DOUBLE_QUARTET(8),
    NONET(9), DECTET(10), TRIPLE_QUARTET(12);

    private final int numberOfMusicians;
    Ensemble(int size) { this.numberOfMusicians = size; }
    public int numberOfMusicians() { return numberOfMusicians; }
}
```

- 이러한 형태로 인스턴스 필드에 값을 저장하여 사용하도록 하자.
- ordinal은 사실 EnumSet과 EnumMap같은 자료구조에 쓸 목적으로 설계되었다고 한다.

<br>

## **⭐️ 아이템 36 : 비트 필드 대신 EnumSet을 사용하라**

열거한 값들이 주로 집합(단독X)으로 사용될 경우, 예전에는 서로 다른 2의 거듭제곱 값을 할당한 정수 열거 패턴을 사용해왔다고 한다.

```java
// 비트 필드 열거 상수 - 구닥다리 기법
public class Text0 {
    public static final int STYLE_BOLD = 1 << 0; // 1
    public static final int STYLE_ITALIC = 1 << 1; // 2
    public static final int STYLE_UNDERLINE = 1 << 2; // 4
    public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8
    
    // 매개변수 styles는 0개 이상의 STYLE_ 상수를 비트별 OR한 값이다.
    public void applyStyles(int styles) {
        System.out.printf("Applying styles %d to text%n", styles);
    }
    
    // 사용 예
    public static void main(String[] args) {
        Text0 text = new Text0();
        text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
    }
}
```
위 처럼 비트 필드 열거 상수를 정의하게 되면 아이템34처럼 정수 열거 상수의 단점을 그대로 지니고 추가로 문제를 안게 된다.
- 출력시 비트 필드 값이 그대로 출력되서 해석하기 힘들다.
- 비트 필드 하나에 녹아 있는 모든 원소를 순회하기도 까다롭다.
- 최대 몇 비트가 필요한지 API작성시 미리 예측하여 타입을 선택해야한다. 

```java
// EnumSet - 비트 필드를 대체하는 현대적 기법
public class Text {
    public enum Style {BOLD, ITALIC, UNDERLINE, STRIKETHROUGH}

    // 어떤 Set을 넘겨도 되나, EnumSet이 가장 좋다.
    public void applyStyles(Set<Style> styles) {
        System.out.printf("Applying styles %s to text%n",
                Objects.requireNonNull(styles));
    }

    // 사용 예
    public static void main(String[] args) {
        Text text = new Text();
        text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
    }
}
```
- EnumSet 클래스는 비트 필드 수준의 명료함과 성능을 제공하고 Enum을 사용할 수 있어 장점이 많다.

<br>

## **⭐️ 아이템 37 : ordinal 인덱싱 대신 EnumMap을 사용하라**

```java
// 식물이 일년생인지 다년생인지 이년생인지 나타내는 열거타입
class Plant {
    enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }

    final String name;
    final LifeCycle lifeCycle;

    Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override public String toString() {
        return name;
    }
}
```

```java
// ordinal()을 배열 인덱스로 사용
Plant[] plants = ...;
List<Plant>[] plantsByLifeCycle =  (List<Plant>[]) new List[Plant.LifeCycle.values().length];
for (int i = 0; i < plantsByLifeCycle.length; i++)
    plantsByLifeCycle[i] = new ArrayList<>();
for (Plant p : plants)
    plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);
```

- 가끔 이런식으로 ordinal로 배열의 인덱싱을 하는 경우가 있다. 
- 문제점
    1. 잘 문서화 하지 않는 한 배열이 무엇을 나타내는지 명확하지 않아 오류가 발생하기 쉽다.
    2. 유지보수 시 열거형 상수의 순서가 바뀌면 배열의 인덱싱이 에러가 날 수 있다.
    3. 타입 안정성이 없어 실수로 잘못된 배열을 사용할 수도있다.

```java
// EnumMap을 사용해 데이터와 열거 타입을 매핑
Map<Plant.LifeCycle, List<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);
for (Plant.LifeCycle lc : Plant.LifeCycle.values())
    plantsByLifeCycle.put(lc, new ArrayList<>());
for (Plant p : plants)
    plantsByLifeCycle.get(p.lifeCycle).add(p);
```
- EnumMap은 열거타입을 key로 사용하도록 설계되었다. 

- EnumMap의 장점
    - 타입 안정성이 생긴다.
    - Map의 키인 Enum이 문자열을 제공한다.
    - 인덱싱 계산에 의한 오류를 제거할 수 있다.
    - 성능도 배열의 ordinal인덱싱과 비슷하다.

```java
System.out.println(Arrays.stream(garden)
                .collect(groupingBy(p -> p.lifeCycle,
                        () -> new EnumMap<>(LifeCycle.class), toSet())));
```
- 이런식으로 스트림을 이용해 구현할 수도 있다.
- 이런 형태는 맵을 빈번하게 사용하는 상황에서 유용하게 쓰일 수 있다. 예를 들면 static으로 맵을 초기화하여 캐슁하는 전략

<br>

## **⭐️ 아이템 38 : 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라**

이전 아이템에서 설명하듯이 대부분의 상황에서 열거 타입은 타입 안전열거 패턴보다 우수하다.  
하지만 예외가 있다면 타입 안전패턴은 class라 확장할 수 있으나 열거타입은 불가능하다는 점이다.  
대부분의 상황에서 열거타입을 확장하겠다는 생각은 좋지 않지만, 특이 케이스에서 필요시에는 인터페이스를 사용하자.

```java
// 인터페이스를 이용해 확장 가능 열거타입 흉내
public interface Operation {
    String symbol();
    BiFunction<Double, Double, Double> function();

    default double apply(double x, double y) {
        return function().apply(x, y);
    }
}
```
```java
public enum BasicOperation implements Operation {
    PLUS("+", (x, y) -> x + y),
    MINUS("-", (x, y) -> x - y),
    TIMES("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> x / y);

    private final String symbol;
    private final BiFunction<Double, Double, Double> function;

    BasicOperation(String symbol, BiFunction<Double, Double, Double> function) {
        this.symbol = symbol;
        this.function = function;
    }

    @Override
    public String symbol() {
        return symbol;
    }

    @Override
    public BiFunction<Double, Double, Double> function() {
        return function;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
```
- 열거타입인 BasicOperation는 확장할 수 없지만 Operation은 확장 가능하다

```java
// 확장 가능한 열거 타입
public enum ExtendedOperation implements Operation {
    EXP("^", (x, y) -> Math.pow(x, y)),
    REMAINDER("%", (x, y) -> x % y);

    private final String symbol;
    private final BiFunction<Double, Double, Double> function;

    ExtendedOperation(String symbol, BiFunction<Double, Double, Double> function) {
        this.symbol = symbol;
        this.function = function;
    }

    @Override
    public String symbol() {
        return symbol;
    }

    @Override
    public BiFunction<Double, Double, Double> function() {
        return function;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
```
- 확장하여 지수 연산과 나머지 연산을 추가한 모습이다. 인터페이스 덕분에 확장이 가능하다.
- 이렇게 확장된 연산들은 기존 연산을 쓰던 곳이면 어디든 쓸 수 있다. Operation 인터페이스를 사용하도록 작성 되어 있기만 하다면!!


<br>

## **⭐️ 아이템 39 : 명명 패턴보다 애너테이션을 사용하라**
JUnit3까지는 테스트 메서드 이름을 test로 시작해서 짓지 않으면 테스트 메서드의 대상이 아니었다. 이러한 방식을 명명 패턴이라고 한다. Junit4 부터는 ```@Test``` 애노테이션으로 테스트의 대상을 식별한다.  
- 명명 패턴은 단점
    - 오타가 나면 안된다.
    - 올바른 프로그램 요소에서만 사용되리라 보증할 방법이 없다.
    - 프로그램 요소를 매개변수로 전달할 마땅한 방법이 없다.

스프링 부트를 사용해본 경험이 있다면 애노테이션 방식이 친숙하지만 직접 애노테이션을 만들어본 경험은 없을 수도 있다.  

책은 간단한 애노테이션의 예시들을 제공한다

```java
/**
 * 테스트 메서드임을 선언하는 애너테이션이다.
 * 매개변수 없는 정적 메서드 전용이다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}
```
- 간단한 테스트 애노테이션이다.
- 애노테이션 상위에 애노테이션을 다는 것을 메타애노테이션이라고 한다.
- @Retention(RetentionPolicy.RUNTIME)은 현재 런타임에도 유지되어야한다. 는 표시이다.
- @Target(ElementType.METHOD)는 테스트가 메서드 선언에만 사용되어야 한다는 뜻이다.
- 이처럼 아무 매개 변수 없이 단순히 대상에 마킹하는 애노테이션을 ```마킹애노테이션```이라고 한다.
```java
public class Sample {
    @Test
    public static void m1() { }        // 성공해야 한다.
    public static void m2() { }
    @Test public static void m3() {    // 실패해야 한다.
        throw new RuntimeException("실패");
    }
    public static void m4() { }  // 테스트가 아니다.
    @Test public void m5() { }   // 잘못 사용한 예: 정적 메서드가 아니다.
    public static void m6() { }
    @Test public static void m7() {    // 실패해야 한다.
        throw new RuntimeException("실패");
    }
    public static void m8() { }
}
```
- 간단한 테스트 메서드들 이다.
```java
public class RunTests {
    public static void main(String[] args) throws Exception {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName("org.example.chapter06.item39.markerannotation.Sample");
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                tests++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    System.out.println(m + " 실패: " + exc);
                } catch (Exception exc) {
                    System.out.println("잘못 사용한 @Test: " + m);
                }
            }
        }
        System.out.printf("성공: %d, 실패: %d%n",
                passed, tests - passed);
    }
}
```
- 실행하면 총 4개의 테스트 메서드들 중 성공은 1개 실패는 4개이다. 

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}
```
- 특정 예외를 던져야만 성공하는 테스트 어노테이션
- 매개변수 타입으로 ```Class<? extends Throwable>```를 가진다.

```java
// 매개변수 하나짜리 애너테이션을 사용한 프로그램
public class Sample2 {
    @ExceptionTest(ArithmeticException.class)
    public static void m1() {  // 성공해야 한다.
        int i = 0;
        i = i / i;
    }
    @ExceptionTest(ArithmeticException.class)
    public static void m2() {  // 실패해야 한다. (다른 예외 발생)
        int[] a = new int[0];
        int i = a[1];
    }
    @ExceptionTest(ArithmeticException.class)
    public static void m3() { }  // 실패해야 한다. (예외가 발생하지 않음)
}
```
- 매개변수 하나짜리 라면 위처럼 하나만 지정해주면 된다.

```java
//  배열 매개변수를 받는 애너테이션 타입
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Exception>[] value();
}
```
- 매개변수 타입으로 ```Class<? extends Exception>[]``` 배열을 가진다.
```java
// 배열 매개변수를 받는 애너테이션을 사용하는 프로그램 
public class Sample3 {

    @ExceptionTest({ IndexOutOfBoundsException.class,
                     NullPointerException.class })
    public static void doublyBad() {   // 성공해야 한다.
        List<String> list = new ArrayList<>();

        // 자바 API 명세에 따르면 다음 메서드는 IndexOutOfBoundsException이나
        // NullPointerException을 던질 수 있다.
        list.addAll(5, null);
    }
}
```
- 배열 매개변수를 받는 애너테이션은 중괄호로 묶어 사용한다.

```java
// 반복 가능한 애너테이션 타입 
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}

// 반복 가능한 애너테이션의 컨테이너 애너테이션 (244쪽)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTestContainer {
    ExceptionTest[] value();
}
```
- 배열 매개변수를 사용하는 대신 @Repeatable 메타애너테이션을 다는 방식은 하나의 프로그램에 여러번 달 수 있다.
```java
@ExceptionTest(IndexOutOfBoundsException.class)
@ExceptionTest(NullPointerException.class)
public static void doublyBad() {
    List<String> list = new ArrayList<>();

    // 자바 API 명세에 따르면 다음 메서드는 IndexOutOfBoundsException이나
    // NullPointerException을 던질 수 있다.
    list.addAll(5, null);
}
```
- 코드 가독성이 배열 매개변수보다 개선된다.
- 위의 코드 예시들을 제대로 처리하려면 자바의 애노테이션 API 문서를 참고해서 처리하면될 것이다.  
- 또한 사용법을 잘 알고 있다면 애노테이션으로 할 수 있는 일을 명명 패턴으로 처리할 필요는 없다.

<br>

## **⭐️ 아이템 40 : @Override 애너테이션을 일관되게 사용하라**

```@Override``` 애노테이션은 상위 타입의 메서드를 재정의했음을 의미한다.  

재정의 함에도 메서드 위에 @Override 애노테이션을 달지 않으면 재정의가 아니라 다중 정의(오버로딩)이므로 컴파일러가 에러를 던져줄 것이다.  

단 예외적으로 구체 클래스에서 상위 클래스의 추상메서드를 재정의할 때는 굳이 @Override를 달지 않아도 된다.  

그러나 보통 인텔리제이에서는 기본적으로 붙여준다. 달아도 해로울 것은 없다.

<br>

## **⭐️ 아이템 41 : 정의하려는 것이 타입이라면 마커 인터페이스를 사용하라**

마커 인터페이스는 메서드를 정의하지 않은 인터페이스이다. 이 인터페이스를 구현하는 클래스에 특정 속성이 있음을 나타내기에 마커인터페이스라고 불린다.  

대표적인 예시로 Serializable 와 Cloneable 이 있다.
```java
public interface Serializable {
}

public interface Cloneable {
}
```

마커 어노테이션은 아이템 39에서 말했듯 매개변수가 없다.  

대표적인 예시로 @FunctionalInterface가 있다.

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FunctionalInterface {}
```
  
마커 애노테이션이 나오면서 마커 인터페이스는 구식이 되었다는 이야기가 있는데 이는 사실이 아니다.

- 마커 인터페이스가 나은점
    - 마커 인터페이스는 이를 구현한 클래스의 인스턴스들을 구분하는 타임으로 쓸 수 있으나, 마커 애노테이션은 그렇지 않다.
    - 마커 인터페이스가 나은 점 두번째는 적용 대상을 더 정밀하게 지정할 수 있다는 것
- 마커 애노테이션이 나은점
    - 거대한 애너테이션 시스템의 지원을 받는다는 점

### ***결론***
새로 추가하는 메서드 없이 단지 타입 정의가 목적이라면 마커 인터페이스를 선택.  

클래스나 인터페이스 외의 프로그램 요소에 마킹해야 하거나, 애너테이션을 적극 활용하는 프레임 워크의 일부로 그 마커를 편입시키고자 한다면 마커 애노테이션을 선택하자.  

적용 대상이 ElementType.TYPE인 마커 애너테이션을 작성할 때는 곰곰히 생각해보고 둘 중 선택을 하자.