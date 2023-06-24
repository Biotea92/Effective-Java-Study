# 🎁 **클래스와 인터페이스**

<br>

## **⭐️ 아이템 15 : 클래스와 멤버의 접근 권한을 최소화하라**

잘 설계된 컴포넌트는 클래스 내부 데이터와 내부 구현 정보를 외부 컴포넌트로부터 잘 숨긴다. 이를 객체지향에서는 ***정보은닉***, ***캡슐화***라고 부른다. 

### ***정보 은닉의 장점***
- 시스템 개발 속도를 높인다.
- 시스템 관리 비용을 낮춘다.
- 성능 최적화에 도움을 준다.
- 소프트웨어 재사용성을 높인다.
- 큰 시스템을 제작하는 난이도를 낮춰준다.  

### ***자바에서 제공되는 접근 제한자의 활용*** 
- 모든 클래스와 멤버의 접근성을 가능한 한 좁히자. 

- 톱레벨 클래스와 인터페이스에 부여할 수 있는 접근 수준 
    - package-private : 해당 패키지 안에서만 사용가능
    - public : 어디서든 사용가능
    - 패키지 외부에서 이용할 일이 없으면 package-private으로 선언하자. 
    - 한 클래스 내부에서만 이용한다면 클래스 안에 private static으로 중첩시키자.
        ```java
        public class ExtraClass {

            public static void main(String[] args) {
                String hello = InnerClass.hello;
            }
            
            // ExtraClass에서만 사용가능
            private static class InnerClass {
                private static final String hello = "hello";
            }
        }
        ```
- 멤버(필드, 메서드, 중첩 클래스, 중첩 인터페이스)에 부여할 수 있는 접근 수준
    - private: 멤버를 선언한 톱레벨 클래스에서만 접근 가능
    - package-private: 멤버가 소속된 패키지 안의 모든 클래스에서 접근 가능(default)
    - protected: default의 접근 범위를 포함하여 이 멤버를 선언한 클래스의 하위 클래스에서도 접근 가능
    - public: 모든 곳에서 접근 가능
    - 클래스의 모든 멤버는 공개 API 외에는 모두 private으로 만들자. 패키지내에서 필요하면 package-private으로 풀어주자
    - public 클래스의 인스턴스 필드는 되도록 public이 아니어야 한다.

### ***결론***
접근제어자는 가능한 한 좁게 설정하라 api가 필요하면 ```public```으로 그외에는 공개되어서는 안된다. 필드는 상수로 제공되는 ```public static final```을 제외하고 절대 ```public```으로 공개하지 말자.

<br>

## **⭐️ 아이템 16 : public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라**

public class는 필드들을 절대 public으로 직접 노출하지 말자.  
package-private class나 private 중첩 class라면 필드를 노출해야 할 때도 있다.  

결론 : 객체지향의 원칙을 잘지키자.

<br>

## **⭐️ 아이템 17 : 변경 가능성을 최소화하라**
가변 클래스보다 불변 클래스를 이용하자  
불변 클래스는 설계하고 구현하고 사용하기 쉽다. 오류가 생길 여지도 적어진다.

### _불변 클래스를 만드는 다섯가지 규칙_
1. 객체의 상태를 변경하는 메서드를 제공하지 않는다.
2. 클래스를 확장할 수 없도록 한다.
3. 모든 필드를 final로 선언한다.
4. 모든 필드를 private으로 선언한다.
5. 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다. 

### _불변 객체의 특징_
- 단순하다. 
- 근본적으로 스레드 안전하여 따로 동기화할 필요 없다. 
    - 여러 스레드가 동시 사용해도 훼손되지 않기 때문에 안심하고 공유할 수 있다. 
    - 따라서 불변 클래스는 최대한 인스턴스를 재활용하기를 권장하고, 자주 쓰이는 값은 상수로 제공, 나아가 캐싱하여 인스턴스를 중복으로 생성하지 않게 할 수 있다. 
    - Integer Class의 127과 128의 예시로 재밌는 점을 알 수 있다.
        ```java
        @Test
        void test() {
            Integer num1 = Integer.valueOf(127);
            Integer num2 = Integer.valueOf(127);

            assertTrue(num1 == num2);

            Integer num3 = Integer.valueOf(128);
            Integer num4 = Integer.valueOf(128);

            assertFalse(num3 == num4);
        }
        ```
