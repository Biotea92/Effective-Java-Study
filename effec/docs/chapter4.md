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


## **⭐️ 아이템 17 : public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라**