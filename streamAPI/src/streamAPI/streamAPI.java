package streamAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* 람다식
    람다식(Lambda Expression)이란 함수를 하나의 식(expression)으로 표현한 것이다. 
    함수를 람다식으로 표현하면 메소드의 이름이 필요 없기 때문에, 
    람다식은 익명 함수(Anonymous Function)의 한 종류라고 볼 수 있다.
    
    (매개변수, ... ) -> { 실행문 ... }
*/

/* 구성
    스트림 생성
        스트림은 컬렉션, 배열, 파일, 혹은 직접 생성된 데이터 소스에서 만들어집니다.
        주요 메서드:
        Collection.stream(): 리스트, 셋 등에서 스트림 생성.
        Arrays.stream(): 배열에서 스트림 생성.
        Stream.of(): 개별 요소로 스트림 생성.
        Stream.generate(), Stream.iterate(): 동적 스트림 생성.

    중간 연산
        filter(): 조건에 맞는 요소만 남김.
        map(): 요소를 변환.
        sorted(): 정렬.
        distinct(): 중복 제거.
        limit(), skip(): 크기 조절.
        
    최종 연산
        collect, count, average, max, reduce, anyMatch, forEach, findAny .... 
        collect(): 리스트, 맵 등으로 변환.
        forEach(): 각 요소에 작업 수행.
        reduce(): 요소를 하나로 집계.
        count(), max(), min(): 통계값 계산.
        anyMatch(), allMatch(), noneMatch(): 조건 검사.

    return type이 Stream 이라면 중간 처리 메소드이고, 
    return type이 기본타입 or Optional___ 라면 최종 처리 메소드이다.
*/

/* 처리과정
    - 중간 스트림이 생성될 때 요소들이 바로 처리되는 것이 아님.
        중요) 최종 처리가 시작되기 전까지 중간처리는 지연(lazy)되며, 최종 처리가 시작되면 중간 스트림에서 처리를 시작한다.
    - stream은 한번 사용하면 재사용이 불가능

    단 Optional의 경우는 다름 Optional이 제공하는 메소드(map()...)는 최종 연산 없이도 실행될 수 있습니다.
 */

/* 1급 객체 (함수를 데이터 다루듯이  다룰 수 있다)
    - 변수나 데이터에 할당 할 수 있어야 한다. ( const mul = function (num) {...} )
    - 객체의 인자로 넘길 수 있어야 한다. ( function mulNum(func, number) {return func(number);} )
    - 객체의 리턴값으로 리턴 할수 있어야 한다. ( function add(num1) { return function (num2) { return num1 + num2;} } ) 
 */

/* 함수형 인터페이스(Functional Interface)
    함수를 1급 객체처럼 다룰 수 있게 해주는 어노테이션으로 (@FunctionalInterface), 
    인터페이스에 선언하여 단 하나의 추상 메소드만을 갖도록 제한하는 역할을 한다. 
    함수형 인터페이스를 사용하는 이유는 Java의 람다식이 함수형 인터페이스를 반환하기 때문이다.

    [ Java에서 제공하는 함수형 인터페이스 ]    
        Supplier<T>    : T 타입의 값을 제공하는 함수 인터페이스
        Consumer<T>    : T 타입을 받아서 로직을 수행 후 반환값은 없는 함수 인터페이스
        Function<T, R> : T라는 타입을 받아서 R이라는 타입을 반환하는 추상 메소드
        Predicate<T>   : T 타입을 받아서 boolean을 리턴하는 함수 인터페이스
        Runnable,Callable : 쓰레드 구현시 사용
*/

/* Method Reference
    함수형 인터페이스를 람다식이 아닌 일반 메소드를 참조시켜 선언하는 방법
    기존 람다식을 줄일 수 있는 표현법으로 메소드, 생성자의 참조를 전달할 수 있음, "::"로 표현됨
    기본 표현식
    ClassName::Method  ex) Person::getAge

    일반 메소드를 참조 시키기 위해서는 다음 3가지 조건을 만족해야한다.
        - 함수형 인터페이스의 매개변수 타입 = 메소드의 매개변수 타입
        - 함수형 인터페이스의 매개변수 개수 = 메소드의 매개변수 개수
        - 함수형 인터페이스의 반환형 = 메소드의 반환형

    사용법
    1. (Person p) -> p.getAge()                     ==>    Person::getAge

    2. () -> Thread.currentThread().dumpStack()  ==>   Thread.currentThread::dumpStack

    3. (str, i) -> str.substring(i)                      ==>    String::substring

    4. (String s) -> System.out.println(s)           ==>    System.out::println 
*/

