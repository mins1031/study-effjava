package effective.item11;

public class PhoneNumberTest {
    public static void main(String[] args) {
        short s1 = 707;
        short s2 = 800;
        short s3 = 5309;

        short s12 = 20;
        short s22 = 30;
        short s32 = 200;

        PhoneNumber p1 = new PhoneNumber(s1, s2, s3);
        PhoneNumber p2 = new PhoneNumber(s12, s22, s32);

        System.out.println(p1.hashCode());
        System.out.println(p2.hashCode());

        System.out.println(new PhoneNumber(s1, s2, s3).hashCode());
        System.out.println(new PhoneNumber(s1, s2, s3).hashCode());
    }
}
