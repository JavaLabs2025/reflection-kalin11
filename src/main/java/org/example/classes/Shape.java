package org.example.classes;

import org.example.annotation.Generatable;

@Generatable
public interface Shape {
    double getArea();
    double getPerimeter();
}