package org.example;

public class Car extends Vehicle {

    public Car(String id,String brand, String model, int year, double price, boolean rented ) {
        super( id,brand, model, year, price, rented );
    }


    @Override
    public Vehicle copy() {
        return new Car(getId(),getBrand(),getModel(),getYear(),getPrice(),isRented());
    }

    @Override
    String toCSV(String id, String brand, String model, int year, double price, boolean rented) {
        return "CAR;" + super.toCSV(id, brand, model, year, price, rented);
    }

    @Override
    public String toString() {
        return "{CAR " + super.toString();
    }
}
