package org.example.classes;

public class Example extends AbstractExample {
    int i;

    public Example(int i, int x) {
        super(x);
        this.i = i;
    }

    public Example(int x) {
        super(x);
    }

    @Override
    public String toString() {
        return "Example{" +
                "i=" + i +
                ", x=" + x +
                '}';
    }
}
