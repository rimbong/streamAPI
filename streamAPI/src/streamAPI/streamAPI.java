package streamAPI;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
        stream
    중간 연산
        filter, map, limit, sorted
    최종 연산
        collect, count, average, reduce, anyMatch, forEach .... 

    return type이 Stream 이라면 중간 처리 메소드이고, 
    return type이 기본타입 or Optional___ 라면 최종 처리 메소드이다.
*/

/* 처리과정
    - 중간 스트림이 생성될 때 요소들이 바로 처리되는 것이 아님.
        중요) 최종 처리가 시작되기 전까지 중간처리는 지연(lazy)되며, 최종 처리가 시작되면 중간 스트림에서 처리를 시작한다.
    - stream은 한번 사용하면 재사용이 불가능
 */
/* 1급 객체
    - 변수나 데이타에 할당 할 수 있어야 한다.
    - 객체의 인자로 넘길 수 있어야 한다.
    - 객체의 리턴값으로 리턴 할수 있어야 한다.
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

public class streamAPI {
    public static void main(String[] args) {
        test7();
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
                .filter(s -> s.startsWith("b")).findFirst();
        System.out.println("findFirst: " + firstElement.get());

        Optional<String> anyElement = productList.stream()
                .map(Product::getName)
                .filter(s -> s.startsWith("b")).findAny();
        System.out.println("findAny: " + anyElement.get());

        Optional<String> anyElement2 = productList.stream().map(Product::getName).parallel()
                .filter(s -> s.startsWith("b")).findAny();        
        System.out.println("findAny2: " + anyElement2.get());
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
