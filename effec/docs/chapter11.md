# 🎁 **동시성**

자바는 기본적으로 동시성 프로그래밍을 지원한다.  
동시성 프로그래밍은 여러 스레드가 동시에 실행되는 프로그래밍이다.  
동시성 프로그래밍을 잘못하면 응답 불가, 안전 실패, 결과 불일치 같은 문제가 발생할 수 있다.  
이번 장에서는 동시성 프로그래밍을 올바르게 하는 방법을 다룬다.

<br>

## **⭐️ 아이템 78 : 공유 중인 가변 데이터는 동기화해 사용하라**

```synchronized``` 키워드를 사용하면 동기화를 할 수 있다. 해당 메서드나 블록을 한번에 한 스레드씩 수행하도록 보장 한다.  
하지만 프로그래머들은 한 스레드가 변경하는 중이라서 상태가 일관되지 않은 순간의 객체를 다른 스레드가 보지 못하게 막는 용도로만 생각한다.  
쉽게 말해 객체를 하나의 일관된 상태에서 다른 일관된 상태로 변화시킨다.  
동기화를 제대로 사용하면 어떤 메서드도 이 객체의 상태가 일관되지 않은 순간을 볼 수 없을 것이다.  

맞는 설명이지만 동기화에는 중요한 기능이 하나 더 있다. 동기화 없이는 한 스레드가 만든 변화를 다른 스레드에서 확인하지 못할 수 있다.  
동기화는 일관성이 깨진 상태를 볼 수 없게 하는 것은 물론, 동기화된 메서드나 블록에 들어간 스레드가 같은 락의 보호하에 수행된 모든 이전 수정의 최종 결과를 보게 해준다.

```long```과 ```double``` 외의 변수를 읽고 쓰는 동작은 ```원자적(atomic)```이다. 이 말을 듣고 "성능을 높이려면 원자적 데이터를 읽고 쓸 때는 동기화하지 말아야겠다"고 생각하기 쉬운데, 아주 위험한 발상이다.

**동기화는 배타적 실행뿐 아니라 스레드 사이의 안정적인 통신에 꼭 필요하다.**

공유 중인 가변 데이터를 비록 원자적으로 읽고 쓸 수 있을지라도 동기화에 실패하면 처참한 결과로 이어질 수 있다. 

```java
// 잘못된 코드 - 이 프로그램은 얼마나 오래 실행될까?
public class StopThread {
    private static boolean stopRequested;

    public static void main(String[] args) throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested)
                i++;
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;
    }
}
```
위 코드는 1초 뒤에 종료되는 프로그램을 의도하였지만 전혀 그렇지 않고 계속 실행된다.  
원인은 동기화에 있다. 동기화하지 않으면 메인 스레드가 수정한 값을 백그라운드 스레드가 언제쯤에나 보게 될지 보증할 수 없다.  
동기화가 빠지면 마치 아래의 코드와 같이 가상 머신이 최적화를 수행 할 수도 있다.

```java
// 원래 코드
while (!stopRequested)
    i++;

// 최적화한 코드
if (!stopRequested)
    while (true)
        i++;
```
위 코드는 OpenJDK 서버 VM이 실제로 적용하는 끌어올리기(hoisting)라는 최적화 기법이다.  
stopRequested 필드를 동기화해 접근하면 이 문제를 해결할 수 있다.

```java
// 적절히 동기화해 스레드가 정상 종료한다.
public class StopThread {
    private static boolean stopRequested;

    private static synchronized void requestStop() {
        stopRequested = true;
    }

    private static synchronized boolean stopRequested() {
        return stopRequested;
    }

    public static void main(String[] args)
            throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested())
                i++;
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        requestStop();
    }
}  
```
requestStop 메서드와 stopRequested 메서드를 모두 동기화 했다.  
쓰기와 읽기 모두가 동기화 되지 않으면 동작을 보장하지 않는다.  

또한 위 코드보다 더 빠른 대안은 stopRequested 필드를 volatile로 선언하는 것이다.  
volatile으로 선언하면 동기화를 생략해도 된다.  
volatile 필드는 항상 가장 최근에 기록된 값을 읽게 해준다.

```java
// volatile 필드를 사용해 스레드가 정상 종료한다.
public class StopThread {
    private static volatile boolean stopRequested;

    public static void main(String[] args)
            throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested)
                i++;
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;
    }
}
```

하지만 volitile은 주의해서 사용해야 한다. 

