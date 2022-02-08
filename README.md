# study-effjava
## Item 1 생성자 대신 정적팩토리 메서드를 고려하라
* 클라이언트가 클래스의 인스턴스를 얻는 전통적 수단은 public 생성자를 통해 new 연산으로 생성하는 것이다.
* 그러나 클래스는 생성자와 별개로 해당 클래스의 인스턴스를 반환하는 정적인 메서드를 제공할수 있고 이것을 **정적 팩토리 메서드**라고 한다
* 클래스는 클라이언트에 public 생성자 대신(혹은 생성자와 함께) 정적 팩토리 메서드를 제공할 수 있다. 여기엔 당연하게 장점과 단점이 있다
* 장점
  1) 이름을 가질 수 있다
    * 생성자에 넘기는 매겨변수와 생성자 자체로는 반환될 인스턴스의 특성을 재대로 설명하지 못한다
    * 반면 정적 팩터리 메서드는 반환 인스턴스의 특성을 이름으로 쉽게 설명할수 있다
    * ex) Member.createMember(...), BigInteger.probablePrime(...)
  2) 호출 될떄 마다 인스턴스를 새로 생성하지 않아도 된다
    * 대표적으로 Boolean.valueOf() 메서드는 Boolean 객체를 아예 생성하지 않는다(static 메서드기에) 
    * 따라서 같은 객체가 자주 요청되는 상황이라면 성능을 크게 향상 시킬수 있다. 
  3) 반환타입의 하위타입 객체를 반환할 수 있다.
  * 자바의 다형성의 특징을 이용해 유연한 구현을 할 수 있다.
  ```
  public interface Type{
     static Type getAType(){
       retrurn new AType;
     }
     static Type getBType(){
       retrurn new BType;
     }
  }
  
  class AType implement Type{
  ...
  }
  class BType implement Type{
  ...
  }
  ```
  * 위와같이 Type 인터페이스와 이 인터페이스의 구현체인 AType,BType이 있다.
  * get메서드들을 보면 메서드의 리턴타입은 Type 인터페이스지만 리턴값은 인터페이스의 하위클래스인것을 알 수 있다.
  * 또한 이렇게 구현하면 사용자는 AType을 얻기위해 많은 정보가 필요없이 Type.getAType()만 해준다면 어려움없이 AType을 얻어 낼수 있기에 인터페이스의 구현체를 노출시킬 필요없고(캡슐화) 사용자입장에서도 손쉽게 사용할 수 있다는 장점이 있다.
  4) 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다
    * 반환타입의 하위타입이기만 하면 어떤 클래스의 객체를 반환하든 상관없다.\
    ```
    public class Foo{
       public static Foo getFoo(boolean flag){
          return flag ? new TestFoo() : new BarFoo();
       }
       
       static class BarFoo extend Foo{
       ...
       }
       static class TestFoo extend Foo{
       ...
       }
       
       public static void main(String[] args){
          Foo fool1 = Foo.getFoo(ture); // TestFoo
          Foo fool2 = Foo.getFoo(false); // BarFoo
       } 
    }
    ```
    * 위의 코드를 보면 flag에 따라 리턴값이 달라지는 것을 볼 수 있다. 이렇게 유연하게 구조화가 가능하다
    * 또 다른예론 Level 클래스를 상속받은 Basic,Advanced,Intermediate 클래스 3개가 있고 Level의 of메서드의 매개변수로 성적을 입력하면 성적에 맞는 등급의 인스턴스를 반환해주는 방식도 예로 들수 있다.  
  5) 정적 팩토리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다
  * 이런 유연함은 서비스 제공자 프레임워크를 만드는 근간이 된다. 대표적으로 JDBC가 있다
  * 이부분 보충필요 자료더 찾아 볼것.
