package org.example.generator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ReflectionUtil {

    private static final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);

    public static boolean isClassOrParentAnnotated(
            final Class<?> clazz,
            final Class<? extends Annotation> annotation,
            final String packageName
    ) {
        var clazzCopy = clazz;
        var packageNameForClasses = new StringBuilder().append(packageName).append(".classes");

        var isInterfaceAnnotated = false;

        if (clazzCopy.isInterface()) {
            isInterfaceAnnotated = clazzCopy.isAnnotationPresent(annotation);
            var implementations = findAllInterfaceImplementations(clazzCopy, packageNameForClasses.toString());
            clazzCopy = implementations.get(new Random().nextInt(implementations.size()));
            log.debug("Was given an interface = {}, chosen implementation = {}", clazz.getCanonicalName(), clazzCopy.getCanonicalName());
        }

        while (!clazzCopy.equals(Object.class)) {
            var implementedInterfaces = Arrays.asList(clazzCopy.getInterfaces());
            if (clazzCopy.isAnnotationPresent(annotation) ||
                    isInterfaceAnnotated ||
                    implementedInterfaces.stream().anyMatch(i -> i.isAnnotationPresent(annotation))
            ) {
                return true;
            }
            clazzCopy = clazzCopy.getSuperclass();
        }
        return false;
    }

    public static List<Class<?>> findAllInterfaceImplementations(Class<?> interfaceClass, String packageName) {
        var path = packageName.replace('.', '/');
        var classLoader = Thread.currentThread().getContextClassLoader();
        var resource = classLoader.getResource(path);
        if (resource == null) {
            return Collections.emptyList();
        }

        File dir;
        try {
            dir = new File(resource.toURI());
        } catch (URISyntaxException e) {
            log.error("Invalid URI syntax: {}", e.getMessage());
            return Collections.emptyList();
        }

        var implementations = new ArrayList<Class<?>>();

        for (var file : Objects.requireNonNull(dir.listFiles())) {
            if (file.getName().endsWith(".class")) {
                var className = packageName + "." + file.getName().replace(".class", "");
                try {
                    var clazz = Class.forName(className);
                    if (interfaceClass.isAssignableFrom(clazz) && !clazz.isInterface()) {
                        implementations.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    log.error("Class not found: {}", e.getMessage());
                }
            }
        }

        return implementations;
    }
}
