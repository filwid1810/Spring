package com.umcsuser.carrent.models;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Vehicle {
    private String id;
    private String category;
    private String brand;
    private String model;
    private int year;
    private String plate;
    private double price;
    private Map<String, Object> attributes;

    public Vehicle(String id, String category, String brand, String model, int year, String plate, double price, Map<String, Object> attributes) {
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.price = price;
        this.attributes = (attributes != null) ? new HashMap<>(attributes) : new HashMap<>();
    }

    Map<String, Object> getAttributes() { return attributes; }
    public Object getAttribute(String key) { return attributes.get(key); }
    public void addAttribute(String key, Object value) { attributes.put(key, value); }
    public void removeAttribute(String key) { attributes.remove(key); }

    public Vehicle copy() {
        return new Vehicle(id, category, brand, model, year, plate, price, attributes);
    }

    @Override
    public String toString() {
        return String.format("Pojazd [ %s %s | Rok: %d | Rejestracja: %s | Cena: %.2f PLN]",
                 brand, model, year, (plate != null ? plate : "Brak"), price);
    }
}