* 단점
  1) 상속을 하려면 public이나 protected 생성자가 필요하니 정적팩토리 메서드만 제공하면 하위 클래스를 만들수 없다.
    * 이 제약은 상속보다 컴포지션을 사용하도록 유도하고 불변 타입으로 만드려면 이 제약을 지켜야 한다는 점에서 오히려 장점으로 받아들일 수도 있다.
  2) 정적팩터리 메서드는 개발자가 찾기 어렵다
    * 생성자 처럼 API 설명에 명확히 드러나지 않아 사용자는 정적 팩토리 메서드 방식 클래스를 인스턴스화 할 방법을 알아내야 한다. 
    * 이건 정적 팩토리 메서드의 이름에 대한 컨밴션을 만들어 널리 사용되는 것으로 어느정도 보완하고 있다
      * from : 매개변수를 하나 받아서 해당 타입의 인스턴스를 반환하는 형변환 메서드  ex) Date d = Date.from(instant); 
      * of: 여러 매개변수를 받아 적합한 타입의 인스턴스를 반환하는 집계 메서드 
      * valueOf: from과 of의 더 자세한 버전
      * instance or getInstance : (매개변수를 받는다면) 매개변수로 명시한 인스턴스를 반환하지만 같은 인스턴스임을 보장하지 않는다 ex)StackWalker luke = StackWalker.getInstance(option);
      * create or newInstance : instance or getInstance 와 같지만 매번 새로운 인스턴스를 생성해 반환함을 보장한다.
      * getType: getInstance와 같으나 생성할 클래스가 아닌 다른 클래스에 팩터리메서드를 정의할떄 쓴다.
      * newType: newInstance와 같으나 생성할 클래스가 아닌 다른 클래스에 팩터리메서드를 정의할떄 쓴다.
      * type : getType과 newType의 간결한 버전
    
## Item 2 생성자에 매개변수가 많다면 빌더를 고려하라
> 정적팩토리 매세드와 생성자는 똑같은 제약이 있는데 바로 선택적 매개변수가 많은 경우 적절히 대응하기 어렵다는 점이다.
- **식품 포장의 영양정보를 표현하는 클래스가 있다고 했을때 영양정보는 20가지가 넘는 항목들이 있지만 모두 필수가 아닌 선택적 항목들이다. 즉 필드가 null이어도 상관없다는 의미이다**
- 그렇다면 이런 상황은 어떻게 해결 해야 할까 
1) 점층적 생성자 패턴
  - 기존엔 점층적 생성자 패턴을 즐겨 사용했다. 점층적 생성자 패턴이란 매개변수 별로 생성자를 만들어 놓는 방식이다 ex) 필수 항목 생성자, 필수 항목 + 선택항목1 생성자, 필수 항목 + 선택항목2개 생성자......모든 필드항목 생성자.
  - 점층적 생성자 패턴은 매개변수 갯수가 많아지면 클라이언트 코드를 작성하거나 읽기가 어려워진다. 
    - 코드를 읽을때 각 값의 의미가 무엇인지 헷갈린다
    - 생성시 매개변수가 몇개인지도 꼼꼼히 세어봐야 할 것이다
    - 타입이 같은 매개변수가 연달아 늘어서 있으면 착각해 찾기 어려운 버그로 이어질 가능성도 있다
    - 가장 치명적인건 클라이언트가 실수로 매개변수의 순서를 바꿔 건네줘도 컴파일러는 알지 못하고 결국 런타임시 오류를 내버릴수 있다. 
 ```
 public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public NutritionFacts(int servingSize, int servings) {
        this(servingSize, servings, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories) {
        this(servingSize, servings, calories, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat) {
        this(servingSize, servings, calories,  fat, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
        this(servingSize, servings, calories,  fat,  sodium, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
        this.servingSize = servingSize;
        this.servings = servings;
        this.calories = calories;
        this.fat = fat;
        this.sodium = sodium;
        this.carbohydrate = carbohydrate;
    }
}
 ```
2) 자바빈즈 패턴
  - 자바빈즈 패턴은 매개변수가 없는 디폴트 생성자로 객체를 생성하고 세터 메서드를 호출해 원하는 매개변수의 값을 설정하는 방식이다.
  - 자바빈즈 패턴은 심각한 단점이 하나 있는데 `객체를 만들려면 메서드를 여려개 호출해야 하고 객체가 완전히 생성되기 전까지는 '일관성'이 무너진 상태에 놓이게 된다. 즉 값을 보장하지 못하는 상태이다`
  - 일관성이 깨진 객체가 만들어지면 버그를 심은 코드와 버그로 문제가 일어나는 코드가 물리적으로 멀리 떨어져 있을 것이므로 디버깅도 쉽지 않아진다..
