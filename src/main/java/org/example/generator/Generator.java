package org.example.generator;

import org.example.annotation.Generatable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static org.example.generator.util.ReflectionUtil.getAnnotatedClass;

public class Generator {

    private static final int MAX_DEPTH = 2;

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
            Map.entry(String.class, getRandomString())
    );

    private static final Set<Class<?>> IMMUTABLE_KEY_TYPES = PRIMITIVE_TYPES_GENERATORS.keySet();

    public Object generateByType(Class<?> clazz, String packageName) throws IllegalArgumentException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        var annotatedClass = getAnnotatedClass(clazz, Generatable.class, packageName);

        if (annotatedClass.isEmpty()) {
            throw new IllegalArgumentException(clazz.getCanonicalName() + " is not annotated with @" + Generatable.class.getCanonicalName());
        }

        return createInstance(annotatedClass.get(), 0);
    }

    private static Object createInstance(Class<?> clazz, int depth) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (PRIMITIVE_TYPES_GENERATORS.containsKey(clazz)) {
            return PRIMITIVE_TYPES_GENERATORS.get(clazz).get();
        }

        if (depth > MAX_DEPTH) {
            return null;
        }

        if (Collection.class.isAssignableFrom(clazz)) {
            return new ArrayList<>();
        }

        if (Map.class.isAssignableFrom(clazz)) {
            return new HashMap<>();
        }

        var constructors = clazz.getDeclaredConstructors();
        var randomConstructorIndex = constructors.length == 0 ? 0 : new Random().nextInt(constructors.length);
        var constructor = constructors[randomConstructorIndex];
        constructor.setAccessible(true);

        var paramTypes = constructor.getParameterTypes();
        var params = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            params[i] = createInstance(paramTypes[i], depth + 1);
        }

        var instance = constructor.newInstance(params);

        for (var field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            var fieldType = field.getType();
            Object value;
            if (Collection.class.isAssignableFrom(fieldType)) {
                value = generateCollection(field, depth + 1);
            } else if (Map.class.isAssignableFrom(fieldType)) {
                value = generateMap(field, depth + 1);
            } else if (PRIMITIVE_TYPES_GENERATORS.containsKey(fieldType)) {
                value = PRIMITIVE_TYPES_GENERATORS.get(fieldType).get();
            } else {
                value = createInstance(fieldType, depth + 1);
            }

            field.set(instance, value);
        }

        return instance;
    }

    private static Object generateCollection(Field field, int depth) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        var collection = new ArrayList<>();

        var genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType parameterizedType) {
            var typeArgs = parameterizedType.getActualTypeArguments();
            if (typeArgs.length == 1 && typeArgs[0] instanceof Class<?> elementClass) {
                int size = new Random().nextInt(3) + 1;
                for (int i = 0; i < size; i++) {
                    collection.add(createInstance(elementClass, depth + 1));
                }
            }
        }

        return collection;
    }

    private static Object generateMap(Field field, int depth)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        var map = new HashMap<>();

        var genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType parameterizedType) {
            var typeArgs = parameterizedType.getActualTypeArguments();
            if (typeArgs.length == 2 && typeArgs[0] instanceof Class<?> keyClass && typeArgs[1] instanceof Class<?> valueClass) {
                int size = new Random().nextInt(3) + 1;
                for (int i = 0; i < size; i++) {
                    Object key;
                    if (keyClass.isEnum() || IMMUTABLE_KEY_TYPES.contains(keyClass)) {
                        key = createInstance(keyClass, depth + 1);
                    } else {
                        throw new IllegalArgumentException("key for Map is not immutable");
                    }
                    Object value = createInstance(valueClass, depth + 1);
                    map.put(key, value);
                }
            }
        }

        return map;
    }

    private static Supplier<?> getRandomString() {
        return () -> {
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
        };
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
