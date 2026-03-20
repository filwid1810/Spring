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
    private String filePath = "vehicles.csv";

    public VehicleRepository() {
        load();

        if(vehicles.isEmpty()) {
            vehicles.add(new Car("1", "Awaryjny", "Test", 2020, 100.0, false));
        }
    }


    @Override
    public boolean rentVehicle(String id) {
        for(Vehicle v:vehicles){
            if(v.getId().equals(id)&&!v.isRented()){
                v.setRented(true);
                save();
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
                save();
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
    public void save() {
        try(PrintWriter writer = new PrintWriter(filePath)) {
            for(Vehicle v:vehicles){
                writer.println(v.toCSV(v.getId(),v.getBrand(),v.getModel(),v.getYear(),v.getPrice(),v.isRented()));
            }
        }catch (IOException e){
            System.out.println("Error saving file");
        }

    }

    @Override
    public void load() {
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
                    cur = new Motorcycle(split[1], split[2], split[3], Integer.parseInt(split[4]), Double.parseDouble(split[5]), Boolean.parseBoolean(split[6]), MotorcycleCategory.valueOf(split[7]));
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
        for(int i = 0; i < vehicles.size(); i++){
            if(vehicles.get(i).getId().equals(id)){
                vehicles.remove(i);
                save();
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean add(Vehicle vehicle) {
       if(vehicle != null){
           vehicles.add(vehicle);
           save();
           return true;
       }
       return false;
    }

    @Override
    public Vehicle getVehicle(String id) {
      for(Vehicle v:vehicles){
          if(v.getId().equals(id)){
            return v.copy();
          }
      }
      return null;
    }

}