```
public class NutritionFacts {
    private int servingSize = -1;
    private int servings = -1;
    private int calories = 0;
    private int fat = 0;
    private int sodium = 0;
    private int carbohydrate = 0;

    public NutritionFacts() {}

    public void setServingSize(int servingSize) {
        this.servingSize = servingSize;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public void setSodium(int sodium) {
        this.sodium = sodium;
    }

    public void setCarbohydrate(int carbohydrate) {
        this.carbohydrate = carbohydrate;
    }
}
```
3) 빌더 패턴
  - 빌더 패턴은 점층적 생성자 패턴의 안정성과 자바빈드 패턴의 가독성을 겸비했다. 
  - 클라이언트는 필요한 객체를 직접만드는 대신 필수 매개변수만으로 생성자를 호출해 빌더 객체를 얻는다. 그 다음 빌더가 제공하는 세터 메서드들로 원하는 선택 매개변수를 설정하고 마지막으로 매개변수가 없는 build메서드를 호출해 필요한(보통 불변인) 객체를 얻게 된다
```
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    private NutritionFacts(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }

    public static class Builder {
        // 필수 매개변수
        private final int servingSize;
        private final int servings;

        // 선택 매개변수
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrate = 0;

        // 필수 매개변수만을 담은 Builder 생성자
        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        // 선택 매개변수의 setter, Builder 자신을 반환해 연쇄적으로 호출 가능
        public Builder calories(int val) {
            calories = val;
            return this;
        }

        public Builder fat(int val) {
            fat = val;
            return this;
        }
        
        public Builder sodium(int val) {
            sodium = val;
            return this;
        }
        
        public Builder carbohydrate(int val) {
            carbohydrate = val;
            return this;
        }
        
        // build() 호출로 최종 불변 객체를 얻는다.
        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }
 ```
 - NutritionFacts는 불변이고 모든 매개변수의 기본값을 한곳에 모아뒀다. 
 - 또한 빌더의 세터메서드들은 빌더 자신을 반환하기때문에 연쇄적으로 호출할 수 있다. 이런방식을 메서드 호출이 흐르듯 연결된다는 뜻으로 **플루언트API or 메서드 연쇄**라고 한다.\
 ```
 NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8).calories(100).sodium(35).carbohydrate(27).build()
 ```
 - 위의 클라이언트 코드는 쓰기 쉽고 무엇보다도 읽기 쉽다.
 - 생성자로는 누릴수 없는 사소한 이점으로 **빌더를 사용하면 기변인수 매개변수를 여러개 사용할 수 있다.**
 - **위 코드처럼 각각을 적절한 메서드로 나눠 선언하면 된다. 아니면 한 메서드를 여러번 호출하도록 하고 각 호출때 넘겨진 매개변수들을 하나의 필드(예를들면 set? list?)로 모을 수도 있다**
 - 빌더 패턴은 상당히 유연하다. 빌더 하나로 여러 객체를 순회하면서 만들수 있고? 빌더에 넘기는 매개변수에 따라 다른 객체를 만들 수도 있다(예를 들어 매개변수 boolean이 true냐 false냐에 따라 다른 구현체를 혹은 다른 자식 클래스를 리턴하는 메서드를 구성할 수 있다. -> 이부분은 좀 생각해 볼 필요가 있을듯 하다)
 - 다만 빌더 패턴에 장점만 있는 것은 아니다.
 - 객체를 만들려면 빌더부터 만들어야 하는데 빌더 생성 비용이 크지는 않지만 성능에 민감한 상황에서는 문제가 될 수 있다. 
 - 또한 다른 패턴들 보다는 코다가 장황해서 매개변수가 최소 4개 이상은 되어야 값어치를 한다.
 - 하지만 API는 시간이 지날수록 매개변수가 많아지는 경향이 있다는 것을 생각해볼때 처음부터 빌더 패턴으로 구현하는 것도 좋은 선택지가 될수 있다는 생각이 든다.

## Item3 private생성자나 Enum타입으로 싱글텅임을 보증하라
- 싱글턴이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말한다.
- 싱글턴의 전형적인 예로는 함수같은 무상태객체나 설계상 유일해야하는 시스템 컴포넌트를 들수 있다(스프링 빈 처럼)
- 그런데 클래스를 싱글턴으로 만들면 이를 사용하는 클라이언트를 테스트하기가 어려워질 수 있다. -> 싱글턴 인스턴스를 Mock구현으로 대체할 수 없기 때문에
- 싱글턴을 만드는 방식은 크게 두가지이다
1) public static 멤버가 final필드인 방식 (public 필드 방식)
```
public class Elvis {
   public static final Elvis INSTANCE = new Elvis();
   private Elvis() {...}
   ....
}
```
  - private 생성자는 public static final필드인 Elvis.Instance를 초기화할때 딱 한번만 호출된다.
  - public이나 protected생성자가 없으므로 Elvis 클래스가 초기화될때 만들어진 인스턴스가 시스템에서 하나 뿐임이 보장된다
  - 다만 권한이 있는 클라이언트가 리플렉션API을 사용해 private생성자를 호출할 수 있어 문제가 있다
  - public 필드 방식의 큰 장점은 해당 클래스가 싱글턴인 것이 API에 명백히 드러난다는 점과 간결함이다