```java
// 잘못된 코드 - 동기화가 필요하다.
private static volatile int nextSerialNumber = 0;

public static int generateSerialNumber() {
    return nextSerialNumber++;
}
```
위 코드는 일련번호를 하나씩 증가시키면서 반환할 것 같지만 그렇지 않다.  
증가 연산자(++)는 원자적이지 않기 때문이다.  
증가 연산자는 먼저 변수 값을 읽고, 1 증가시키고, 그리고 그 변수에 새로운 값을 저장한다.  
증가 연산자를 사용하면 3개의 동작이 하나로 합쳐지지만, volatile 변수에 대한 단일 연산이 아니기 때문에 스레드 안전하지 않다.  
이런 오류를 안전 실패(safety failure)라고 한다.  

generateSerialNumber 메서드에 synchronized 한정자를 붙이면 안전 실패를 막을 수 있다.  
메서드에 synchronized를 붙이면 nextSerialNumber에는 volatile를 제거해야 한다.

```java.util.concurrent.atomic``` 패키지의 AtomicLong을 사용하면 락 없이도(lock-free) 스레드 안전 프로그래밍을 할 수 있다.  
거기다 성능도 동기화 버전보다 좋다.

```java
// java.util.concurrent.atomic을 사용한 락-프리 동기화 
private static final AtomicLong nextSerialNum = new AtomicLong();
public static long generateSerialNumber() {
    return nextSerialNum.getAndIncrement();
}
```

사실 이번 아이템에서 언급된 문제들을 피하는 가장 좋은 방법은 애초에 가변 데이터를 공유하지 않는 것이다.  
불변 데이터만 공유하거나 아무것도 공유하지말자.  
다시 말해 가변 데이터는 단일 스레드에서만 쓰도록 하자.

<br>

## **⭐️ 아이템 79 : 과도한 동기화는 피하라**
과도한 동기화는 성능을 떨어뜨리고, 교착상태에 빠뜨리고, 심지어는 예측할 수 없는 동작을 낳기도 한다.  

응답 불가와 안전 실패를 피하려면 동기화 메서드나 동기화 블록 안에서느 제어를 절대로 클라이언트에 양도하면 안된다.  
- 동기화된 영역 안에서는 재정의할 수 있는 메서드는 호출하면 안되며, 클라이언트가 넘겨준 함수 객체를 호출해서도 안 된다.  
- 동기화된 영역을 포함한 클래스 관점에서는 이런 메서드는 모두 바깥 세상에서 온 외계인이다. 
- 그 메서드가 무슨일을 할지 알지 못하며 통제할 수도 없다.

```java
// 잘못된 코드. 동기화 블록 안에서 외계인 메서드를 호출한다.
public class ObservableSet<E> extends ForwardingSet<E> {
    public ObservableSet(Set<E> set) {
        super(set);
    }

    private final List<SetObserver<E>> observers = new ArrayList<>();

    public void addObserver(SetObserver<E> observer) {
        synchronized(observers) {
            observers.add(observer);
        }
    }

    public boolean removeObserver(SetObserver<E> observer) {
        synchronized(observers) {
            return observers.remove(observer);
        }
    }

    private void notifyElementAdded(E element) {
        synchronized(observers) {
            for (SetObserver<E> observer : observers)
                observer.added(this, element);
        }
    }

    @Override
    public boolean add(E element) {
        boolean added = super.add(element);
        if (added)
            notifyElementAdded(element);
        return added;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean result = false;
        for (E element : c)
            result |= add(element);  // notifyElementAdded를 호출한다.
        return result;
    }
}
```

위 코드는 Set에 원소가 추가되면 알림을 받을 수 있는 관찰자 패턴이다.(옵저버 패턴)  
관찰자들은 addObserver와 removeObserver메서드를 호출해 구독을 신청하거나 해지한다. 두 경우 모두 다음 콜백 인터페이스의 인스턴스를 메서드에 건넨다.

```java
// 집합 관찰자 콜백 인터페이스 
@FunctionalInterface
public interface SetObserver<E> {
    // ObservableSet에 원소가 더해지면 호출된다.
    void added(ObservableSet<E> set, E element);
}
```

```java
// ObservableSet 동작 확인 #1 - 0부터 99까지 출력한다.
public class Test1 {
    public static void main(String[] args) {
        ObservableSet<Integer> set = new ObservableSet<>(new HashSet<>());

        set.addObserver((s, e) -> System.out.println(e));

        for (int i = 0; i < 100; i++)
            set.add(i);
    }
}
```
위 코드를 실행해보면 99까지 잘 출력한다.  
하지만 다음 코드를 실행해보면 어떻게 될까?

