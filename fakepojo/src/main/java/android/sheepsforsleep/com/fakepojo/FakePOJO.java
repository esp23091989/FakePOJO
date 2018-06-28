package android.sheepsforsleep.com.fakepojo;

import android.util.Pair;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("unchecked")
public class FakePOJO {

    private static Random rnd;
    private static InvocationHandler invocationHandler;
    private static Map<Pair<Integer, String>, Object> values = new HashMap<>();
    static {
        rnd = new Random();
    }

    public static  <T> T create(Class<T> tClass) throws IllegalAccessException, InstantiationException {
        if(isPrimitive(tClass))
            return createPrimitive(tClass);

        T instance = null;

//        try {
//            Constructor conatructor = tClass.getConstructor();
//            instance = (T) conatructor.newInstance();
//        } catch (NoSuchMethodException e) {
//            Constructor[] constructors = tClass.getConstructors();
//            Class<?>[] params = constructors[0].getParameterTypes();
//            List<Object> parameterInstances = new ArrayList<>();
//            for (Class<?> param : params){
//                parameterInstances.add(create(param));
//            }
////            constructors[0].newInstance(parameterInstances.get(0));
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
        instance = tClass.newInstance();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields){
            if(field.isSynthetic())
                continue;

            if(!field.isAccessible())
                field.setAccessible(true);

            Class<?> fieldType = field.getType();
            if(isPrimitive(fieldType)){
                field.set(instance, createPrimitive(fieldType));
            }
            else if(List.class.isAssignableFrom(fieldType)){
                Class<? extends  List> fieldTypeList = (Class<? extends List>) fieldType;
                Class<?> parameterizedType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                field.set(instance, createFakeList(fieldTypeList, parameterizedType));
            }
            else if(Set.class.isAssignableFrom(fieldType)){
                Class<? extends  Set> fieldSetType = (Class<? extends Set>) fieldType;
                Class<?> parameterizedType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                field.set(instance, createFakeSet(fieldSetType, parameterizedType));
            }
            else if(Map.class.isAssignableFrom(fieldType)){
                Class<? extends Map> fieldMapType = (Class<? extends Map>) fieldType;
                Class<?> parameterizedKeyType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                Class<?> parameterizedValueType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];
                field.set(instance, createFakeMap(fieldMapType, parameterizedKeyType, parameterizedValueType));
            }
            else if(fieldType.isArray()){
                field.set(instance, createFakeArray(fieldType.getComponentType()));
            }
            else if(fieldType.isInterface()){
                field.set(instance, createInterfaceProxy(fieldType));
            }
            else {
                field.set(instance, create(fieldType));
            }
        }
        return instance;
    }

    private static  <F> F createInterfaceProxy(Class<F> fClass){
        InvocationHandler ivocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Integer proxyId = System.identityHashCode(proxy);
                String methodId = method.getName();

                Object value = values.get(new Pair<>(proxyId, methodId));
                if(value != null)
                    return value;

                Class<?> methodReturnType = method.getReturnType();
                if(isPrimitive(methodReturnType)){
                    value = createPrimitive(methodReturnType);
                    values.put(new Pair<>(proxyId, methodId), value);
                }

                if(methodReturnType.isInterface()){
                    value = createInterfaceProxy(methodReturnType);
                }

                return value;
            }
        };

        Class<?>[] ifaces = new Class[]{fClass};
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return (F) Proxy.newProxyInstance(cl,ifaces, ivocationHandler);
    }

    private static <T> Object createFakeArray(Class<T> componentType) throws InstantiationException, IllegalAccessException {
        Object array = Array.newInstance(componentType, 20);
        for (int i = 0; i < 9; i++){
            T item = create(componentType);
            Array.set(array, i, item);
        }
        return array;
    }

    private static  <T extends List<E>, E> T createFakeList(Class<T> type, Class<E> parameterizedType) throws IllegalAccessException, InstantiationException {
        T collection = null;
        try {
            collection = type.newInstance();
        } catch (InstantiationException e) {
            collection = (T) new ArrayList<>();
        } catch (IllegalAccessException e) {
            throw e;
        }

        for (int i = 0; i< 9; i++){
            collection.add(FakePOJO.create(parameterizedType));
        }
        return collection;
    }

    private static <T extends Set<E>, E> T createFakeSet(Class<T> type, Class<E> parameterizedType) throws IllegalAccessException, InstantiationException {
        T set = null;
        try {
            set = type.newInstance();
        } catch (IllegalAccessException e) {
            throw e;
        } catch (InstantiationException e) {
            set = (T) new HashSet<>();
        }

        for(int i = 0; i < 9; i++){
            set.add(FakePOJO.create(parameterizedType));
        }
        return set;
    }

    private static <T extends Map<K,V>, K, V> T createFakeMap(Class<T> type, Class<K> parametrizedKeyType, Class<V> parametrizedValueType) throws InstantiationException, IllegalAccessException {
        T map = null;
        try {
            map = type.newInstance();
        } catch (InstantiationException e) {
            map = (T) new HashMap<>();
        } catch (IllegalAccessException e) {
            throw e;
        }

        for (int i = 0; i < 9; i++){
            K key = FakePOJO.create(parametrizedKeyType);
            V value = FakePOJO.create(parametrizedValueType);
            map.put(key, value);
        }
        return map;
    }

    private static <T> T createPrimitive(Class<T> type) {
        Object value = null;
        if(String.class.isAssignableFrom(type)){
            byte[] bytes = new byte[10];
            rnd.nextBytes(bytes);
            value = new String(bytes, Charset.forName("UTF-8"));
        }
        else if(Integer.class.isAssignableFrom(type) || type == Integer.TYPE) {
            value = rnd.nextInt();
        }
        else if(Double.class.isAssignableFrom(type) ||type == Double.TYPE){
            value = rnd.nextDouble();
        }
        else if(Float.class.isAssignableFrom(type) || type == Float.TYPE){
            value = rnd.nextFloat();
        }
        else if(Long.class.isAssignableFrom(type)|| type == Long.TYPE){
            value = rnd.nextLong();
        }
        else if(Character.class.isAssignableFrom(type)){
            value = type.getName().charAt(0);
        }
        else if(Boolean.class.isAssignableFrom(type) || type == Boolean.TYPE)
            value = rnd.nextBoolean();

        return (T) value;
    }

    private static boolean isPrimitive(Class<?> type){
        if(type.isPrimitive() ||Integer.class.isAssignableFrom(type)
                || Double.class.isAssignableFrom(type)
                || Float.class.isAssignableFrom(type)
                || Long.class.isAssignableFrom(type)
                || Character.class.isAssignableFrom(type)
                || Boolean.class.isAssignableFrom(type)
                || String.class.isAssignableFrom(type)
                ) {
            return true;
        }

        return false;
    }

}