2) 정적팩터리 메서드 방식
```
public class Elvis {
   private static final Elvis INSTANCE = new Elvis();
   private Elvis() {...}
   public static Elvis getInstance() {
      return INSTANCE;
   }
   ....
}
```
  - Elvis.getInstance는 항상 같은 객체를 반환함으로 시스템에서 하나뿐임을 보장할 수 있다.
  - 정적팩터리 방식의 첫번째 장점은 API를 바꾸지 않고도 싱글턴이 아니게 변경할 수 있다는 점이다. 유일한 인스턴스를 반환하던 팩터리 메서드가 호출하는 스레드별로 다른 인스턴스를 넘겨주게 할 수도 있다
  - 두번째 장점은 원한다면 정적팩터리를 제네릭 싱글턴 팩터리로 만들 수 있다는 점이다.
  - 세번쨰 장점은 정적 팩터리의 메서드 참조를 공급자로 사용할 수 있다는 점이다.
  - 그러나 위의 장점들이 굳이 필요없다면 public필드 방식이 더 좋다
3) Enum타입
```
public enum Elvis {
   INSTANCE;
   .....
}
```
  - public 필드 방식과 비슷하지만 더 간결하고 추가 노력없이 직렬화 할 수 있고 아주 복잡한 직렬화 상황이나 리플렉션 공격에서도 제2의 인스턴스가 생기는 일을 완벽히 막아준다
  - 대부분의 상황에서는 원소가 하나뿐인 enum타입이 싱글턴을 만드는 가장 좋은 방법이다. 단, 만들려는 싱글턴이 Enum외의 클래스를 상속해야 한다면 이방식은 사용할 수 없다
  
## item4 인스턴스화를 막으려면 private 생성자를 사용하자
- 단순히 정적 메서드와 정적 필드만을 담은 클래스를 만들어야 할때가 있다. 객체지향적으로 좋지 않은 방식이지만 필요한순간들이 이따금 있다(있다고 한다... 내 경험엔 입출력만을 담당하는 클래스의 메서드, 필드들에 대한 클래스를 구성시 위와 같이 구현했다.)또한 정적팩토리 메서드를 모아놓았을 수도 있다. 마지막으로 final클래스와 관련된 메서드들을 모아놓을때도 사용한다. final클래스를 상속해서 하위 클래스에 메서드를 넣는 것은 불가능하기 때문이다.
- 정적 멤버만 담은 유틸리티 클래스는 인스턴스로 만들어 사용하려는게 아닌 사용시마다 객체 생성없이 꺼내 사용하려고(아마..? 대부분..?) 설계한다. 하지만 생성자를 명시하지 않으면 컴파일러가 자동으로 기본 생성자를 만들어진다 즉, 매게변수가 없는 생성자가 만들어지게 되고 상속해서 사용하게 되는 일도 생기게 된다.
- 인스턴스화를 막는 간단한 방법은 private생성자를 직접 클래스 안에 생성하면 된다. 
- 명시적 생성자가 private이니 클래스 바깥에선 접근조차 할 수 없다.
```
public class UtilityClass {
//인스턴스화 방지용 생성자
    private UtilityClass() {
        throw new AssertionError();
    }
}
```
- 위 코드는 어떤 환경에서도 UtilityClass클래스가 인스턴스화 되는 것을 막아준다. 다만 생성자가 있는데 호출할 수 없는건 조금 이상하니 주석을 달아주는 습관을 들이는게 좋을듯 하다.
- 또한 상속조차 막아주니 완벽한... 독립 객체가 된다.

