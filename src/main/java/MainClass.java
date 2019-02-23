import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class MainClass {

    public static void main(String[] args) {
        Class testedClazz = TestedClass.class;

        try {
            //start("TestedClass");
            start(testedClazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Запускаем тесты
     */
    public static void start(Object obj) throws IllegalAccessException, InstantiationException, ClassNotFoundException {

        Class<?> clazz = null;

        if(obj instanceof Class<?>) {
            clazz = (Class<?>)obj;
        } else if(obj instanceof String) {
            clazz = Class.forName((String) obj);
        } else {
            System.out.println("Incorrect argument int method start()");
            return;
        }

        Method[] methods = clazz.getDeclaredMethods();

        // Все методы @Test складываем в List сортируя по priority
        List<Method> methodTest =
                Arrays.stream(methods)
                        .filter(m -> (m.getAnnotation(Test.class) != null))
                        .sorted(MethodComp)
                        .collect(Collectors.toList());

        // Методы @BeforeSuite
        List<Method> beforeSuite = Arrays.stream(methods)
                .filter(m -> (m.getAnnotation(BeforeSuite.class) != null))
                .collect(Collectors.toList());

        // Методы @AfterSuite
        List<Method> afterSuite = Arrays.stream(methods)
                .filter(m -> (m.getAnnotation(AfterSuite.class) != null))
                .collect(Collectors.toList());

        // Создать инстанс тестируемого класса
        Object instance = clazz.newInstance();

        try {
            // Должно присутствовать по одному методу с
            // аннотациями @BeforeSuite и @AfterSuite
            if (beforeSuite.size() != 1 || afterSuite.size() != 1) {
                throw new RuntimeException("One or more required methods are missing");
            }

            // Далее вызываются все методы с аннотациями.
            // Аргументы генерим через класс DataSource.

            // @BeforeSuite
            methodCaller(instance, beforeSuite.get(0), beforeSuite.get(0).getAnnotation(BeforeSuite.class).toString());

            // @Test
            for (Method m : methodTest) {
                methodCaller(instance, m, m.getAnnotation(Test.class).toString());
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            // @AfterSuite
            if (afterSuite.size() == 1) {
                methodCaller(instance, afterSuite.get(0), afterSuite.get(0).getAnnotation(AfterSuite.class).toString());
            }
        }
    }

    /**
     * TODO: Вызыватель метода.
     * TODO: Аргументы генерим через класс DataSource
     */
    public static void methodCaller(Object instance, Method m, String annotation) {

        Class<?>[] types = m.getParameterTypes();
        Object[] args = new Object[types.length];

        // Сгенерить массив аргументов
        for (int i = 0; i < types.length; i++) {
            args[i] = DataSource.genArg(types[i].getSimpleName());
        }

        // Проверка приватного доступа
        if (Modifier.isPrivate(m.getModifiers())) {
            m.setAccessible(true);
        }

        System.out.println("Trying to call method:\n\t"
                + annotation + "\n\t"
                + m.getReturnType().getSimpleName()
                + " " + m.getName() + ":" + Arrays.asList(args)
        );

        try {
            // Вызов метода и печать результата
            System.out.println("Result:\n\t" + m.invoke(instance, args) + "\n");

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Comparator для сравнения методов по значению
     * priority аннотации @Test
     */
    public static Comparator<Method> MethodComp = new Comparator<Method>() {
        @Override
        public int compare(Method m1, Method m2) {
            return Integer.compare(m1.getAnnotation(Test.class).priority(), m2.getAnnotation(Test.class).priority());
        }
    };
}

/**
 * Генератор данных притивных типов и строк
 */
class DataSource {

    private static final Random random = new Random();

    static byte genByte() {
        return (byte) random.nextInt(100);
    }

    static short genShort() {
        return (short) random.nextInt(100);
    }

    static int genInt() {
        return random.nextInt(100);
    }

    static long getLong() {
        return random.nextLong();
    }

    static float getFloat() {
        return random.nextFloat();
    }

    static double genDouble() {
        return random.nextDouble();
    }

    static String getString() {
        String[] strings = {
                "Ольга", "Иван", "Андрей", "Артем"
        };
        return strings[random.nextInt(strings.length)];
    }

    public static Object genArg(String type) {
        Object obj = new Object();

        switch (type) {
            case "byte":
                obj = DataSource.genByte();
                break;
            case "short":
                obj = DataSource.genShort();
                break;
            case "int":
                obj = DataSource.genInt();
                break;
            case "long":
                obj = DataSource.getLong();
                break;
            case "float":
                obj = DataSource.getFloat();
                break;
            case "double":
                obj = DataSource.genDouble();
                break;
            case "String":
                obj = DataSource.getString();
                break;
        }
        return obj;
    }
}
