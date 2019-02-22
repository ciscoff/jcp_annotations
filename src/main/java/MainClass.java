import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MainClass {

    public static void main(String[] args) {
        Class testedClazz = TestedClass.class;
        start(testedClazz);
    }

    public static void start(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();

        // Складываем тестируемые методы и их метаданные в список отсортированный
        // по значению priority аннотации Test
        List<MetaData> meta =
                Arrays.stream(methods)
                        .filter(m -> (m.getAnnotation(Test.class) != null))
                        .map(m -> new MetaData(m, m.getAnnotation(Test.class)))
                        .sorted(AnnoComp)
                        .collect(Collectors.toList());

        // Проверка сортировки
//        for (MetaData m : meta) {
//            System.out.println(m.getAnno().priority());
//        }

        TestedClass ts = new TestedClass();

        // Вызываем методы из списка.
        // Аргументы генерим сами
        for (MetaData md : meta) {
            Method m = md.getMethod();
            Class<?>[] types = m.getParameterTypes();
            Object[] args = new Object[types.length];

            for (int i = 0; i < types.length; i++) {
                args[i] = DataSource.genArg(types[i].getSimpleName());

//                switch (types[i].getSimpleName()) {
//                    case "int":
//                        args[i] = DataSource.genInt();
//                        break;
//                    case "long":
//                        args[i] = DataSource.getLong();
//                        break;
//                    case "float":
//                        args[i] = DataSource.getFloat();
//                        break;
//                    case "double":
//                        args[i] = DataSource.genDouble();
//                        break;
//                    case "String":
//                        args[i] = DataSource.getString();
//                        break;
//                    default:
//                        args[i] = new Object();
//                }
            }

            try {
                System.out.println("Trying to call method:\n\t"
                        + m.getReturnType().getSimpleName()
                        + " " + m.getName() + ":" + Arrays.asList(args)
                );

                System.out.println("Result:\n\t" + m.invoke(ts, args) + "\n");
//                m.invoke(ts, args);

            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Comparator для сравнения аннотаций по полю priority
     */
    public static Comparator<MetaData> AnnoComp = new Comparator<MetaData>() {
        @Override
        public int compare(MetaData m1, MetaData m2) {
            return Integer.compare(m1.getAnno().priority(), m2.getAnno().priority());
        }
    };
}

/**
 * Контейнер для хранения метода и его аннотации
 */
class MetaData {
    private Method method;
    private Test anno;

    public MetaData(Method method, Test anno) {
        this.method = method;
        this.anno = anno;
    }

    public Method getMethod() {
        return method;
    }

    public Test getAnno() {
        return anno;
    }
}

/**
 * Генератор данных притивных типов и строк
 */
class DataSource {

    private static final Random random = new Random();

    public static byte genByte() {
        return (byte)random.nextInt(100);
    }

    public static short genShort() {
        return (short)random.nextInt(100);
    }

    public static int genInt() {
        return random.nextInt(100);
    }

    public static long getLong() {
        return random.nextLong();
    }

    public static float getFloat() {
        return random.nextFloat();
    }

    public static double genDouble() {
        return random.nextDouble();
    }

    public static String getString() {
        String[] strings = {
                "Ольга", "Иван", "Андрей", "Артем"
        };
        return strings[random.nextInt(strings.length)];
    }

    public static Object genArg(String type) {
        Object obj = new Object();

        switch (type){
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