/* Optional
    Java8에서는 Optional<T> 클래스를 사용해 NPE를 방지할 수 있도록 도와준다. 
    Optional<T>는 null이 올 수 있는 값을 감싸는 Wrapper 클래스로, 참조하더라도 NPE가 발생하지 않도록 도와준다. 

    사용법 및 가이드
    - Optional 변수에 Null을 할당하지 말아라
    - 값이 없을 때 Optional.orElseX()로 기본 값을 반환하라
    - 단순히 값을 얻으려는 목적으로만 Optional을 사용하지 마라
    - 생성자, 수정자, 메소드 파라미터 등으로 Optional을 넘기지 마라
    - Collection의 경우 Optional이 아닌 빈 Collection을 사용하라
    - 반환 타입으로만 사용하라
*/

/*  
 * ParallelStream
    Java8에서 등장한 Stream은 병렬 처리를 쉽게 할 수 있도록 메소드를 제공해 준다.
    개발자가 직접 스레드 혹은 스레드풀을 생성하거나 관리할 필요 없이 parallelStream(), parallel()만 사용하면 
    알아서 ForkJoinFramework 관리 방식을 이용하여 작업들을 분할하고, 병렬적으로 처리하게 된다.

    Fork / Join Framework
    Fork / Join Framework는 작업들을 분할 가능한 만큼 쪼개고, 쪼개진 작업들을 Work Thread를 통해 작업 후 
    결과를 합치는 과정으로 결과를 만들어 낸다.
    즉, 분할 정복(Divide and Conquer) 알고리즘과 흡사하며, Fork를 통해 Task를 분담하고 Join을 통해 결과를 합친다.
    사용방법은 parallelStream() 또는 stream().parallel() 만 붙여주면 된다
    
    ForkJoinPool
    ForkJoinPool을 설정하여 동시에 실행 될 수 있는 쓰레드의 개수를 제한 할 수 있다.


    ParallelStream 사용 전 꼭 알아야 할 주의사항 
    1. Thread Pool 공유
     parallelStream은 내부적으로 common ForkJoinPool을 사용하여 작업을 병렬화 시킨다. 
    별도의 설정이 없다면 하나의 Thread Pool을 모든 parallelStream이 공유하게 되고,  
    이는 Thread Pool을 사용하는 다른 Thread에 영향을 줄 수 있으며, 반대로 영향을 받을 수 있다.
    따라서 Thread를 반납하지 않고 계속 점유 중이라면 문제가 될 수 있다.
    4개의 Thread 중 1, 2, 3은 사용할 수 없으며 Thread 4 한 개 만을 이용해서 모든 요청을 처리하고 있는 상황을 가정하자. 
    이때 Thread 1, 2, 3 이 sleep과 같이 아무런 일을 하지 않으면서 점유를 하고 있다면 이는 문제가 크다.
    만약, Thread 4까지 점유 중이게 되면 더 이상 요청은 처리되지 않고 Thread Pool Queue에 쌓이게 되며, 
    일정시간 이상 되면 요청이 Drop 되는 현상까지 발생할 것이다. 
    병렬 스트림은 Thread Pool을 global하게 공유하기 때문에 만약 A메서드에서 4개의 Thread를 모두 점유하면 
    다른 병렬 스트림의 요청은 처리되지 않고 대기하게 된다.또한, blocking  I/O 가 발생하는 작업을 하게 되면 
    Thread Pool 내부의 스레드들은 block 되며, 이때 Thread Pool을 공유하는 다른 쪽의 병렬 Stream은 스레드를 얻을 때까지 
    계속해서 기다리게 되면서 문제가 발생한다. 
    이 문제는 각 parallelStream마다 커스텀(new ForkJoinPool(int n))하여 독립적인 Thread Pool로 분리하여 사용하면 해결할 수 있다.


    2. Custom Thread Pool 사용 시 Memory Leak 주의
     ForkJoinPool customForkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    별도의 스레드 풀 생성 시 정석은 실행 중인 CPU 코어 수를 기준으로 생성하는 것이다. 
    물리적인 코어 수를 초과하여 생성할 경우, 생성은 되지만 스레드 관리 오버헤드와 스레드 간의 
    빈번한 컨텍스트 스위칭(Context-Switching) 등의 문제로 성능 저하가 발생할 수 있다. 
    Parallel Stream 별로 ForkJoinPool을 인스턴스화하여 사용하면 OOME(OutOfMemoryError)이 발생할 수 있다.
    default로 사용되는 Common ForkJoinPool은 정적(static)이기 때문에 메모리 누수가 발생하지 않지만,
    Custom 한 ForkJoinPool 객체는 참조 해제되지 않거나, GC(Garbage Collection)로 수집되지 않을 수 있다.
    이 문제에 대한 해결 방법은 간단한데, Custom ForkJoinPool을 사용한 후 다음과 같이 스레드 풀을 명시적으로 종료하는 것이다.
    customForkJoinPool.shutdown();

    3. 정리
    I/O를 기다리는 작업에는 적합하지 않고 (이 경우 CompletableFuture가 적합)
    분할이 잘 이루어질 수 있는 데이터 구조 혹은 작업이 독립적이면서 CPU 사용이 높은 작업에 적합하다. 
*/	

