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

      