## Item5 의존객체 주입을 사용하라
- 많은 클래스가 하나 이상의 자원에 의존한다. 
- 사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나, 싱글턴 방식이 적합하지않다.
- 대신 클래스가 여러 자원 인스턴스를 지원해야 하며 클라이언트가 원하는 자원을 사용해야 한다. 
- 이 조건을 만족하는 간단한 패턴이 바로 인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨주는 방식이다.
```
public class SpellChecker {
    private final Lexicon dictionary;
    
    public SpellChecker(Lexicon dictionary) {
        this.dictionary = Object.requireNonNull(dictionary);
    }
    
   .....
}
```
- 위에선 dictionary라는 하나의 자원만 사용하지만 자원이 몇개든 의존관계가 어떻든 상관없이 잘 작동한다. 또한 불변을 보장하여 여러 클라이언트가 의존 객체들을 안심하고 공유할 수 있기도 하다(이건 생성시에만 자원이 주입되므로 -> 세터나 다른 인터페이스로 의존하는 값이 바뀌지 않는것이 보장되는 패턴이라는 말인것 같다.)
- 이 패턴의 쓸만한 변형으로 생성자에 자원팩토리를 넘겨주는 방식이 있다.(팩토리란 호출시마다 특정 타입의 인스턴스를 반복해서 만들어주는 객체를 말한다.)
- 의존 객체 주입이 유연성과 테스트 용이성을 개선해주긴 하지만 의존성이 수천개나 되는 큰 프로젝트에선 코드가 어지럽다.-> 스프링 쓰자!

## item 6 불필요한 객체 생성을 피해라
- 똑같은 기능의 객체를 매번 생성하기 보다는 객체 하나를 재사용하는 편이 나을때가 많다. 재사용은 빠르고 세련되었다. 특히 불변객체는 언제든 재사용할 수 있다.
```
// 밑의 코드는 하면 안되는 극단적인 예시이다
String s = new String("hi");
//이 문장은 실행될때마다 String인스턴스를 만들어낸다. 잘못된다면 쓸데없는 인스턴스가 수백개 만들어질수도 있다.
//반면
String s = "hi";
//위 코드는 새로운 인스턴스를 만들지 않고 한개의 String타입의 'hi'라는 인스턴스를 가지고 같은 jvm안에서 'hi'라는 문자열을 사용한다면 같은 객체를 사용하는것이 보장된다.
```
- 이번내용을 "객체생성은 비싸니 피해야 한다"로 오해하면 안된다. 객체지향적, 간결성, 기능을 위해서 객체를 추가 생성해야한다면 좋은 방법이다. 또 최신 jvm의 가비지 컬렉터의 성능을 작은 객체를 생성, 회수하는일에 큰 부담을 가지지 않는다
- 박싱된 기본 타입을 사용하고 의도치 않은 오토 박싱이 사용되지 않도록 주의해야한다
- 다만 무조건 기본타입을 지향하는 건 어리석다. 소프트웨어의 세계엔 정답은 없다. 만약 가격에 대한 변수를 만들때 가격이 0값이 있는 경우 가격이 0인지 가격이 할당되지 않았는지를 판별할 순 없다(보통은 가격이 0이라고 생각하겠지만 그럼에도 의미는 좋지않다.)
- 결국 가격이 할당되지 않은 것을 표현하려면 'int price' = 0 보단 'Integer price = null'이 훨씬 안전하고 의미도 정확하다

## item 7 다 쓴 객체 참조를 해제하라
> 예시가 굉장히 극단적인 예시지만 일단 해당 아이템의 예제 Stack을 본다. 그냥 보면 문제가 없어 보이지만 '메모리 누수'가 발생하는 곳이 있다. 방치하면 성능이 계속 저하 될 것이고 심각하면 OutOfMemory 에러를 일으켜 서버를 다운시킬 것이다.
- 예제에서 메모리 누수가 발생하는 지점은 어디일까? 바로 pop부분이다. 해당 객체의 size만 줄일뿐 리턴된 객체가 사라지지 않는다. 그런데 가비지 컬랙터는 프로그램에서 더이상 사용하지 않아도 '닫지 않는 객체'가 되어 버린 객체를 회수하지 않는다.
- 이유는 스택이 그 객체의 참조를 계속 가지고 있기 때문이다. 이렇게 메모리 누수를 찾기가 쉽지가 않다.
- 그럼 해결 방법은? 쉽다 안쓰는 객체가 있다면 가비지 컬렉터에게 알리기 위해 null로 초기화 해주면된다. 또한 null처리하게 되면 실수로 접근하더라도 NullPoint예외를 내주기에 안전해진다
- 하지만 모든 미활용 객체를 null로 초기화하는 로직이 있다면 코드가 많이 지저분해 질것이다. 그럼 null처리는 언제해주는게 좋을까.
- 일반적으로는 '자기 메모리를 직접 관리하는 클래스라면 개발자는 항시 메모리 누수에 주의해야 한다.' 스택을 보면 elements라는 배열을 미리 16개 만들어서 사용하고 있고 이는 스택 객체만의 독자적인 메모리 풀을 가지고 있는 것이다.
- 메모리 누수는 발견하기가 쉽지 않아 예방법을 익혀두는게 좋고 가비지 컬렉터와 jvm의 동작방식과 구조는 다시 찬찬히 공부해봐야겠다.