- 불변 객체는 자유롭게 공유할 수 있음은 물론, 불변 객체끼리는 내부 데이터를 공유할 수 있다.
- 객체를 만들 때 다른 불변 객체들을 구성요소로 사용하면 이점이 많다.
- 불변 객체는 그 자체로 실패 원자성을 제공한다.
- 불변 클래스의 단점으로 값이 다르면 반드시 독립된 객체로 만들어야 한다. 

### _불변 클래스를 만드는 다른 방법_
자신을 상속하지 못하게 하는 방법으로 class를 final로 선언하는 방법이 있지만 더 유연한 방법은 모든 생성자를 private또는 default로 만들고 Public 정적 팩터리 메서드를 제공하는 것이다. 

<br>

## **⭐️ 아이템 18 : 상속보다는 컴포지션을 사용하라**

상속은 코드를 재사용하는 강력한 수단이지만, 동시에 잘못 사용하면 오류를 내기 쉽다.  
가장 큰 문제는 상속은 캡슐화를 해친다.

### _상속의 문제점_
메서드 호출과 달리 상속은 캡슐화를 깨뜨린다. 다르게 말하면 상위 클래스가 어떻게 구현되느냐에 따라 하위 클래스의 동작에 이상이 생길 수 있다.  
- 초기 구현에서는 문제가 되지 않을 수 있으나 다음 릴리즈에서 내부 구현이 달라질 수 있고 그 이유로 하위 클래스에서 오작동을 일으킬 수 있다.  
- 또한 다음 릴리즈에 상위 클래스에 새로운 메서드가 추가되면 모든 하위 클래스에서 문제를 일으키지 않도록 메서드를 재정의 해줘야한다.

### _컴포지션_
기존 클래스를 상속하는 대신 새로운 클래스에서 private 필드로 기존 클래스의 인스턴스를 참조하는 방식 

<br>

## **⭐️ 아이템 19 : 상속을 고려해 설계하고 문서화하라 그러지 않았다면 상속을 금지하라**

- 상속용 클래스를 사용하기로 마음먹었다면 메서드를 재정의하면 어떤일이 일어나는지 꼭 문서를 남겨야한다. (java doc 생성)  
- 문서만 남기는 것이 능사는 아니다. 효율적으로 하위클래스를 만들게 하기 위해 클래스의 내부 동작 과정 중간에 끼어들 수 있는 hook을 잘 선별하여 protected메서드 형태로 공개해야 할 수도 있다. 
- 상속용 클래스를 시험하기 위해 꼭 하위 클래스를 만들어 시험해 보자. 3개 정도가 적당하며 그 중 한개는 다른 사람이 만들어 봐야 한다.
- 상속용 클래스의 생성자는 절대 재정의 가능한 메서드를 호출해서는 안된다. 마찬가지로 clone메서드나 readObject메서드도 생성자와 비슷한 효과를 내므로 재정의 가능한 메서드를 호출하지 말자.

### _결론_
상속용으로 만든 클래스가 아니면 상속을 금지하자. 

<br>

## **⭐️ 아이템 20 : 추상 클래스보다는 인터페이스를 우선하라**
java에서 제공되는 다중 구현 메커니즘에는 abstract class와 interface가 있다.  
자바8 이후로 인터페이스도 디폴트 메서드를 제공하여 abstract class와 interface 모두 메서드를 구현 형태로 제공할 수 있다.  
큰 차이는 단일 상속 여부 차이이다. 
- 추상클래스 예시 
  ```java
  public abstract class Example {

    public void print() {
        System.out.println("test");
    }

    public abstract void printName();

    public void decoratePrintName() {
        System.out.println("**");
        printName();
        System.out.println("**");
    }

    public static void main(String[] args) {
        Example example = new Example() {
            @Override
            public void printName() {
                System.out.println("test");
            }
        };
        example.decoratePrintName();
    }
  }
  ```
