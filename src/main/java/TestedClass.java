import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface BeforeSuite {
}
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface AfterSuite {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Test {
    int priority();
}

public class TestedClass {

    @BeforeSuite
    public void beforeTest(){
        System.out.println("Start test");
    }

    @AfterSuite
    public void afterTest(){
        System.out.println("Start executed");
    }

    @Test(priority = 3)
    public int methodMul(int a, int b) {
        return a * b;
    }

    @Test(priority = 1)
    public int methosAdd(int a, int b) {
        return a + b;
    }

    @Test(priority = 7)
    public int methodInc(int a) {
        return (a + 1);
    }

    @Test(priority = 2)
    public int methodDec(int a) {
        return (a - 1);
    }

    @Test(priority = 6)
    public void printWelcome(String name) {
        System.out.println("Привет, " + name + "!");
    }

    @Test(priority = 10)
    public float calculate(float a, float b, float c, float d) {
        return a * (b + (c / 2) * d);
    }
}
