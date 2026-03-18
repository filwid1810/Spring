package org.example;



public class Main {
    public static void main(String[] args) {

        IVehicleRepository repo = new VehicleRepository();
        repo.load("vehicles.csv");
        Ui ui = new Ui(repo);
        ui.run();

    }
}