- 인터페이스 예시
  ```java
  public interface ExampleInterface {

    void printName();

    default void decoratePrintName() {
        System.out.println("**");
        printName();
        System.out.println("**");
    }

    public static void main(String[] args) {
        ExampleInterface example = new ExampleInterface() {
            @Override
            public void printName() {
                System.out.println("test");
            }
        };
        example.decoratePrintName();
    }
  }
  ```

### _인터페이스의 장점_
- 인터페이스는 믹스인 정의에 안성맞춤이다.
    - 대상 타입의 주된 기능을 혼합해서 사용할 수 있다.
- 인터페이스로는 계층구조가 없는 타입 프레임 워크를 만들 수 있다.  

인터페이스와 추상 골격 구현 클래스를 함께 제공하는 방법으로 인터페이스와 추상 클래스의 장점을 모두 취하는 방법도 있다. 대표적인 예시가 템플릿 메서드 패턴이다.  

또한 '가능한 한' 인터페이스의 디폴트 메서드를 제공하여 개발자가 구현하는 수고로움을 덜어주고 복잡한 인터페이스라면 골격 구현 클래스를 추상 클래스로 제공하자.

<br>

## **⭐️ 아이템 21 : 인터페이스는 구현하는 쪽을 생각해 설계하라**

앞선 아이템에서도 나오지만 자바8 이후에는 인터페이스에 default메서드를 쓸 수 있다.  

기본적인 인터페이스의 메서드는 구현하는쪽에서 메서드를 정의하기에 상관없지만 default 메서드는 구현하는 class에서 기대와 다르게 이상현상을 일으킬 수 있다.  

따라서 인터페이스에 디폴트 메서드를 만들때는 항상 구현체 class에서도 정상적으로 작동할 수 있도록 세심하게 만들어야한다.  

<br>

## **⭐️ 아이템 22 : 인터페이스는 타입을 정의하는 용도로만 사용하라**

인터페이스는 구현한 클래스의 인스턴스를 참조할 수 있는 타입 역할을 한다.  

메서드 이름 반환타입 파라미터 등 만 정의 하는 용도로만 사용 해야한다.  

특히 상수 공개용 수단으로 인터페이스를 사용하지 말자. Enum이나 상수 유틸리티 클래스를 이용하는게 좋을 것이다.

<br>

## **⭐️ 아이템 23 : 태그 달린 클래스보다는 클래스 계층구조를 활용하라**

Enum 타입 선언, 태그 필드 등으로 태그 달린 클래스는 사용하지 않는 것이 좋다.  
- 장황하고, 오류를 내기 쉽고, 비효율적이다.
- 태그 달린 클래스의 예시 
    ```java
    class Figure {
        enum Shape { RECTANGLE, CIRCLE };

        // 태그 필드 - 현재 모양을 나타낸다.
        final Shape shape;

        // 다음 필드들은 모양이 사각형(RECTANGLE)일 때만 쓰인다.
        double length;
        double width;

        // 다음 필드는 모양이 원(CIRCLE)일 때만 쓰인다.
        double radius;

        // 원용 생성자
        Figure(double radius) {
            shape = Shape.CIRCLE;
            this.radius = radius;
        }

        // 사각형용 생성자
        Figure(double length, double width) {
            shape = Shape.RECTANGLE;
            this.length = length;
            this.width = width;
        }

        double area() {
            switch(shape) {
                case RECTANGLE:
                    return length * width;
                case CIRCLE:
                    return Math.PI * (radius * radius);
                default:
                    throw new AssertionError(shape);
            }
        }
    }
    ```
태그 달린 클래스보다는 클래스 계층구조를 이용하자.
- 책에서는 태그 달린 클래스를 클래스 계층구조를 어설프게 따라한 아류로 표현한다.
- 위 예시를 클래스 계층구조로 바꾼 예시
```java
abstract class Figure {
    abstract double area();
}

class Circle extends Figure {
    final double radius;

    Circle(double radius) { this.radius = radius; }

    @Override double area() { return Math.PI * (radius * radius); }
}

class Rectangle extends Figure {
    final double length;
    final double width;

    Rectangle(double length, double width) {
        this.length = length;
        this.width  = width;
    }
    @Override double area() { return length * width; }
}

// 정사각형 
class Square extends Rectangle {
    Square(double side) {
        super(side, side);
    }
}
```