```java
// ObservableSet 동작 확인 #2 - 정숫값이 23이면 자신의 구독을 해지한다.
public class Test2 {
    public static void main(String[] args) {
        ObservableSet<Integer> set = new ObservableSet<>(new HashSet<>());

        set.addObserver(new SetObserver<>() {
            public void added(ObservableSet<Integer> s, Integer e) {
                System.out.println(e);
                if (e == 23) // 값이 23이면 자신을 구독해지한다.
                    s.removeObserver(this);
            }
        });

        for (int i = 0; i < 100; i++)
            set.add(i);
    }
}
```
위 코드는 23까지 출력한 후 관찰자 자신을 구독해지한 다음 조용히 종료되어야 한다.  
하지만 실제로는 ```ConcurrentModificationException```을 던진다.  
관찰자의 added 메서드 호출이 일어난 시점이 notifyElementAdded가 관찰자들의 리스트를 순회하는 도중이기 때문이다.  
풀어서 설명하면 
- added메서드는 ObservableSet의 removeObserver 메서드를 호출하고, 이메서드는 다시 observers.remove 메서드를 호출한다.
- 여기서 문제가 발생하는데 리스트에서 원소를 제거하려 하는데, 마침 지금은 이 리스트를 순회하는 도중이다. 즉 허용되지 않은 동작이다.
- notifyElementAdded 메서드에서 수행하는 순회는 동기화 블록 안에 있으므로 동시 수정이 일어나지 않도록 보장하지만, 정작 자신이 콜백을 거쳐 되돌아와 수정하는 것 까지는 막지 못한다.

(다시 정리)


<br>

## **⭐️ 아이템 80 : 스레드보다는 실행자, 태스크, 스트림을 애용하라**

```java.util.concurrent```패키지는 실행자 프레임 워크라고 하는 인터페이스 기반의 유연한 태스크 실행 기능을 담고 있다.  

- 작업 큐를 생성하는 방법
```java
ExecutorService exec = Executors.newSingleThreadExecutor();
```
- 이 실행자에 실행할 태스크를 제출하는 방법
```java
exec.execute(runnable);
```
- 실행자를 우아하게 종료시키는 방법
```java
exec.shutdown();
```
이외에도 실행자 서비스의 기능은 많다.  

### ***실행자의 주요 기능***
- 특정 태스크가 완료되기를 기다린다.
- 태스크 모음 중 아무것 하나(invokeAny 메서드) 혹은 모든 태스크(invokeALL 메서드)가 완료되기를 기다린다.
- 실행자 서비스가 종료하기를 기다린다(awaitTermination 메서드)
- 완료된 태스크들의 결과를 차례로 받는다(ExecutorCompletionService 이용)
- 태스크를 특정 시간에 혹은 주기적으로 실행하게 한다(ScheculedThread PoolExecutor 이용)

### ***다른 기능들***
- 큐를 둘 이상의 스레드가 처리하게 하고 싶다면 간단히 다른 정적 팩터리를 이용하여 다른 종류의 실행자 서비스(스레드 풀)를 생성하면 된다.
- 평범하지 않은 실행자를 원한다면 ThreadPoolExecutor 클래스를 직접 사용해도 된다. 이 클래스로는 스레드 풀 동작을 결정하는 거의 모든 속성을 설정할 수 있다.
- 작은 프로그램이나 가벼운 서버라면 Executors.newCachedThreadPool을 사용하라. 특별히 설정할 게 없고 일반적인 용도에 적합하게 동작한다.
- 무거운 프로덕션 서버에서는 스레드 개수를 고전한 Executors.newFixedThreadPool을 선택하거나 완전히 통제할 수 있는 ThreadPoolExecutor를 직접 사용하는 편이 훨씬 낫다.

### 작업 큐를 손수 만드는 일은 삼가야 하고, 스레드를 직접 다루는 것도 일반적으로 삼가야 한다. 스레드를 직접 다루면 Thread가 작업 단위와 수행 메커니즘 역할을 모두 수행하게 된다. 반면 실행자 프레임워크에서는 작업 단위와 실행 매커니즘이 분리되어 있어서 의미가 명확하다.

작업 단위를 나타내는 핵심 추상 개념이 태스크다. 태스크에는 두 가지가 있다.
- Runnable : 값을 반환하지 않는 태스크
- Callable : 값을 반환하는 태스크(예외를 던질 수 있다.)
- 두 태스크 모두 ExecutorService의 submit 메서드에 건네서 실행할 수 있다.

