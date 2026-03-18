package org.example;

import java.util.List;
import java.util.Scanner;

public class Ui {
    private final IVehicleRepository vehicleRepository;
    private final Scanner scanner;

    public Ui(IVehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("Available commands:\n 1:displayVehicles,\n 2:rentVehicle,\n 3:returnVehicle,\n 4:exit\n");
            System.out.println("Enter command: ");
            String command = scanner.nextLine();

            switch (command) {
                case "1":
                    displayVehicles();
                    break;
                case "2":
                    rentVehicle();
                    break;
                case "3":
                    returnVehicle();
                    break;
                case "4":
                    System.out.println("exit");
                    running = false;
                    break;

            }

        }
    }
    private void displayVehicles() {
        List<Vehicle> vehicles = vehicleRepository.getVehicles();
       for(Vehicle v:vehicles){
           System.out.println(v.toString());
       }
    }
    private void rentVehicle() {
        System.out.println("Enter vehicle id: ");
        String id = scanner.nextLine();
        if(vehicleRepository.rentVehicle(id)){
            System.out.println("Vehicle rented");
        }else{
            System.out.println("Vehicle not found");
        }
    }
    private void returnVehicle() {
        System.out.println("Enter vehicle id: ");
        String id = scanner.nextLine();
        if(vehicleRepository.returnVehicle(id)){
            System.out.println("Vehicle returned");
        }else{
            System.out.println("Vehicle not found");
        }
    }
}
