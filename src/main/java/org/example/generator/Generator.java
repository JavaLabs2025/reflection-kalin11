package org.example.generator;

import org.example.annotation.Generatable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.example.generator.util.ReflectionUtil.isClassOrParentAnnotated;

public class Generator {

    private static final Map<Class<?>, Supplier<?>> PRIMITIVE_TYPES_GENERATORS = Map.ofEntries(
            Map.entry(int.class, () -> new Random().nextInt(100)),
            Map.entry(Integer.class, () -> new Random().nextInt(100)),
            Map.entry(long.class, () -> new Random().nextLong(100)),
            Map.entry(Long.class, () -> new Random().nextLong(100)),
            Map.entry(double.class, () -> new Random().nextDouble(100)),
            Map.entry(Double.class, () -> new Random().nextDouble(100)),
            Map.entry(float.class, () -> new Random().nextFloat(100)),
            Map.entry(Float.class, () -> new Random().nextFloat(100)),
            Map.entry(boolean.class, () -> new Random().nextBoolean()),
            Map.entry(Boolean.class, () -> new Random().nextBoolean()),
            Map.entry(char.class, () -> (char) (new Random().nextInt(26) + 'a')),
            Map.entry(Character.class, () -> (char) (new Random().nextInt(26) + 'a')),
            Map.entry(String.class, () -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                int length = 10;
                char[] chars = new char[length];

                for (int i = 0; i < length; i++) {
                    boolean lower = random.nextBoolean();
                    char left = lower ? 'a' : 'A';
                    char right = lower ? 'z' : 'Z';
                    chars[i] = (char) random.nextInt(left, right + 1);
                }

                return new String(chars);
            })
    );

    public Object generateValueOfType(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        int randomConstructorIndex = new Random().nextInt(constructors.length);
        Constructor<?> randomConstructor = constructors[randomConstructorIndex];
        return randomConstructor.newInstance(111);
    }

    public Object generateByType(Class<?> clazz, String packageName) throws IllegalArgumentException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if (!isClassOrParentAnnotated(clazz, Generatable.class, packageName)) {
            throw new IllegalArgumentException(clazz.getCanonicalName() + " is not annotated with @" + Generatable.class.getCanonicalName());
        }

        return createInstance(clazz);
    }

    private static Object createInstance(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var constructors = clazz.getDeclaredConstructors();
        var randomConstructorIndex = constructors.length == 0 ? 0 : new Random().nextInt(constructors.length);
        var constructor = constructors[randomConstructorIndex];
        constructor.setAccessible(true);

        var paramTypes = constructor.getParameterTypes();
        var params = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            if (PRIMITIVE_TYPES_GENERATORS.containsKey(paramTypes[i])) {
                params[i] = PRIMITIVE_TYPES_GENERATORS.get(paramTypes[i]).get();
            } else {
                //todo а что если параметр - класс, помеченный аннотацией? надо рекурсивно создавать объекты до какой-то глубины
                params[i] = null;
            }
        }

        return constructor.newInstance(params);
    }
}

/*todo описание алгоритма работы генератора:
* 1) Проверить чем является переданный класс - классом или интерфейсом
*    + Если интерфейс, то:
*       а) Если ни одной реализации интерфейса нет, то выбросить исключение
*       б) Найти любую реализацию интерфейса и выбрать ее для создания объекта
*    + Если класс, то:
*       a) Дойти до максимального родителя (Object) и посмотреть есть ли у него аннотация Generatable
*         - Если нет, то выбросить исключение
* 2) Теперь мы выбрали класс, на основе которого будет создаваться объект
*
*
*
 */