### ***ForkJoinTask***
자바 7이 되면서 실행자 프레임워크는 포크-조인 태스크를 지원하도록 확장되었다. 포크-조인 태스크, 즉 ForkJoinTask의 인스턴스는 작은 하위 태스크로 나뉠 수 이고, ForkJoinPool을 구성하는 스레드들이 이 태스크들을 처리하며, 일을 먼저 끝낸 스레드는 다른 스레드의 남은 태스크를 가져와 대신 처리할 수도 있다.
이러한 포크-조인 태스크를 직접 작성하고 튜닝하기란 어려운 일이지만, 포크-조인 풀을 이용해 만든 병렬 스트림을 이용하면 적은 노력으로 그 이점을 얻을 수 있다. 물론 포크-조인에 적합한 형태의 작업이어야 한다.

<br>

## **⭐️ 아이템 81 : wait와 notify보다는 동시성 유틸리티를 애용하라**
요즘 버전의 자바는 wait와 notify를 사용해야 할 이유가 많이 줄었다.  
하지만 알아 두자!

synchronized 키워드로 동기화를 수행하면 데이터를 보호할 수 있지만 하나의 임계영역에 하나의 스레드만 들어갈 수 있다.  
이를 위해 wait와 notify를 사용한다.  
wait와 notify는 Object의 메서드이다.  
wait와 notify는 반드시 동기화 영역 안에서 호출해야 한다.  
- wait는 객체의 lock을 풀고 쓰레드를 해당 객체의 wating pool에 넣는다.
- notify는 객체의 lock을 풀고 waiting pool에 있는 쓰레드 중 하나를 깨운다.
- notifyAll은 객체의 lock을 풀고 waiting pool에 있는 모든 쓰레드를 깨운다.

wait와 notify는 올바르게 사용하기가 아주 까다롭다. 따라서 고수준 동시성 유틸리티를 사용하는 것이 좋다.  

### ***고수준 동시성 유틸리티 종류***
- 실행자 프레임워크
- 동시성 컬렉션(concurrent collection)
- 동기화 장치(synchronizer)

### ***동시성 컬렉션***
- ```List, Queue, Map, Set``` 인터페이스에 동시성을 가미해 구현한 고성능 컬렉션
- 높은 동시성에 도달하기 위해 동기화를 각자의 내부에서 수행한다.
- **동시성 컬렉션에서 동시성을 무력화하는 건 불가능하며, 외부에서 락을 추가로 사용하면 오히려 속도가 느려진다.**
- 동시성을 무력화하지 못하므로 여러 메서드를 원자적으로 묶어 호출하는 일은 여전히 어렵다.
- 따라서 ```상태 의존적 수정```메서드가 추가되었다.

```java
// ConcurrentMap으로 구현한 동시성 정규화 맵
public class Intern {
    // ConcurrentMap으로 구현한 동시성 정규화 맵 - 최적은 아니다.
    private static final ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

    public static String intern(String s) {
        String previousValue = map.putIfAbsent(s, s);
        return previousValue == null ? s : previousValue;
    }
}
```
위 코드는 동시성 정규화 맵을 구현한 코드이다.  
```putIfAbsent```메서드는 주어진 키에 매핑된 값이 아직 없을 때만 새 값을 집어넣는다. 기존값이 있었다면 반환하고, 없으면 null을 반환한다.  
이 메서드 덕에 안전한 정규화 맵을 쉽게 만들 수 있다.  

하지만 위 코드는 최적이 아니다.  
```ConcurrentHashMap```은 ```get``` 같은 검색 기능에 최적화되어 있기 떄문에 이를 이용하면 더 빠르게 개선할 수 있다.

```java
// ConcurrentMap으로 구현한 동시성 정규화 맵 - 더 빠르다!
public static String intern(String s) {
    String result = map.get(s);
    if (result == null) {
        result = map.putIfAbsent(s, s);
        if (result == null)
            result = s;
    }
    return result;
}
```
동시성 컬렉션은 동기화한 컬렉션을 낡은 유산으로 만들었다.  
이제는 ```Collections.synchronizedMap``` 보다는 ```ConcurrentHashMap```을 사용하는게 훨씬 낫다.

### ***동기화 장치***
`BlockingQueue` 는 `Queue`를 확장한 컬렉션인데 추가된 메서드 중 take는 큐의 첫 원소를 꺼낸다.  
이때 만약 큐가 비어있다면 새로운 원소가 추가될 때까지 기다린다.  
이런 특성 덕분에 작업 큐(생산자-소비자 큐)로 쓰기에 적합하다.  
`ThreadPoolExecutor`는 작업 큐로 `BlockingQueue`를 사용한다.

