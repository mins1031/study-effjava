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

      
