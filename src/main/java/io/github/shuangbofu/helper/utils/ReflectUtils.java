package io.github.shuangbofu.helper.utils;

import sun.misc.Unsafe;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReflectUtils {
    public static List<Field> getFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    public static <A extends Annotation> List<A> getAnnotations(Class<?> clazz, Class<A> annotationClass) {
        List<A> annotations = new ArrayList<>();
        while (clazz != null) {
            annotations.addAll(Arrays.asList(clazz.getAnnotationsByType(annotationClass)));
            clazz = clazz.getSuperclass();
        }
        Collections.reverse(annotations);
        return annotations;
    }

    public static Type[] getGenericTypes(Class<?> clazz) {
        return ((ParameterizedType) clazz
                .getGenericInterfaces()[0]).getActualTypeArguments();
    }

    public static void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);

            Class cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception e) {
            // ignore
        }
    }
}