<br>

## **⭐️ 아이템 24 : 멤버 클래스는 되도록 static으로 만들라**

중첩 클래스에는 네 가지 종류가 있다.  
- 정적 멤버 클래스
- (비정적) 멤버 클래스
- 익명 클래스
- 지역 클래스

### _정적 멤버 클래스_
- static class를 이용하면 outClass의 인스턴스에 대한 참조 없이도 innerClass를 만들 수 있다.
```java
public class OuterClass {
    private final String name;

    public OuterClass(String name) {
        this.name = name;
    }

    static class InnerClass {
        public void printName(OuterClass outerClass) {
            System.out.println(outerClass.name);
        }
    }

    public static void main(String[] args) {
        OuterClass outerClass = new OuterClass("test");
        InnerClass innerClass = new InnerClass();
        innerClass.printName(outerClass);
    }
}
```

### _비정적 멤버 클래스_
- static이 없는 멤버 클래스는 outClass의 인스턴스의 참조가 필요하다.
```java
public class NoneStaticInnerClass {
    private final String name;

    public NoneStaticInnerClass(String name) {
        this.name = name;
    }

    private class InnerClass {
        public void printName() {
            System.out.println(name);
        }
    }

    public static void main(String[] args) {
        NoneStaticInnerClass outerClass = new NoneStaticInnerClass("test");
        InnerClass innerClass = outerClass.new InnerClass();
        innerClass.printName();
    }
}
```
- 멤버 클래스에서 바깥 인스턴스에 접근할 일이 없다면 무조건 static을 붙여서 멤버 클래스를 만들자
    - static을 생략하면 바깥 인스턴스로의 숨은 외부 참조를 갖게 된다.
    - 이 참조를 저장하려면 시간과 공간이 소비된다.
    - 더 심각한 문제는 가비지 컬렉션이 바깥 클래스의 인스턴스를 수거하지 못하는 메모리 누수가 생길 수 있다는 것이다.
    
### _익명 클래스_
- 익명 클래스는 말 그대로 이름이 없는 클래스이다. 
- 쓰이는 동시에 만들어진다.
```java
public interface NoNameClass {
    void printName();

    public static void main(String[] args) {
        NoNameClass noNameClass = new NoNameClass() {
            @Override
            public void printName() {
                System.out.println("test");
            }
        };
        noNameClass.printName();
    }
}
```
- 자바가 람다식을 지원한 이후로는 익명클래스를 람다로 주로 사용한다. 
```java
public interface NoNameClass {
    void printName();

    public static void main(String[] args) {
        NoNameClass noNameClass = () -> System.out.println("test");
        noNameClass.printName();
    }
}
```
### _지역 클래스_
- 지역 클래스는 네 가지 중첩 클래스 중 가장 드물게 사용된다. 
- 지역변수를 선언할 수 있는 곳이면 어디든지 선언 가능하고, 유효 범위도 지역변수와 같다. 
-  
```java
public class LocalClass {

    private final String name;

    public LocalClass(String name) {
        this.name = name;
    }

    public void printName() {
        class InnerClass {
            public void printName() {
                System.out.println(name);
            }
        }

        InnerClass innerClass = new InnerClass();
        innerClass.printName();
    }

    public static void main(String[] args) {
        LocalClass localClass = new LocalClass("test");
        localClass.printName();
    }
}
```

<br>

## **⭐️ 아이템 25 : 톱레벨 클래스는 한 파일에 하나만 담으라**

- java는 소스파일 하나에 톱레벨 클래스 여러개를 사용해도 불평하지 않는다.
- 하지만 어느 것을 사용할지는 어떤 소스파일을 컴파일 하냐에 따라 달라진다.
```java
// 코드 25-1 두 클래스가 한 파일(Utensil.java)에 정의되었다. - 따라 하지 말 것! (150쪽)
class Utensil {
    static final String NAME = "pan";
}

class Dessert {
    static final String NAME = "cake";
}
```
- 굳이 여러 톱레벨 클래스를 한파일에 담고 싶으면 정적멤버클래스를 이용해라.  
- 그 외에는 무조건 소스파일 하나에는 반드시 하나의 톱레벨 클래스만 담아라.