동기화 장치는 스레드가 다른 스레드를 기다릴 수 있게 해 서로의 작업을 조율할 수 있도록 해준다.  
가장 자주 쓰이는 동기화 장치는 `CountDownLatch` 와 `Semaphore`다.  
그리고 가장 강력한 동기화 장치는 바로 `Phaser`다.

### CountDownLatch
`CountDownLatch`는 일회성 장벽으로, 하나 이상의 스레드가 다른 하나 이상의 스레드 작업이 끝날 때까지 기다리게 한다.  
`CountDownLatch`의 유일한 생성자는 `int` 값을 받으며, 이 값이 `countDown` 메서드를 몇번 호출해야 대기 중인 스레드들을 깨우는지를 결정한다.

`CountDownLatch`를 활용해 시간을 재는 프레임워크를 구축하는 예시를 보자.

```java
// 동시 실행 시간을 재는 간단한 프레임워크 
public class ConcurrentTimer {
    private ConcurrentTimer() { } // 인스턴스 생성 불가

    public static long time(Executor executor, int concurrency, Runnable action) throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(concurrency);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(concurrency);

        for (int i = 0; i < concurrency; i++) {
            executor.execute(() -> {
                ready.countDown(); // 타이머에게 준비를 마쳤음을 알린다.
                try {
                    start.await(); // 모든 작업자 스레드가 준비될 때까지 기다린다.
                    action.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();  // 타이머에게 작업을 마쳤음을 알린다.
                }
            });
        }

        ready.await();     // 모든 작업자가 준비될 때까지 기다린다.
        long startNanos = System.nanoTime();
        start.countDown(); // 작업자들을 깨운다.
        done.await();      // 모든 작업자가 일을 끝마치기를 기다린다.
        return System.nanoTime() - startNanos;
    }

    public static void main(String[] args) {
        Executor executor = Executors.newCachedThreadPool();
        try {
            long time = time(executor, 10, () -> System.out.println("Hello World"));
            System.out.println("실행 시간: " + time + " ns");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

ready 래치는 작업자 스레드들이 준비가 완료됐음을 타이머 스레드에게 통지할 때 사용한다. 통지를 끝낸 작업자 스레드들은 두 번째 래치인 start가 열리기를 기다린다.  
마지막 작업자 스레드가 ready.countDown을 호출하면 타이머 스레드가 시작 시각을 기록하고 start.countDown()을 호출해 기다리던 작업자 스레드들을 깨운다. 그 직후 타이머 스레드는 세 번째 래치인 done이 열리기를 기다린다.  
done 래치는 마지막 남은 작업자 스레드가 동작을 마치고 done.countDown을 호출하면 열린다. 타이머 스레드를 done 래치가 열리자마자 깨어나 종료 시각을 기록한다.  

세부적으로 time 메서드에 넘겨진 executor는 concurrency 매개변수로 지정한 동시성 수준만큼의 스레드를 생성할 수 있어야 한다.  
그렇지 못하면 이 메서드는 결코 끝나지 않는 스레드 기아 교착상태(thread starvation deadlock)에 빠진다.

### ***wait 과 notify***

새로운 코드라면 wait과 notify 대신 동시성 유틸리티를 사용해야 한다. 하지만 레거시를 다뤄야 할 때도 있을 것이다.  

```java
// wait 메서드를 사용하는 표준 방식
synchronized (obj) {
	while(<조건이 충족되지 않았다>)
    	obj.wait(); // 락을 놓고, 깨어나면 다시 잡는다.
	
    ... // 조건이 충족되면 동작을 수행한다.
}
```

**wait 메서드를 사용할 때는 반드시 대기 반복문(wait loop) 관용구를 사용하라. 반복문 바깥에서는 절대 호출하지 말아야 한다.**  
이 구문은 wait호출 전 후로 조건이 만족되었는지 확인한다.  

대기 전에 조건을 검사해 조건이 이미 충족되었다면 wait를 건너뛰게 하는 것은 응답 불가 상태를 예방하는 조치다.  
만약 조건이 충족되었는데 스레드가 notify 메서드를 호출하고 대기 상태로 빠지면, 그 스레드를 다시 깨울 수 있다고 보장할 수 없다.

대기 후에 조건을 검사하여 조건이 충족되지 않았다면 다시 대기하게 하는 것은 안전 실패를 막는 조치다.  
만약 조건이 충족되지 않았는데 스레드가 동작을 이어가면 락이 보호하는 불변식을 깨뜨릴 위험이 있다.

조건이 만족되지 않아도 스레드가 깨어날 상황이 몇가지가 있다. 다음을 보자.

- 스레드가 notify를 호출하고 대기중이던 스레드가 깨어나는 사이 다른 스레드가 락을 얻어 그 락이 보호하는 상태를 변경한다.
- 조건이 만족되지 않았음에도 다른 스레드가 실수로 혹은 악의로 notify를 호출한다.
- 깨우는 스레드가 지나치게 관대해 일부 조건만 만족했는데도 notifyAll을 호출하는 경우
- 대기 중인 스레드가 드물게 notify 없이도 깨어나는 경우 (허위 각성 현상, spurious wakeup)

### **notify 대신 notifyAll**  
일반적으로는 notify 보다는 notifyAll을 호출하는 것이 합리적이고 안전한 조언이 된다.  

- notifyAll은 관련 없는 스레드가 실수로 혹은 악의로 wait를 호출하는 공격으로 보호할 수 있다.
- 깨어나야 하지만 깨어나지 못한 스레드들을 깨울 수 있다. 다른 스레드가 깨워질수 있지만, 조건이 만족되지 않았다면 그 스레드들은 다시 대기할 것이다.

모든 스레드가 같은 조건을 기다리고, 조건이 한 번 충족될때마다 하나의 스레드만 깨우는 경우라면 최적화 하기 위한 용도로 notify를 사용해도 된다.

<br>

## **⭐️ 아이템 82 : 스레드 안전성 수준을 문서화하라**

자바독이 기본 옵션에서 생성한 API문서에는 `synchonized` 한정자가 포함되지 않는다.  
**메서드 선언에 `synchonized` 한정자를 선언할지는 구현 이슈일 뿐 API에 속하지 않는다.**  
따라서 `synchonized` 한정자를 사용해 스레드 안전성을 구현했다고 해도 API 사용자는 그 사실을 알 수 없다.  

멀티스레드 환경에서도 API를 안전하게 사용하게 하려면 클래스가 지원하는 스레드 안전성 수준을 정확히 명시해야 한다.
### ***스레드 안전성 수준***
- 불변 : 이 클래스의 인스턴스는 마치 상수와 같아서 외부 동기화도 필요없다.
  - String, Long, BigInteger
  - @Immutable
- 무조건적 스레드 안전 : 이 클래스의 인스턴스는 수정될 수 있으나, 내부에서 충실히 동기화하여 별도의 외부 동기화 없이 동시에 사용해도 안전하다.
  - AtomicLong, ConcurrentHashMap
  - @ThreadSafe
- 조건부 스레드 안전 : 무조건적 스레드 안전과 같으나, 일부 메서드는 동시에 사용하려면 외부 동기화가 필요하다.
  - Collections.synchronizedMap
  - @ThreadSafe
- 스레드 안전하지 않음 : 이 클래스의 인스턴스는 수정될 수 있고, 동시에 사용하려면 외부 동기화가 필요하다.
  - ArrayList, HashMap, HashSet
  - @NotThreadSafe
- 스레드 적대적 : 이 클래스의 인스턴스는 수정될 수 있으며, 동시에 사용하려면 외부 동기화가 필요하다. 또한 해당 클래스는 클라이언트가 동기화해야 하는 또 다른 객체의 동기화 정책을 훼손한다.
  - Hashtable, Vector
  - @NotThreadSafe

(다시 정리)

모든 클래스가 자신의 스레드 안전성 정보를 문서화해야한다. 정확한 언어로 명확히 설명하거나 스레드 안전성 애너테이션을 사용할 수 있다.  
synchronized 한정자는 문서화와 관련이 없다.  조건부 스레드 안전 클래스는 메서드를 어떤 순서로 호출할 때 외부 동기화가 요구되고, 그때 어떤 락을 얻어야 하는지도 알려줘야 한다.  
무조건적 스레드 안전 클래스를 작성할 때는 synchronized 메서드가 아닌 비공개 락 객체를 사용하자. 이렇게 해야 클라이언트나 하위 클래스에서 동기화 매커니즘을 깨뜨리는 걸 예방할 수 있고, 
필요하다면 다음에 더 정교한 동시성을 제어 메커니즘으로 재구현할 여지가 생긴다.

<br>

## **⭐️ 아이템 83 : 지연 초기화는 신중히 사용하라**

지연 초기화(lazy initialization)는 필드의 초기화 시점을 처음 사용할 때까지 늦추는 기법이다.  
- 주로 최적화용도로 쓰인다.
- 클래스와 인스턴스 초기화 때 발생하는 위험한 순환 문제를 해결하는 효과도 있다.

지연 초기화는 "필요할 때까지는 하지 말라" 클래스 혹은 인스턴스 생성 시의 초기화 비용은 줄지만 그 대신 지연 초기화하는 필드에 접근하는 비용은 커진다.  
지연 초기화하려는 필드들 중 초기화가 이뤄지는 비율에 따라, 실제 초기화에 드는 비용에 따라, 초기화된 각 필드를 얼마나 빈번히 호출하느냐에 따라 지연 초기화가 실제로는 성능을 느려지게 할 수 도 있다.

**지연 초기화가 필요한 경우**  
해당 클래스의 인스턴스 중 그 필드를 사용하는 인스턴스의 비율이 낮은 반면, 그 필드를 초기화하는 비용이 크다면 지연 초기화가 제 역할을 할 것이다.
하지만 위 효과를 확인하기 위해서는 지연 초기화 작용 전후의 성능을 측정해보는 것이다.  


### ***지연 초기화 방식***
멀티스레드 환경에서는 지연 초기화 하기가 까다롭다. 지연 초기화 하는 필드를 둘 이상의 스레드가 공유한다면 어떤 형태로든 반드시 동기화 해야한다.

대부분의 상황에서는 일반적인 초기화가 지연초기화보다 낫다.

```java
// 인스턴스 필드를 초기화하는 일반적인 방법 
private final FieldType field1 = computeFieldValue();
```
지연 초기화가 초기화 순환성을 깨뜨릴 것 같으면 synchronized 접근자를 사용하자
```java
// 인스턴스 필드의 지연 초기화 - synchronized 접근자 방식 
private FieldType field2;
private synchronized FieldType getField2() {
    if (field2 == null)
        field2 = computeFieldValue();
    return field2;
}
```
두 관용구(보통의 초기화와 synchronized 접근자를 사용한 지연 초기화)는 정적 필드에도 똑같이 적용된다.  
이때 필드와 접근자 메서드 선언에 static 한정자를 추가해야 한다.  

성능면에서 정적 필드의 지연 초기화가 필요한 경우 지연 초기화 홀더 클래스 관용구(lazy initialization holder class) 를 사용한다.  
클래스는 클래스가 처음 쓰일 때 비로소 초기화된다는 특성을 이용한 관용구
```java
// 정적 필드용 지연 초기화 홀더 클래스 관용구 
private static class FieldHolder {
    static final FieldType field = computeFieldValue();
}

