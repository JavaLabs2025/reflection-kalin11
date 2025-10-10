package org.example.classes;

import org.example.annotation.Generatable;

//@Generatable
public class Example extends AbstractExample {
    int i;

    public Example(int i, int x) {
        super(x);
        this.i = i;
    }

    @Override
    public String toString() {
        return "Example{" +
                "i=" + i +
                ", x=" + x +
                '}';
    }
}
