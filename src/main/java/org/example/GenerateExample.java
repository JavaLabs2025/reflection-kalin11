package org.example;


import org.example.classes.BinaryTreeNode;
import org.example.classes.Cart;
import org.example.classes.Example;
import org.example.classes.Product;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.example.generator.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class GenerateExample {
    private static final String PACKAGE_NAME = "org.example";
    private static final Logger log = LoggerFactory.getLogger(GenerateExample.class);

    public static void main(String[] args) {
        var gen = new Generator();
        try {
            generateWithAnnotatedAbstractClass(gen);
            generateRandomInterfaceImplementation(gen);
            generateRecursiveDataStructure(gen);
            generateSimpleClass(gen);
            generateWithCollections(gen);
            generateTriangle(gen);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void generateWithAnnotatedAbstractClass(Generator generator) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Object generated = generator.generateByType(Example.class, PACKAGE_NAME);
        log.info("Generated: {}", generated);
    }

    private static void generateRandomInterfaceImplementation(Generator generator) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Object generated = generator.generateByType(Shape.class, PACKAGE_NAME);
        log.info("Generated: {}", generated);
    }

    private static void generateRecursiveDataStructure(Generator generator) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Object generated = generator.generateByType(BinaryTreeNode.class, PACKAGE_NAME);
        log.info("Generated: {}", generated);
    }

    private static void generateSimpleClass(Generator generator) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Object generated = generator.generateByType(Product.class, PACKAGE_NAME);
        log.info("Generated: {}", generated);
    }

    private static void generateWithCollections(Generator generator) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Object generated = generator.generateByType(Cart.class, PACKAGE_NAME);
        log.info("Generated: {}", generated);
    }

    private static void generateTriangle(Generator generator) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Triangle generated = (Triangle) generator.generateByType(Triangle.class, PACKAGE_NAME);
        log.info("Generated: {}", generated);
        log.info("Triangle perimeter is = {}", generated.getPerimeter());
    }
}