## item 8 finalizer와 cleaner 사용을 피해라
> 우선 전에 스레드 공부하며 잠깐 봤던 것 외에는 finalizer와 cleaner를 본적도, 사용해본적도 거의 없다. finalizer와 cleaner는 객체 소멸자 라고 하는 내용인데 finalizer의 경우 자바 9 부터 사용자제 API가 되었다
- 둘의 단점은 예측할 수 없고 느리고 일반적으로 불필요하다는 단점들이 있다.
- 우선 두개는 언제 실행될지 가비지 컬랙터의 알고리즘 전략에 따라 달라져 실행 시점 예측 자체가 어렵다고 한다.
- 웬만하면 다루지 않는 것이 좋다

## item 10 equals는 일반 규약을 지켜 재정의하라
> equals 메서드는 재정의 하기 쉬워보이지만 실상은 그렇지 않다. 문제를 회피하는 가장 쉬운길은 아예 재정의하지 않는 것인데 아래 열거한 상황중 하나에 해당하면 재정의하지 않는 것이 맞다
  - 각 인스턴스가 본질적으로 고유하다 : 값을 표현하는게 아니라 동작하는 개체를 표현하는 클래스거 여기 해당한다. Thread가 좋은 예로 이미 Object에서 이러한 객체들에 알맞게 구현이 되어있다.
  - 인스턴스의 '논리적 동치성(값이 맞는지 아닌지)'을 검사할 일이 없는 경우
  - 상위 클래스에서 재정의한 eqauls가 하위 클래스에도 딱 들어맞는다.
  - 클래스가 private 이거나 equals 메서드를 호출할 일이 없다.
- 그럼 언제 equals 메서드를 재정의 해야 할까? 그건 객체 식별성이 아닌 논리적 동치성을 확인해야 하는데 상위 클래스의 equals 가 논리적 동치성을 비교하게 구현되어있지 않은 경우에 재정의 해야한다. 주로 값클래스들이 이에 해당한다.
- 그러나 값 클래스라고 해도 값이 같은 인스턴스가 둘 이상 만들어지지 않음을 보장하는 인스턴스 통제 클래스라면 equals 를 재정의하지 않아도 된다. 대표적으로 enum이다.
- equals 메서드를 재정의할땐 반드시 아래 일반규약을 따라야 한다. 아래 내용은 Object명세에 적힌 규약이다
  - equals 메서드는 동치관계를 구현하며 다음을 만족한다
  1) 반사성: null이 아닌 모든 참조값 x에 대해 x.equals(x) 는 true다
  2) 대칭성: null이 아닌 모든 참조값 x,y에 대해 x.equals(y)가 true면 y.equals(x)도 true다
  3) 추이성: null이 아닌 모든 참조값 x,y,z에 대해 x.equals(y), y.equals(z)가 true면 x.equals(z)도 true다
  4) 일관성: null이 아닌 모든 참조값 x,y에 대해 x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환한다
  5) null-아님: null이 아닌 모든 참조값 x에 대해 x.equals(null)은 false다.
- 책에선 위의 규약 하나하나에 대한 예시와 부가 설명을 진행하지만 다 정리하면 내용이 너무 많아지지만 다행이도 해당 장의 마지막 부분에 해당 내용들을 바탕으로 양질의 equals 메서드 재정의 구현 방법을 정리한다
  1) == 연산자를 사용해 입력이 자기 자신의 참조인지 확인한다. : 자기 자신이면 true를 반환한다. 단순히 성능 향상용이다.
  2) instanceof 연산자로 입력이 올바른 타입인지 확인한다. : 보통 null이 인자로 입력되도 알아서 false를 리턴해준다.
  3) 입력을 올바른 타입으로 형변환한다: 2번을 구현했을때 무조건 통과하는 단계이다
  4) 입력 객체와 자기자신의 대응되는 '핵심'필드들이 모두 일치하는지 하나씩 검사한다.
  + 번외로 equals 메서드의 인자 타입은 Object로 하는것이 좋다
