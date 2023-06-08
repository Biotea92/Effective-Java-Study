# 🎁 **객체의 생성과 파괴**
[예제 코드 링크](https://github.com/WegraLee/effective-java-3e-source-code)

## **⭐️ 아이템 1 : 생성자 대신 정적 팩터리 메서드를 고려하라**


- 객체를 생성할 때 전통적으로 public 생성자를 통해 객체를 생성한다. 
    - 다음과 같이 Book class가 있으면
        ```java
        public class Book {

            private String title;
            private String author;

            public Book(String title, String author) {
                this.title = title;
                this.author = author;
            }
        }
        ```
    - 클라이언트는 public 생성자를 이용해 객체를 생성한다. 
        ```java
        Book book = new Book("effective java", "Joshua Bloch");
        ```
- 만약 Book class를 정적 팩터리 메서드를 이용해 객체를 생성하면
    - private 생성자를 통해 정적 팩터리 메서드를 만든다.
        ```java
        public class Book {

            private String title;
            private String author;

            private Book(String title, String author) {
                this.title = title;
                this.author = author;
            }

            public static Book createWithTitleAndAuthor(String title, String author) {
                return new Book(title, author);
            }
        }
        ```
    - 클라이언트는 정적 팩터리 메서드를 이용해 객체를 생성한다. 
        ```java
        Book book = Book.createWithTitleAndAuthor("effective java", "Joshua Bloch");
        ```

<br>

### **📎 정적 팩터리 메서드의 장점**

1. 이름을 가질 수 있다. 
    - 위의 Book 정적 팩터리 메서드처럼 객체 생성시 "Book을 제목과 작가와 함께 만든다"라고 명시적으로 메서드명을 지을 수 있다. 
    - public 생성자를 통해 만들게 되면 어떤 목적으로 객체를 생성하는지 알 수 없다. 
    - 책에서는 BigInteger class를 예시로 들고 있다. 
        ```java
        BigInteger primeNumber1 = new BigInteger(100, new Random());
        BigInteger primeNumber2 = BigInteger.probablePrime(100, new Random());
        ```
    - 둘다 소수를 반환 하지만 정적팩터리 메서드가 소수를 반환한다는 뜻을 더 잘 설명하고 있다. 
    - 또한 public 생성자를 통해 만드는 BigInteger는 api를 잘모르는 상태에서 소수를 반환하는 객체를 만들기 위해 직접 찾기가 힘들다.

<br>

2. 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다.
    - ```Boolean.valueOf(boolean)``` 메서드를 보면 객체 자체를 아예 생성하지 않는다. 
        ```java
        public static final Boolean TRUE = new Boolean(true);

        public static final Boolean FALSE = new Boolean(false);

        // 중략

        @HotSpotIntrinsicCandidate
        public static Boolean valueOf(boolean b) {
            return (b ? TRUE : FALSE);
        }
        ```
    - Boolean class에서는 단 한 번의 객체를 생성하고 값을 반환한다.
    - 플라이웨이트 패턴도 이와 비슷한 기법이다.
        > **플라이 웨이트 패턴**  
        > 인스턴스를 가능한 한 공유해서 사용함으로써 메모리를 절약하는 패턴이다. 즉, 변하지 않는 공통 부분을 ```Flyweight``` 란 클래스로 분리하고 ```FlyweightFactory``` 에서 기존에 저장해둔 ```Flyweight``` 인스턴스가 있다면, 새로 생성하지 않고 기존 인스턴스를 반환하여 공유하는 형식이다.
    - singleton으로 만들어 단 하나의 인스턴스만을 보장할 수 있다.
        ```java
        public class DataSource {
            private String url;
            private String username;
            private String password;

            private DataSource() {
            }

            private static final DataSource INSTANCE = new DataSource();
            
            public static DataSource getInstance() {
                return INSTANCE;
            }
        }
        ```
    - 클래스를 인스턴스화 불가로 만들 수 있다. ```private``` 으로 생성자를 막는다.
    - 불변 값 클래스에서 동일 인스턴스임을 보장할 수 있다.
    - 열거타입은 인스턴스를 하나만 만들어짐을 보장한다.

<br>

3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.
    - 반환할 객체의 클래스를 자유롭게 선택할 수 있는 '엄청난 유연성'을 선물한다.
    - Collection framework처럼 구현 클래스를 공개하지 않고도 그 객체의 반환할 수 있다. 이로 인해 API를 만들 때 API를 작게 유지 할 수 있다.
    - 해당 유연성은 인터페이스를 정적 팩터리 메서드의 반환 타입으로 사용하는 인터페이스 기반 프레임워크를 만드는 핵심 기술이다.
        > 자바8 이전 인터페이스에 정적 메서드를 선언할 수 없으므로 인스턴스화가 불가한 동반 클래스를 두어 내부에 선언 ```UnmodifiableList``` -> ```Collection```에 정의해둠
        >> 내부 클래스여서 직접적으로 구현체를 들여다보지 않아도 되며, 얻은 객체를 인터페이스만으로 다룰 수 있게 되었다.  
        결론적으로 컬렉션 프레임워크는 이러한 특성을 통해 해당 45개의 클래스를 공개하지 않고도 정적 메서드를 통해서 사용자들에게 제공하였기 때문에 API의 외견을 훨씬 작게 만들 수 있었다.

        > 자바8 이후 인터페이스의 정적 메소드(static)가 가능해졌기 때문에, 단순히 public 정적 멤버들을 인터페이스 내부에 두면 된다. 
        >> 참고로 자바 9는 private 정적 메서드는 허락하지만, 필드와 멤버 클래스는 여전히 public 만 가능하여 별도의 package-private 클래스에 두어야 할 수도 있다.

4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다. 
    - ```반환 타입이 하위 타입```이기만 하면 어떤 클래스의 객체를 반환하든 상관없다. ```EnumSet``` 클래스는 public 생성자 없이 오직 정적 팩터리 메서드만 제공하는데 원소의 수에 따라 두 가지 하위의 클래스 중 하나의 인스턴스를 반환한다.
        ```java
        public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
            Enum<?>[] universe = getUniverse(elementType);
            if (universe == null)
                throw new ClassCastException(elementType + " not an enum");

            // 원소수 64개 이하 일때 RegularEnumSet
            if (universe.length <= 64)
                return new RegularEnumSet<>(elementType, universe);
            // 원소수 65개 이상 일때 JumboEnumSet
            else
                return new JumboEnumSet<>(elementType, universe);
        }
        ``` 

5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다. 
    - 이런 유연함은 서비스 제공자 프레임 워크를 만드는 근간이 된다. 대표적으로 ```JDBC```가 있다.
        > ```Connection```이 서비스 인터페이스 역할을,  
        > ```DriverManager.registerDriver```이 제공자 등록 API 역할을,  
        > ```DriverManager.getConnection```이 접근 API 역할을,  
        > ```Driver```가 서비스 제공자 인터페이스 역할을 수행한다.

<br>

### **📎 정적 팩터리 메서드의 단점**
1. 상속을 하려면 public이나 protected 생성자가 필요하니 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다. 
2. 정적 팩터리 메서드는 프로그래서가 찾기 어렵다.  
생성자처럼 api 설명에 명확히 드러나지 않는다. 메서드 이름을 널리 알려진 규약을 따라 짓는 식으로 문제를 어느정도는 해결할 수 있다. 

 <br>

### **📎 정적 팩터리 메서드의 명명 규칙**

1. ```from``` : 매개변수 하나 받아 해당 타입의 인스턴스 반환하는 형변환 메서드
    ```java
    Date d = Date.from(instant);
    ```
2. ```of``` : 여러 매개변수 받아 적합한 타입의 인스턴스를 반환하는 집계 메서드
    ```java
    Set<Rank> faceCards = EnumSet.of(JACK, QUEEN, KING);
    ```
3. ```valueOf``` : from 과 of 의 더 자세한 버전
    ```java
    BigInteger prime = BigInteger.valueOf(Integer.MAX_VALUE);
    ```
4. ```instance``` / ```getInstance``` : 매개변수로 명시한 인스턴스를 반환하지만, 같은 인스턴스 보장하지 않는다.
    ```
    StackWalker luke = StackWalker.getInstance(options);
    ```
5. ```create``` / ```newInstance``` : 매번 새로운 인스턴스를 생성해 반환함을 보장한다.
    ```java
    Object newArray = Array.newInstance(classObject, arrayLen);
    ```
6. ```getType``` : getInstance 와 같으나, 생성 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할때 사용한다.
    ```java
    FileStore fs = Files.getFileStore(path)
    ```
7. ```newType``` : newInstance 와 같으나, 생성 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할때 사용한다.
    ```java
    BufferedReader br = Files.newBufferedReader(path);
    ```
8. ```type``` : getType / newType 간결 버전
    ```java
    List<Complaint> litany = Collections.list(legacyLitany);
    ```