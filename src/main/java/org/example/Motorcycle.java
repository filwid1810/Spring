package org.example;

public class Motorcycle extends Vehicle {
    private String category;


    public Motorcycle( String id,String brand, String model, int year, Double price, boolean rented, String category) {
        super(id,brand, model, year, price, rented );
        this.category = category;
    }

    @Override
    public Vehicle copy() {
        return new Motorcycle(this.getId(),this.getBrand(),this.getModel(),this.getYear(),this.getPrice(),this.isRented(),this.getCategory());
    }

    @Override
    String toCSV(String id, String brand, String model, int year, double price, boolean rented) {
        return "MOTORCYCLE;" + super.toCSV(id, brand, model, year, price, rented)+ ";"+category;
    }

    @Override
    public String toString() {
        return "{MOTORCYCLE " + super.toString()+ ";"+category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