public class streamAPI {
    public static void main(String[] args) {
        mapPractice4();
    }
    
    public static void test1() {        
        // 잘못된 예 , filter라는 중간 연산만 있어서 실제 실행되지 않는다.
        Stream.of("a", "b", "c", "d", "f")
            .filter(s -> {
                System.out.println("filter: " + s);
                return true;
            });
            
        // 최종 연산(forEach)이 있어 실제로 실행되어 결과가 나온다.
        Stream.of("a", "b", "c", "d", "f")
            .filter(s -> {
                System.out.println("filter: " + s);
                return true;
            })
            .forEach(s -> System.out.println("forEach: " + s));
    }
    public static void test2() {
        // map 모든 값을 대상을 처리후 리턴하는것이 아니라 순차적으로 진행시킴을 알 수 있다
        Stream.of("a", "b", "c", "d", "f")
            .map(s -> {
                System.out.println("map : " + s);
                return s.toUpperCase();
            })
            .anyMatch(s -> {
                System.out.println("anyMatch: " + s);
                return s.startsWith("B");
            });
    }
    public static void test2a() {
        // map 모든 값을 대상을 처리후 리턴하는것이 아니라 순차적으로 진행시킴을 알 수 있다
        Stream.of("a", "b", "c", "d", "f")
            .map(s -> {
                try {
                    System.out.println("map : " + s);
                    Thread.sleep(1000);
                    return s.toUpperCase();
                } catch (Exception e) {
                    return "error";
                }
            }).forEach((s) -> {
                System.out.println("result : " + s);
            });
    }
    public static void test3() {
        Optional<String> optional = Optional.of("abc");

        optional.isPresent();           // true
        optional.get();                 // "abc"
        optional.orElse("def");    // "abc"

        optional.ifPresent((s) -> System.out.println(s.charAt(0)));     // "a"
    }
    public static void test4() {
        
        String[] nameArr = {"superM", "betM", "spiderM", "ironM"};
        List<String> nameList = Arrays.asList(nameArr);
        
        // 원본의 데이터가 아닌 별도의 Stream을 생성함
        Stream<String> nameStream = nameList.stream();
        Stream<String> arrayStream = Arrays.stream(nameArr);
        
        // 복사된 데이터를 정렬하여 출력함
        nameStream.sorted().forEach(System.out::println);
        System.out.println("------------------------");
        arrayStream.sorted().forEach(System.out::println);
    }
    public static void test5() {
        // Function 사용법
        Function<Integer, Integer> addOne = (i) -> i + 1;
        Function<Integer, Integer> square = (i) -> i * i;
        System.out.println(addOne.andThen(square).apply(2));//9
    }
    public static void test6() {
        // 기존의 람다식
        Function<String, Integer> function = (str) -> str.length();
        function.apply("Hello World");

        // 메소드 참조로 변경
        Function<String, Integer> function2 = String::length;
        function2.apply("Hello World");
    }
    public static void test7() {
        // Stream의 요소들을 List나 Set, Map, 등 다른 종류의 결과로 수집하고 싶은 경우에는 collect 함수를 이용할 수 있다.
        List<Product> productList = Arrays.asList(
                new Product(23, "potatoes"),
                new Product(17, "banana"),
                new Product(14, "orange"),
                new Product(13, "lemon"),
                new Product(23, "bread"),
                new Product(13, "sugar"));
        
        //############# toList
        List<String> nameList = productList.stream()
                .map(Product::getName)
                .collect(Collectors.toList());        
        for (String name : nameList) {
            System.out.println(name);   
        }
        System.out.println("-------------------");

        //############# joining
        String listToString = productList.stream()
            .map(Product::getName)
            .collect(Collectors.joining());
        System.out.println(listToString);
        // potatoesorangelemonbreadsugar

        String listToString2 = productList.stream()
            .map(Product::getName)
            .collect(Collectors.joining(" "));
        System.out.println(listToString2);
        // potatoes orange lemon bread sugar

        // delimiter : 각 요소 중간에 들어가 요소를 구분시켜주는 구분자
        // prefix : 결과 맨 앞에 붙는 문자
        // suffix : 결과 맨 뒤에 붙는 문자
        String listToString3 = productList.stream()
            .map(Product::getName)
            .collect(Collectors.joining(",", "<", ">")); 
        System.out.println(listToString3);
        // <potatoes,orange,lemon,bread,sugar>
        System.out.println("-------------------");

        //############# Match        
        // anyMatch: 1개의 요소라도 해당 조건을 만족하는가
        // allMatch: 모든 요소가 해당 조건을 만족하는가
        // nonMatch: 모든 요소가 해당 조건을 만족하지 않는가
        boolean anyMatch = productList.stream()
            .map(Product::getName)
            .anyMatch(name -> name.contains("a"));
        boolean allMatch = productList.stream()
            .map(Product::getName)
            .allMatch(name -> name.length() > 3);
        boolean noneMatch = productList.stream()
            .map(Product::getName)
            .noneMatch(name -> name.endsWith("s"));
        System.out.println(anyMatch);
        System.out.println(allMatch);
        System.out.println(noneMatch);
        System.out.println("-------------------");

        //############# findAny,findFirst
        // 직렬 처리시 차이 없음 , 병렬 처리시 ,any는 순서가 아닌 가장 먼저 찾게되는걸 리턴함.
        Optional<String> firstElement = productList.stream()
                .map(Product::getName)
                .filter(s -> s.startsWith("t")).findFirst();
        
        // 아래 optional 접근 방법 2가지중 2번째 방법이 더 효율이 좋다.
        // optional의 값의 접근 방법1
        if(firstElement.isPresent()) {
            System.out.println("findFirst: " + firstElement.get());
        }
        // optional의 값의 접근 방법2
        System.out.println("findFirst: " + firstElement.orElseGet( ()->"" ));



        Optional<String> anyElement = productList.stream()
                .map(Product::getName)
                .filter(s -> s.startsWith("b")).findAny();
        System.out.println("findAny: " + anyElement.get());

        Optional<String> anyElement2 = productList.stream().map(Product::getName).parallel()
                .filter(s -> s.startsWith("b")).findAny();        
        System.out.println("findAny2: " + anyElement2.get());
    }
    public static void test8() {
        List<Product> productList = Arrays.asList(
            new Product(23, "potatoes"),
            new Product(17, "banana"),
            new Product(14, "orange"),
            new Product(13, "lemon"),
            new Product(23, "bread"),
            new Product(13, "sugar"));        

        
        List<String> names = productList.stream().map((t) ->t.getName()).collect(Collectors.toList());
        List<String> names2 = null;
        // 전통적인 null 체크
        List<String> nameList = names != null ? names : new ArrayList<>();        
        System.out.println("nameList : " + nameList);

        // Java8 이후
        // 참고) orElse는 null이 아니어도 무조건 불리지만, orElseGet은 null 때만 불린다. 따라서 서버의 부하를 줄이기 위해 orElseGet 사용하자.
        // orElse: 파라미터로 값을 받는다.
        // orElseGet: 파라미터로 함수형 인터페이스(함수)를 받는다.
        
        // 아래는 예를 들기 위한것으로 단순히 값을 얻으려는 목적으로만 Optional을 사용하면 안된다.(부하문제)
        // Optional은 반환타입으로만 사용하여야 한다. (람다 함수를 통한 반환 등)

        // List<String> nameList2 = Optional.ofNullable(names)
        //         .orElseGet(() -> new ArrayList<>());
        List<String> nameList2 = Optional.ofNullable(names)
                .orElse(new ArrayList<>());
        System.out.println("nameList2 : " + nameList2);
    }
    public static void test9() {
        List<Product> productList = Arrays.asList(
                new Product(23, "potatoes"),
                new Product(17, "banana"),
                new Product(14, "orange"),
                new Product(13, "lemon"),
                new Product(23, "bread"),
                new Product(13, "sugar"));
        
        System.out.println("parallelStream Start");
        productList.parallelStream().forEach( (c) -> {
            try {
                Thread.sleep(3000);
                System.out.println(c.getName());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        System.out.println("parallelStream End");

        System.out.println("parallel Start");
        productList.stream().parallel().forEach( (c) -> {
            try {
                Thread.sleep(3000);
                System.out.println(c.getName());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        System.out.println("parallel End");
    }
    public static void test10() {
        List<Product> productList = Arrays.asList(
                new Product(23, "potatoes"),
                new Product(17, "banana"),
                new Product(14, "orange"),
                new Product(13, "lemon"),
                new Product(23, "bread"),
                new Product(13, "sugar"));
        
        // ForkJoinPool 설정 
        // ForkJoinPool fj = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        ForkJoinPool fj = new ForkJoinPool(3);
        System.out.println("parallelStream Start");
        try {
            fj.submit(() -> productList.parallelStream().forEach(number -> {
                try {
                    Thread.sleep(3000);
                    System.out.println(number.getName());
                } catch (Exception e) {
                }
            })).get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 명시적으로 종료시킴
        fj.shutdown();

        System.out.println("parallelStream End");
    }

    /* 
    * 1. 필터링 (Filtering)
    * 컬렉션에서 특정 조건을 만족하는 요소들만 추출하는 작업입니다.
    */
    public static void listPractice1() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> eNumbers = numbers.stream().filter((param) -> {
            if (param % 2 ==0 ) {
                return true;
            }
            return false;
        }).collect(Collectors.toList())
        ;

        System.out.println(eNumbers);
    }

    /* 
    * 2. 매핑 (Mapping)
    * 컬렉션의 요소들을 변환하는 작업입니다.
    */
    public static void listPractice2() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> rNumbers = numbers.stream().map((params) -> {
            return params * 2;
        }).collect(Collectors.toList());

        System.out.println(rNumbers);
    }

    /* 
     * 3. 집계 (Reducing)
     * 컬렉션의 모든 요소를 하나로 결합하는 작업입니다.
     *  a -> total , b -> next
     */
    public static void listPractice3() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        int sum = numbers.stream().reduce(0,(a,b) -> {
            return a+b;
        });

        System.out.println(sum);
    }
    /* 
     * 4. 정렬 (Sorting)
     * 컬렉션의 요소들을 정렬하는 작업입니다.
     */
    public static void listPractice4() {
        List<Integer> numbers = Arrays.asList(5, 1, 4, 3, 2);
        List<Integer> rNumbers = numbers.stream()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

        System.out.println(rNumbers);
    }
    /* 
     * 5. 그룹화 (Grouping)
     * 컬렉션의 요소들을 특정 기준으로 그룹화하는 작업입니다.
     */
    public static void listPractice5() {
        List<Person> people = Arrays.asList(
            new Person("Alice", 30),
            new Person("Bob", 25),
            new Person("Charlie", 30)
        );
        
        Map<Integer,List<Person>> pByAge = people.stream()
                                                .collect(Collectors.groupingBy((params) -> {
                                                    return params.age;
                                                }));
        System.out.println(pByAge);
    }
    /* 
     * 6. 중복 제거 (Distinct)
     * 컬렉션의 중복 요소를 제거하는 작업입니다.
     */
    public static void listPractice6() {
        List<String> items = Arrays.asList("apple", "banana", "apple", "orange", "banana");
        List<String> items2 = items.stream().distinct().collect(Collectors.toList());
        System.out.println(items2);
    }
    /* 
     * 7. 전체 매칭/아무거나 매칭 (All Match / Any Match)
     * 컬렉션의 요소들이 특정 조건을 모두 또는 일부 만족하는지 검사하는 작업입니다.
     */
    public static void listPractice7() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        boolean a = numbers.stream().allMatch((params) -> {
            return params > 0;
        });
        System.out.println(a);
        numbers = Arrays.asList(-1, 2, 3, 4, 5);
        a = numbers.stream().anyMatch((params) -> {
            return params > 0;
        });
        System.out.println(a);
    }
    /* 
     * 8. 최대값/최소값 찾기 (Finding Max/Min)
     * 컬렉션의 최대값 또는 최소값을 찾는 작업입니다.
     */
    public static void listPractice8() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        int m = numbers.stream().max((a,b) -> {
            return Integer.compare(a, b);
        }).orElseGet(() -> {
            return 0;
        });
        System.out.println(m);

        int t = numbers.stream().mapToInt((p) -> {
            return p;
        }).max().orElseGet(() -> {
            return 0;
        });

        System.out.println(t);
    }

    /* 
     * 1. 키 또는 값 필터링
     * Map에서 특정 조건을 만족하는 키나 값을 필터링하는 작업입니다.
     */
    public static void mapPractice1() {
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 10);
        map.put("B", 30);
        map.put("C", 50);
        map.put("D", 20);
        
        Map<String, Integer> filteredMap = map.entrySet()
                                    .stream()
                                    .filter((entry) -> {
                                        return entry.getValue() >=30;
                                    }).collect(Collectors.toMap((params) -> {
                                        return params.getKey();
                                    }, (params) -> {
                                        return params.getValue();
                                    }));

    System.out.println(filteredMap); // 출력: {B=30, C=50}

    }
    /* 
     * 2. 값 변환 (Mapping Values)
     * Map의 값들을 다른 값으로 변환하는 작업입니다.
     */
    public static void mapPractice2() {
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 10);
        map.put("B", 30);
        map.put("C", 50);
        map.put("D", 20);
        
        // 아래는 잘못 사용 map은 스트림 요소를 변환 할 때 쓰는데 여기서 요소는 Map.Entry이며 
        // 저런식으로 쓸 경우 map은 Integer를 반환하게 된다...
        // Map<String, Integer> filteredMap = map.entrySet()
        //                                     .stream()
        //                                     .map((params) -> {
        //                                         return params.getValue() + 10;
        //                                     }).collect(Collectors.toMap(
        //                                         (params) -> {
        //                                             return params.getKey();
        //                                         }, (params) -> {
        //                                             return params.getValue();
        //                                         }))

        Map<String, Integer> filteredMap = map.entrySet()
                                        .stream()
                                        .collect(Collectors.toMap((params) -> params.getKey(), (params) -> params.getValue() + 10));
        System.out.println(filteredMap);
    }
    /* 
     * 3. Map의 키와 값을 교환
     * Map의 키와 값을 서로 교환하는 작업입니다.
     */
    public static void mapPractice3() {
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 10);
        map.put("B", 30);
        map.put("C", 50);
        map.put("D", 20);
        
