package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VehicleRepository implements IVehicleRepository {
    List<Vehicle> vehicles = new ArrayList<>();
    private String filePath;

    public VehicleRepository() {
        this.filePath = "vehicles.txt";
        load(filePath);

        if(vehicles.isEmpty()) {
            vehicles.add(new Car("1", "Awaryjny", "Test", 2020, 100.0, false));
        }
    }


    @Override
    public boolean rentVehicle(String id) {
        for(Vehicle v:vehicles){
            if(v.getId().equals(id)&&!v.isRented()){
                v.setRented(true);
                save(filePath);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean returnVehicle(String id) {
        for(Vehicle v:vehicles){
            if(v.getId().equals(id)&&v.isRented()){
                v.setRented(false);
                save(filePath);
                return true;
            }
        }
        return false;
    }

    @Override
    public final List<Vehicle> getVehicles() {
       List<Vehicle> result = new ArrayList<>();
        for(Vehicle v:vehicles){
            result.add(v.copy());
        }
        return result;
    }

    @Override
    public Vehicle getVehicle(String id) {
        return null;
    }

    @Override
    public boolean add(Vehicle vehicle) {
        return false;
    }

    @Override
    public void save(String filePath) {
        try(PrintWriter writer = new PrintWriter(filePath)) {
            for(Vehicle v:vehicles){
                writer.println(v.toCSV(v.getId(),v.getBrand(),v.getModel(),v.getYear(),v.getPrice(),v.isRented()));
            }
        }catch (IOException e){
            System.out.println("Error saving file");
        }

    }

    @Override
    public void load(String filePath) {
        try{
            File file = new File(filePath);
            Scanner scannerf = new Scanner(file);

            vehicles.clear();

            while (scannerf.hasNextLine()) {
                String line = scannerf.nextLine();


                String[] split = line.split(";");
                String type = split[0];
                Vehicle cur;

                if (type.equals("MOTORCYCLE")) {
                    cur = new Motorcycle(split[1], split[2], split[3], Integer.parseInt(split[4]), Double.parseDouble(split[5]), Boolean.parseBoolean(split[6]), split[7]);
                    vehicles.add(cur);
                } else if (type.equals("CAR")) {
                    cur = new Car(split[1], split[2], split[3], Integer.parseInt(split[4]), Double.parseDouble(split[5]), Boolean.parseBoolean(split[6]));
                    vehicles.add(cur);
                }
            }
            scannerf.close();

        }catch(FileNotFoundException e){
            System.out.println("File not found");
        }
    }


    @Override
    public boolean remove(String id) {
        return false;
    }

}
