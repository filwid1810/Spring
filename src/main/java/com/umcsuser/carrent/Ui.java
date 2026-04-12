package com.umcsuser.carrent;

import com.umcsuser.carrent.models.*;
import com.umcsuser.carrent.repositories.*;
import com.umcsuser.carrent.services.AuthService;

import java.time.LocalDateTime;
import java.util.*;

public class Ui {
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;
    private final RentalRepository rentalRepo;
    private final AuthService authService;
    private final Scanner scanner = new Scanner(System.in);

    public Ui(VehicleRepository vehicleRepo, UserRepository userRepo, RentalRepository rentalRepo, AuthService authService) {
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
        this.authService = authService;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- SYSTEM WYPOŻYCZALNI ---");
            System.out.println("1. Zaloguj się");
            System.out.println("2. Zarejestruj się");
            System.out.println("0. Wyjdź");
            System.out.print("Wybór: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> login();
                case "2" -> register();
                case "0" -> running = false;
                default -> System.out.println("Nieprawidłowa opcja.");
            }
        }
    }

    private void login() {
        System.out.print("Login: "); String login = scanner.nextLine();
        System.out.print("Hasło: "); String password = scanner.nextLine();

        authService.login(login, password).ifPresentOrElse(user -> {
            System.out.println("Zalogowano pomyślnie. Witaj " + user.getLogin());
            if (user.getRole() == Role.ADMIN) adminMenu(user);
            else userMenu(user);
        }, () -> System.out.println("Błędny login lub hasło."));
    }

    private void register() {
        System.out.print("Nowy login: "); String login = scanner.nextLine();
        System.out.print("Nowe hasło: "); String password = scanner.nextLine();

        if (authService.register(login, password)) {
            System.out.println("Rejestracja udana. Możesz się zalogować.");
        } else {
            System.out.println("Login jest już zajęty.");
        }
    }

    private void userMenu(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- MENU UŻYTKOWNIKA ---");
            System.out.println("1. Wyświetl dostępne pojazdy");
            System.out.println("2. Wypożycz pojazd");
            System.out.println("3. Zwróć pojazd");
            System.out.println("0. Wyloguj");
            System.out.print("Wybór: ");

            switch (scanner.nextLine()) {
                case "1" -> displayAvailableVehicles();
                case "2" -> rentVehicle(user);
                case "3" -> returnVehicle(user);
                case "0" -> loggedIn = false;
            }
        }
    }

    private void adminMenu(User admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- PANEL ADMINISTRATORA ---");
            System.out.println("1. Lista wszystkich pojazdów");
            System.out.println("2. Dodaj pojazd");
            System.out.println("3. Usuń pojazd");
            System.out.println("4. Lista wszystkich użytkowników");
            System.out.println("0. Wyloguj");
            System.out.print("Wybór: ");

            switch (scanner.nextLine()) {
                case "1" -> displayAllVehicles();
                case "2" -> addVehicle();
                case "3" -> removeVehicle();
                case "4" -> displayAllUsers();
                case "0" -> loggedIn = false;
            }
        }
    }

    // --- LOGIKA POJAZDÓW ---

    private void displayAvailableVehicles() {
        System.out.println("Dostępne pojazdy:");
        vehicleRepo.findAll().stream()
                .filter(v -> rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                .forEach(System.out::println);
    }

    private void displayAllVehicles() {
        vehicleRepo.findAll().forEach(v -> {
            String status = rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isPresent() ? "[WYPOŻYCZONY]" : "[WOLNY]";
            System.out.println(status + " " + v);
        });
    }

    private void rentVehicle(User user) {
        // Sprawdź czy user już czegoś nie wypożyczył
        boolean alreadyHasRental = rentalRepo.findAll().stream()
                .anyMatch(r -> r.getUserId().equals(user.getId()) && r.isActive());

        if (alreadyHasRental) {
            System.out.println("Masz już wypożyczony pojazd!");
            return;
        }

        System.out.print("Podaj ID pojazdu: ");
        String vehicleId = scanner.nextLine();

        Optional<Vehicle> vehicle = vehicleRepo.findById(vehicleId);
        if (vehicle.isPresent() && rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isEmpty()) {
            Rental rental = Rental.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .vehicleId(vehicleId)
                    .rentDateTime(LocalDateTime.now().toString())
                    .build();
            rentalRepo.save(rental);
            System.out.println("Pomyślnie wypożyczono pojazd.");
        } else {
            System.out.println("Pojazd niedostępny lub nie istnieje.");
        }
    }

    private void returnVehicle(User user) {
        rentalRepo.findAll().stream()
                .filter(r -> r.getUserId().equals(user.getId()) && r.isActive())
                .findFirst()
                .ifPresentOrElse(rental -> {
                    rental.setReturnDateTime(LocalDateTime.now().toString());
                    rentalRepo.save(rental);
                    System.out.println("Pojazd zwrócony.");
                }, () -> System.out.println("Nie masz nic do zwrócenia."));
    }

    private void addVehicle() {
        System.out.print("ID: "); String id = scanner.nextLine();
        System.out.print("Marka: "); String brand = scanner.nextLine();
        System.out.print("Model: "); String model = scanner.nextLine();
        System.out.print("Rok: "); int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Cena: "); double price = Double.parseDouble(scanner.nextLine());

        Vehicle vehicle = new Vehicle(id, null, brand, model, year, null, price, new HashMap<>());
        vehicleRepo.save(vehicle);
        System.out.println("Pojazd dodany.");
    }

    private void removeVehicle() {
        System.out.print("Podaj ID pojazdu do usunięcia: ");
        String id = scanner.nextLine();
        vehicleRepo.deleteById(id);
        System.out.println("Pojazd usunięty.");
    }

    private void displayAllUsers() {
        userRepo.findAll().forEach(System.out::println);
    }
}