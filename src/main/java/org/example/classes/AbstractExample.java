package org.example.classes;

import org.example.annotation.Generatable;

@Generatable
public abstract class AbstractExample {
    protected int x;

    public AbstractExample(int x) {
        this.x = x;
    }
}
