package org.example.classes;

import org.example.annotation.Generatable;

import java.util.List;
import java.util.Map;

@Generatable
public class Cart {
    private List<Product> items;
    private Map<String, Product> products;

    public Cart(List<Product> items,Map<String, Product> products) {
        this.items = items;
        this.products = products;
    }

    public List<Product> getItems() {
        return items;
    }

    public void setItems(List<Product> items) {
        this.items = items;
    }

// Конструктор, методы добавления и удаления товаров, геттеры и другие методы

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                ", products=" + products +
                '}';
    }
}