private static FieldType getField() { return FieldHolder.field; }
```
getField가 처음 호출되는 순간 FieldHolder.field가 처음 읽히면서, 비로소 FieldHolder 클래스 초기화를 촉발한다.  
getField 메서드가 필드에 접근하면서 동기화를 전혀 하지 않으니 성능이 느려질 거리가 전혀 없다.  

일반적인 VM은 오직 클래스를 초기화할 때만 필드 접근을 동기화할 것이다.  
클래스 초기화가 끝난 후에는 VM이 동기화 코드를 제거하여, 그 다음부터는 아무런 검사나 동기화 없이 필드에 접근하게 된다.  

**성능 때문에 인스턴스 필드를 지연 초기화해야 한다면 이중검사(double-check) 관용구를 사용하자.**  
이 관용구는 초기화된 필드에 접근할 때의 동기화 비용을 없애준다.  
필드의 값을 두번 검사하는 방식으로  
한번은 동기화 없이 검사하고,  
두 번째는 동기화하여 검사한다.  
두 번째 검사에서도 필드가 초기화되지 않았을 때만 필드를 초기화한다.  
필드가 초기화 된 후로는 동기화하지 않으므로 해당 필드는 반드시 volatile로 선언해야 한다.


```java
// 인스턴스 필드 지연 초기화용 이중검사 관용구
private volatile FieldType field4;