        Map<Integer,String> filteredMap = map.entrySet().stream()
                                                .collect(Collectors.toMap((params) -> params.getValue(), (params) -> params.getKey()));
        System.out.println(filteredMap);
    }
    /* 
     * 4. Map에서 최대값 찾기
     * Map의 값 중 최대값을 찾는 작업입니다.
     */
    public static void mapPractice4() {
         Map<String, Integer> map = new HashMap<>();
        map.put("A", 10);
        map.put("B", 30);
        map.put("C", 50);
        map.put("D", 20);

        int maxValue = map.values().stream()
                                    .max((a,b) -> {
                                        return a-b;
                                    }).orElseGet(() -> {
                                        return 0;
                                    });

        System.out.println(maxValue);
    }
    /* 
     * 5. 키 기준으로 정렬
     * Map을 키 기준으로 정렬하는 작업입니다.
     */
    public static void mapPractice5() {
         Map<String, Integer> map = new HashMap<>();
        map.put("A", 10);
        map.put("B", 30);
        map.put("C", 50);
        map.put("D", 20);
        
        Map<String, Integer> filteredMap = map.entrySet().stream()
                                            .sorted((a,b) -> {
                                                return a.getKey().compareTo(b.getKey());
                                            })
                                            .collect(Collectors.toMap(
                                                (params) -> params.getKey(), 
                                                (params) -> params.getValue(),
                                                (a,b) -> a,
                                                () -> new LinkedHashMap<>()
                                             ));
                                     
        System.out.println(filteredMap);
    }
    /* 
     * 6. 값 기준으로 그룹화
     * Map의 값들을 기준으로 그룹화하는 작업입니다.
     */
    public static void mapPractice6() {
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 30);
        map.put("B", 20);
        map.put("C", 30);
        map.put("D", 20);

        Map<Integer, List<String>> groupedMap = map.entrySet().stream()
        .collect(Collectors.groupingBy(
            entry -> entry.getValue(),
            Collectors.mapping(entry -> entry.getKey(), Collectors.toList())
        ));
    
        System.out.println(groupedMap);
    }

    public static void streamCreationExamples() {
        // 1. 컬렉션에서 스트림 생성
        List<String> list = Arrays.asList("apple", "banana", "cherry");
        Stream<String> streamFromList = list.stream();
        System.out.println("From List:");
        streamFromList.forEach(System.out::println);
    
        // 2. 배열에서 스트림 생성
        String[] array = {"dog", "cat", "bird"};
        Stream<String> streamFromArray = Arrays.stream(array);
        System.out.println("From Array:");
        streamFromArray.forEach(System.out::println);
    
        // 3. 직접 요소로 스트림 생성
        Stream<Integer> streamFromValues = Stream.of(1, 2, 3, 4, 5);
        System.out.println("From Values:");
        streamFromValues.forEach(System.out::println);
    
        // 4. 무한 스트림 생성 (제한 필요)
        Stream<Integer> infiniteStream = Stream.iterate(0, n -> n + 2).limit(5); // 0, 2, 4, 6, 8
        System.out.println("Infinite Stream with Limit:");
        infiniteStream.forEach(System.out::println);
    
        // 5. 랜덤 값 스트림 생성
        Stream<Double> randomStream = Stream.generate(Math::random).limit(3);
        System.out.println("Random Stream:");
        randomStream.forEach(System.out::println);
    }
    
    public static void intermediateOperationExamples() {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5);
    
        // 1. filter: 짝수만 필터링
        Stream<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0);
        System.out.println("Even Numbers:");
        evenNumbers.forEach(System.out::println);
    
        // 2. map: 제곱으로 변환
        Stream<Integer> squaredNumbers = numbers.stream()
            .map(n -> n * n);
        System.out.println("Squared Numbers:");
        squaredNumbers.forEach(System.out::println);
    
        // 3. sorted: 오름차순 정렬
        Stream<Integer> sortedNumbers = numbers.stream()
            .sorted();
        System.out.println("Sorted Numbers:");
        sortedNumbers.forEach(System.out::println);
    
        // 4. distinct: 중복 제거
        Stream<Integer> uniqueNumbers = numbers.stream()
            .distinct();
        System.out.println("Unique Numbers:");
        uniqueNumbers.forEach(System.out::println);
    
        // 5. limit & skip: 앞 3개만 선택, 처음 2개 건너뛰기
        Stream<Integer> limitedNumbers = numbers.stream()
            .skip(2)
            .limit(3);
        System.out.println("Limited & Skipped Numbers:");
        limitedNumbers.forEach(System.out::println);
    
        // 6. chaining: 여러 중간 연산 결합
        Stream<Integer> chainedStream = numbers.stream()
            .filter(n -> n > 3)
            .map(n -> n * 2)
            .sorted(Comparator.reverseOrder());
        System.out.println("Chained Operations:");
        chainedStream.forEach(System.out::println);
    }
    
    public static void terminalOperationExamples() {
        List<String> fruits = Arrays.asList("apple", "banana", "cherry", "date", "elderberry");
    
        // 1. collect: 리스트로 수집
        List<String> upperFruits = fruits.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        System.out.println("Collected to List: " + upperFruits);
    
        // 2. forEach: 출력
        System.out.println("ForEach Output:");
        fruits.stream()
            .filter(f -> f.startsWith("a"))
            .forEach(System.out::println);
    
        // 3. reduce: 문자열 결합
        String combined = fruits.stream()
            .reduce("", (a, b) -> a + ", " + b);
        System.out.println("Reduced String: " + combined);
    
        // 4. count: 요소 개수
        long count = fruits.stream()
            .filter(f -> f.length() > 5)
            .count();
        System.out.println("Count (length > 5): " + count);
    
        // 5. max: 최대값 찾기
        Optional<String> longest = fruits.stream()
            .max(Comparator.comparing(String::length));
        System.out.println("Longest Fruit: " + longest.orElse("None"));
    
        // 6. anyMatch: 조건 만족 여부
        boolean hasBerry = fruits.stream()
            .anyMatch(f -> f.contains("berry"));
        System.out.println("Has 'berry': " + hasBerry);
    }
    
    public static void parallelStreamExample() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) numbers.add(i);
    
        // 순차 스트림
        long startTime = System.currentTimeMillis();
        long sumSequential = numbers.stream()
            .map(n -> n * n)
            .reduce(0, Integer::sum);
        long endTime = System.currentTimeMillis();
        System.out.println("Sequential Sum: " + sumSequential + ", Time: " + (endTime - startTime) + "ms");
    
        // 병렬 스트림
        startTime = System.currentTimeMillis();
        long sumParallel = numbers.parallelStream()
            .map(n -> n * n)
            .reduce(0, Integer::sum);
        endTime = System.currentTimeMillis();
        System.out.println("Parallel Sum: " + sumParallel + ", Time: " + (endTime - startTime) + "ms");
    
        // 커스텀 ForkJoinPool
        ForkJoinPool customPool = new ForkJoinPool(2);
        try {
            long sumCustom = customPool.submit(() -> numbers.parallelStream()
                .map(n -> n * n)
                .reduce(0, Integer::sum)).get();
            System.out.println("Custom Pool Sum: " + sumCustom);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            customPool.shutdown();
        }
    }
    
    public static void enhancedStreamExamples() {
        // 스트림 생성
        Stream<String> mixedStream = Stream.concat(
            Stream.of("alpha", "beta"),
            Arrays.stream(new String[]{"gamma", "delta"})
        );
        System.out.println("Mixed Stream:");
        mixedStream.forEach(System.out::println);
    
        // 중간 연산 체이닝
        List<String> words = Arrays.asList("java", "stream", "api", "is", "awesome");
        List<String> processed = words.stream()
            .filter(w -> w.length() > 2)
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.toList());
        System.out.println("Processed Words: " + processed);
    
        // 최종 연산 복합 사용
        Map<Integer, Long> lengthCount = words.stream()
            .collect(Collectors.groupingBy(String::length, Collectors.counting()));
        System.out.println("Length Count: " + lengthCount);
    }
    
    public static void distinctExample() {
        List<Map<String, Object>> list = new ArrayList<>();
        
        // 예시 데이터 (Map.of 대신 HashMap 사용)
        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 1);
        map1.put("name", "Alice");
        list.add(map1);
    
        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", 2);
        map2.put("name", "Bob");
        list.add(map2);
    
        Map<String, Object> map3 = new HashMap<>();
        map3.put("id", 1);
        map3.put("name", "Charlie");
        list.add(map3);
    
        Map<String, Object> map4 = new HashMap<>();
        map4.put("id", 3);
        map4.put("name", "David");
        list.add(map4);
    
        // id 기준으로 중복 제거 (첫 번째 등장하는 항목 유지)
        List<Map<String, Object>> distinctList = list.stream()
                .collect(Collectors.toMap(
                        map -> map.get("id"), // 키: id
                        map -> map,           // 값: Map 객체
                        (existing, replacement) -> existing, // 중복 시 첫 번째 유지, 생략 시 기본값: 키 충돌 시 IllegalStateException 예외 발생
                        LinkedHashMap::new    // 순서 유지 생략 시 기본값: HashMap 생성.
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
    
        // 결과 출력
        System.out.println(distinctList);
    }
}



class Person {
    String name;
    int age;
    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}


/**
 * Product
 */
class Product  {
    int amount;
    String name;
    Product(int a,String b){
        this.amount = a;
        this.name = b;
    }
    public String getName() {
        return name;
    }
    public int getAmount() {
        return amount;
    }
}
