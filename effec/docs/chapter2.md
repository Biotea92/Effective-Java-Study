# **객체 생성과 파괴**
  1.객체를 만들어야 할 때와 만들지 말아야 할 때를 구분하는 법   
  2.올바른 객체 생성 방법과 불필요한 생성을 피하는 방법  
  3.제때 파괴됨을 보장하고 파괴 전에 수행해야 할 정리 작업을 관리하는 요령

---
## 📌**아이템 1: 생성자 대신 정적 팩터리 메서드를 고려하라**

### 🔗 **클래스의 인스턴스 얻는 방법**  
- public 생산자를 new로 호출하여 인스턴스 얻기
    ```java
    public class Car {

      private final String name;
      private final int oil;

      public Car(String name, int oil) {
        this.name = name;
        this.oil = oil;
        }
    }

    public static void main(String[] args){
      Car car = new Car("fullOilcar",10);
    }
    ```
- 정적 팩터리 메서드 (static factory method) 이용해서 인스턴스 얻기
    ```java
    public class Car{

      private final String name; 
      private final int oil;

      public static Car createCar(String name, int oil){
        return new Car(name,oil);
      }

      private Car(String name, int oil){
        this.name = name;
        this.oil = oil;
      }
    }

    public static void main(String[] args){
      Car car = Car.createCar("car1",10);
    }
    ``` 

### 🔗 **정적 팩터리 메서드 장점**
- 이름을 가질 수 있다.
- 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다.
- 반환타입의 하위 타입 객체를 반환할 수 있다.
- 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
- 정적팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

### 🔗 **정적 팩터리 메서드의 단점**
- 상속하려면 public 또는 protected 생성자가 필요하니 정적팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다.
- 정적팩터리 메서드는 프로그래머가 찾기 어렵다.  
  but!! API 문서 잘 쓰고, 널리 알려진 규약을 따라 메서드 이름을 짓는다면 OK!!  

### 🔗 **정적 팩터리 메서드의 명명 방식**
- from
- of
- valueOf
- instance 혹은 getInstance
- create 혹은 newInstance
- getType
- newType
- type




---
## 📌**아이템 2: 생성자에 매개변수가 많다면 빌더를 고려하라**

## 📌**아이템 3: private 생성자나 열거타입으로 싱글턴임을 보증하라**

## 📌**아이템 4: 인스턴스화를 막으려거든 private 생성자를 사용하라**

## 📌**아이템 5: 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라**

## 📌**아이템 6: 불필요한 객체 생성을 피하라**

## 📌**아이템 7: 다 쓴 객체 참조를 해제하라**

## 📌**아이템 8: finalizer와 cleaner사용을 피하라**

## 📌**아이템 9: try-finally 보다는 try-with-resources를 사용하라**