private FieldType getField4() {
    FieldType result = field4;
    if (result != null)    // 첫 번째 검사 (락 사용 안 함)
        return result;

    synchronized(this) {
        if (field4 == null) // 두 번째 검사 (락 사용)
            field4 = computeFieldValue();
        return field4;
    }
}
```
result 변수는 필드가 초기화된 후에는 필드를 한번만 읽도록 보장한다.  
반드시 필요하지는 않지만 성능을 높여주고, 저수준 동시성 프로그래밍에 표준적으로 적용되는 더 우아한 방법이다.  
이중검사를 정적 필드에도 적용할 수 있지만 굳이 그럴 이유가 없다. 이보다는 지연 초기화 홀더 클래스 방식이 낫다.

**이중검사에서 언급할만한 변종 두가지**  
반복해서 초기화 해도 상관없는 인스턴스 필드를 지연 초기화해야 할 때, 이중검사에서 두 번째 검사를 생략할 수 있다. 이 변종의 이름은 자연히 단일검사(single-check) 관용구다.  

```java
// 단일검사 관용구 - 초기화가 중복해서 일어날 수 있다!
private volatile FieldType field5;

private FieldType getField5() {
    FieldType result = field5;
    if (result == null)
        field5 = result = computeFieldValue();
    return result;
}
```
필드는 여전히 volitile로 선언해야 한다.  

모든 스레드가 필드의 값을 다시 계산해도 되고, 필드의 타입이 long과 double을 제외한 다른 기본 타입이라면, 단일검사의 필드 선언에서 volatile 한정자를 없애도 된다.
이 변종은 짜릿한 단일검사(racy single-check) 관용구라고 불린다.  
이 관용구는 어떤 환경에서는 필드 접근 속도를 높여주지만, 초기화가 스레드당 최대 한 번 더 이뤄질 수 있다.

### 정리
대부분의 필드는 지연시키지말고 곧바로 초기화해야한다.  
성능 때문에 혹은 위험한 초기화 순환을 막기 위해 꼭 지연 초기화를 써야 하는 경우 올바른 지연 초기화 기법을 사용한다.  
인스턴스 필드에는 이중검사 관용구, 정적 필드에는 지연 초기화 홀더 클래스 관용구를 사용한다.  
반복해 초기화해도 괜찮은 인스턴스 필드에는 단일검사 관용구도 고려대상이다.

<br>

## **⭐️ 아이템 84 : 프로그램의 동작을 스레드 스케줄러에 기대지 말라**
여러 스레드가 실행 중이면 OS의 스레드 스케줄러가 어떤 스레드를 얼마나 오래 실행할 지를 결정한다.  
정상적인 OS라면 이 작업을 공정하게 수행하지만 구체적인 스케줄링 정책은 OS마다 다를 수 있다.  
따라서 잘 작성된 프로그램이라면 이 정책에 좌지우지돼서는 안 된다.  
**정확성이나 성능이 스레드 스케줄러에 따라서 달라지는 프로그램이라면 다른 플랫폼에 이식하기 어렵다.**  

견고하고 빠릿하고 이식성 좋은 프로그램을 작성하는 가장 좋은 방법은 실행 가능한 스레드의 평균적인 수를 프로세서 수보다 지나치게 많아지지 않도록 하는 것이다.  

실행 가능한 스레드 수를 적게 유지하는 주요 기법은 각 스레드가 무언가 유용한 작업을 완료한 후에는 다음 일거리가 생길 때까지 대기하도록 하는 것이다.  
**스레드는 당장 처리해야 할 작업이 없다면 실행돼서는 안 된다.**  

스레드는 절대 바쁜 대기(busy waiting) 상태가 되면 안 된다.  
공유 객체의 상태가 바뀔 때까지 쉬지 않고 검사해서는 안 된다는 뜻이다.  
바쁜 대기는 스레드 스케줄러의 변덕에 취약할 뿐 아니라, 프로세서에 큰 부담을 주어 다른 유용한 작업이 실행될 기회를 박탈한다. 

```java
// 끔찍한 CountDownLatch 구현 - 바쁜 대기 버전!
public class SlowCountDownLatch {
    private int count;

