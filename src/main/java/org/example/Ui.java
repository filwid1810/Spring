package org.example;

import java.util.List;
import java.util.Scanner;

public class Ui {
    private final IVehicleRepository vehicleRepository;
    private final Scanner scanner;
    private final IUserRepository userRepository;
    private final Authentication authentication;

    public Ui(IVehicleRepository vehicleRepository, IUserRepository userRepository, Authentication authentication) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.authentication = authentication;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        User loggedInUser = null;
        while (loggedInUser == null) {
            System.out.println("Enter login: ");
            String login = scanner.nextLine();
            System.out.println("Enter password: ");
            String password = scanner.nextLine();
            loggedInUser = authentication.authenticate(login, password);
            if (loggedInUser == null) {
                System.out.println("Invalid login or password");
            }
        }
        System.out.println("Welcome " + loggedInUser.getLogin());

        boolean running = true;
        while (running) {
            if(loggedInUser.getRole() == Role.ADMIN){
                System.out.println("Admin panel");
                running = adminMenu();
            }else {
                running = userMenu(loggedInUser);
            }
        }
    }
    private boolean userMenu(User user) {
        System.out.println("1. Display owned vehicles");
        System.out.println("2. Rent vehicle");
        System.out.println("3. Return vehicle");
        System.out.println("4. See all vehicles");
        System.out.println("0. Exit");
        System.out.println("Enter your choice: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                displayUserInfo(user);
                break;
            case "2":
                rentVehicle(user);
                break;
            case "3":
                returnVehicle(user);
                break;
            case "4":
                displayVehicles();
                break;
            case "0":
                System.out.println("Wylogowano.");
                return false;
            default:
                System.out.println("Nieznana komenda.");
        }
        return true;
    }

    private boolean adminMenu() {
        System.out.println("\n1: Przeglądaj listę pojazdów");
        System.out.println("2: Dodaj nowy pojazd");
        System.out.println("3: Usuń pojazd");
        System.out.println("4: Wyświetl listę użytkowników i ich pojazdy");
        System.out.println("5: Usuń usera");
        System.out.println("0: Wyjście (Wyloguj)");
        System.out.print("Wybierz komendę: ");
        String command = scanner.nextLine();

        switch (command) {
            case "1":
                displayVehicles();
                break;
            case "2":
                addVehicle();
                break;
            case "3":
                removeVehicle();
                break;
            case "4":
                displayAllUsers();
                break;
                case "5":
                    removeUser();
            case "0":
                System.out.println("Wylogowano.");
                return false;
            default:
                System.out.println("Nieznana komenda.");
        }
        return true;
    }

    private void displayVehicles() {
        List<Vehicle> vehicles = vehicleRepository.getVehicles();
        if (vehicles.isEmpty()) {
            System.out.println("Brak pojazdów w bazie.");
            return;
        }
       for(Vehicle v:vehicles){
           System.out.println(v.toString());
       }
    }
    private void displayUserInfo(User user) {
        System.out.println("Zalogowany jako: " + user.getLogin());
        if (user.getRentedVehicles()!= null && !user.getRentedVehicles().isEmpty()) {
            Vehicle v = vehicleRepository.getVehicle(user.getRentedVehicles());
            if (v != null) {
                System.out.println("Wypożyczony pojazd: " + v);
            } else {
                System.out.println(" Pojazd o ID " + user.getRentedVehicles() + " nie istnieje w bazie!");
            }
        } else {
            System.out.println(" nie masz wypożyczonego pojazdu.");
        }
    }

    private void rentVehicle(User user) {
        if (user.getRentedVehicles() != null && !user.getRentedVehicles().isEmpty()) {
            System.out.println(" Zwróć  najpierw wypożyczony pojazd");
            return;
        }
        System.out.print("Podaj ID pojazdu do wypożyczenia: ");
        String id = scanner.nextLine();
        if (vehicleRepository.rentVehicle(id)) {
            user.setRentedVehicles(id);
            userRepository.update(user);
            System.out.println("Pojazd został pomyślnie wypożyczony.");
        } else {
            System.out.println("Pojazd nie został znaleziony lub jest już wypożyczony.");
        }
    }
    private void returnVehicle(User user) {
        if (user.getRentedVehicles() == null || user.getRentedVehicles().isEmpty()) {
            System.out.println("Nie masz przypisanego żadnego pojazdu do zwrotu.");
            return;
        }
        String id = user.getRentedVehicles();
        if (vehicleRepository.returnVehicle(id)) {
            user.setRentedVehicles(null);
            userRepository.update(user);
            System.out.println("Pojazd zwrócony pomyślnie.");
        } else {
            System.out.println("Wystąpił błąd przy zwrocie pojazdu.");
        }
    }
    private void addVehicle() {
        System.out.println("Podaj dane nowego pojazdu:");
        System.out.print("ID: "); String id = scanner.nextLine();
        System.out.print("Marka: "); String brand = scanner.nextLine();
        System.out.print("Model: "); String model = scanner.nextLine();
        System.out.print("Rok: "); int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Cena: "); double price = Double.parseDouble(scanner.nextLine());

        Vehicle newVehicle = new Car(id, brand, model, year, price, false);
        if (vehicleRepository.add(newVehicle)) {
            System.out.println("Pojazd dodany.");
        }
    }
    private void removeVehicle() {
        System.out.print("Podaj ID pojazdu do usunięcia: ");
        String id = scanner.nextLine();
        if (vehicleRepository.remove(id)) {
            System.out.println("Pojazd usunięty z bazy.");
        } else {
            System.out.println("Nie znaleziono pojazdu o takim ID.");
        }
    }
    private void displayAllUsers() {
        List<User> users = userRepository.getUsers();
        for (User u : users) {
            System.out.print("Login: " + u.getLogin() + " | Rola: " + u.getRole());
            if (u.getRentedVehicles() != null && !u.getRentedVehicles().isEmpty()) {
                Vehicle v = vehicleRepository.getVehicle(u.getRentedVehicles());
                System.out.println(" | Wypożyczył: " + (v != null ? v.getBrand() + " " + v.getModel() : "Nieznany pojazd"));
            } else {
                System.out.println(" | Brak wypożyczonego pojazdu");
            }
        }
    }
    private void registerUser() {
        System.out.println("\n--- REJESTRACJA ---");
        System.out.print("Podaj nowy login: ");
        String login = scanner.nextLine();

        if (userRepository.getUser(login) != null) {
            System.out.println("Użytkownik o takim loginie już istnieje");
            return;
        }

        System.out.print("Podaj hasło: ");
        String plainPassword = scanner.nextLine();

        String hashedPassword = Authentication.hashPassword(plainPassword);

        User newUser = new User(login, hashedPassword, Role.USER, null);

        if (userRepository.register(newUser)) {
            System.out.println("Rejestracja zakończona sukcesem");
        } else {
            System.out.println("Błąd rejestracji");
        }
    }
    private void removeUser() {
        System.out.print("Podaj login użytkownika do usunięcia: ");
        String login = scanner.nextLine();

        User u = userRepository.getUser(login);
        if (u == null) {
            System.out.println("Nie znaleziono użytkownika o takim loginie");
            return;
        }

        if (u.getRentedVehicles() != null && !u.getRentedVehicles().isEmpty()) {
            System.out.println(" Nie można usunąć użytkownika trzeba zwrócić pojazd");
            return;
        }

        if (userRepository.delete(login)) {
            System.out.println("Użytkownik '" + login + "' został pomyślnie usunięty");
        } else {
            System.out.println("Błąd podczas usuwania użytkownika");
        }
    }
}