    public SlowCountDownLatch(int count) {
        if (count < 0)
            throw new IllegalArgumentException(count + " < 0");
        this.count = count;
    }

    public void await() {
        while (true) {
            synchronized(this) {
                if (count == 0)
                    return;
            }
        }
    }
    public synchronized void countDown() {
        if (count != 0)
            count--;
    }
}
```
위 코드는 바쁜 대기 버전의 CountDownLatch를 구현한 코드이다.
래치를 기다리는 스레드를 1000개 만들어 자바의 CountDownLatch와 비교하니 10배 느렸다.  
하나 이상의 스레드가 필요도 없이 실행 가능한 상태인 시스템은 흔하게 볼 수 있다.  
이런 시스템은 성능과 이식성이 떨어질 수 있다.

특정 스레드가 다른 스레드들과 비교해 CPU 시간을 충분히 얻지 못해서 간신히 돌아가는 프로그램을 보더라도 Thread.yield를 써서 문제를 고쳐보려는 유혹을 떨쳐내자.  
증상이 어느 정도는 호전될 수도 있지만 이식성은 그렇지 않을 것이다.  
Thread.yield는 테스트할 수단도 없다.  
차라리 애플리케이션 구조를 바꿔 동시에 실행 가능한 스레드 수가 적어지도록 조치해주자.  
이런 상황에서 스레드 우선순위를 조절하는 방법도 있지만, 역시 비슷한 위험이 따른다.  