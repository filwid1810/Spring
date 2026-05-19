package com.umcsuser.carrent;

import com.umcsuser.carrent.models.*;
import com.umcsuser.carrent.repositories.*;
import com.umcsuser.carrent.services.*;

import java.time.LocalDateTime;
import java.util.*;

public class UI {
    private final VehicleServiceInterface vehicleService;
    private final VehicleCategoryConfigService configService;
    private final UserServiceInterface userService;
    private final RentalServiceInterface rentalService;
    private final AuthServiceInterface authService;
    private final Scanner scanner = new Scanner(System.in);

    public UI(VehicleServiceInterface vehicleService,
              VehicleCategoryConfigService configService,
              UserServiceInterface userService,
              RentalServiceInterface rentalService,
              AuthServiceInterface authService) {
        this.vehicleService = vehicleService;
        this.configService = configService;
        this.userService = userService;
        this.rentalService = rentalService;
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
            System.out.println("5. Usuń użytkownika");
            System.out.println("0. Wyloguj");
            System.out.print("Wybór: ");

            switch (scanner.nextLine()) {
                case "1" -> displayAllVehicles();
                case "2" -> addVehicle();
                case "3" -> removeVehicle();
                case "4" -> displayAllUsers();
                case "5" -> removeUser();
                case "0" -> loggedIn = false;
            }
        }
    }

    private void displayAvailableVehicles() {
        System.out.println("Dostępne pojazdy:");
        vehicleService.findAllVehicles().stream()
                .filter(v -> !rentalService.vehicleHasActiveRental(v.getId()))
                .forEach(System.out::println);
    }

    private void displayAllVehicles() {
        vehicleService.findAllVehicles().forEach(v -> {
            String status = rentalService.vehicleHasActiveRental(v.getId()) ? "[WYPOŻYCZONY]" : "[WOLNY]";
            System.out.println(status + " " + v);
        });
    }

    private void rentVehicle(User user) {
        if (rentalService.userHasActiveRental(user.getId())) {
            System.out.println("Masz już wypożyczony pojazd!");
            return;
        }

        System.out.print("Podaj ID pojazdu: ");
        String vehicleId = scanner.nextLine();

        Vehicle vehicle = vehicleService.findById(vehicleId);

        if (vehicle != null && !rentalService.vehicleHasActiveRental(vehicleId)) {
            rentalService.rentVehicle(user.getId(), vehicleId);
            System.out.println("Pomyślnie wypożyczono pojazd.");
        } else {
            System.out.println("Pojazd niedostępny lub nie istnieje.");
        }
    }

    private void returnVehicle(User user) {
        try {
            rentalService.returnVehicle(user.getId());
            System.out.println("Pojazd zwrócony.");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addVehicle() {
        System.out.println("\n--- DODAWANIE POJAZDU ---");
        System.out.print("Podaj kategorię (Car, Motorcycle, Bus): ");
        String category = scanner.nextLine();

        if (!configService.categoryExists(category)) {
            System.out.println("Błąd: Nieznana kategoria pojazdu!");
            return;
        }

        System.out.print("Marka: "); String brand = scanner.nextLine();
        System.out.print("Model: "); String model = scanner.nextLine();

        int year;
        String plate;
        double price;
        try {
            System.out.print("Rok: ");
            year = Integer.parseInt(scanner.nextLine());

            System.out.print("Rejestracja: ");
            plate = scanner.nextLine();

            System.out.print("Cena: ");
            price = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Błąd: Rok i Cena muszą być poprawnymi liczbami! Przerwano dodawanie pojazdu.");
            return;
        }

      Vehicle vehicle = Vehicle.builder()
                .id(UUID.randomUUID().toString())
                .category(category)
                .brand(brand)
                .model(model)
                .year(year)
                .plate(plate)
                .price(price)
                .build();

        VehicleCategoryConfig config = configService.getByCategory(category);
        for (Map.Entry<String, String> entry : config.getAttributes().entrySet()) {
            String attrName = entry.getKey();
            String expectedType = entry.getValue();

            System.out.print("Podaj '" + attrName + "' (typ: " + expectedType + "): ");
            String input = scanner.nextLine();

            try {
                if (expectedType.equalsIgnoreCase("integer")) {
                    vehicle.addAttribute(attrName, Integer.parseInt(input));
                } else if (expectedType.equalsIgnoreCase("number")) {
                    vehicle.addAttribute(attrName, Double.parseDouble(input));
                } else if (expectedType.equalsIgnoreCase("boolean")) {
                    vehicle.addAttribute(attrName, Boolean.parseBoolean(input));
                } else {
                    vehicle.addAttribute(attrName, input);
                }
            } catch (NumberFormatException e) {
                System.out.println("Błąd: Podałeś zły format danych dla atrybutu " + attrName);
                return;
            }
        }

        try {
            vehicleService.addVehicle(vehicle);
            System.out.println("Pojazd dodany pomyślnie!");
        } catch (IllegalArgumentException e) {
            System.out.println("BŁĄD WALIDACJI: " + e.getMessage());
        }
    }

    private void removeVehicle() {
        System.out.print("Podaj ID pojazdu do usunięcia: ");
        String id = scanner.nextLine();

        try {
            vehicleService.removeVehicle(id);
            System.out.println("Pojazd usunięty.");
        } catch (IllegalStateException e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }
    private void removeUser() {
        System.out.print("Podaj ID (lub Login) użytkownika do usunięcia: ");
        String loginOrId = scanner.nextLine();

        try {
            userService.deleteUser(loginOrId, null);
            System.out.println("Użytkownik usunięty.");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void displayAllUsers() {
        userService.findAllUsers().forEach(u -> {
            System.out.print(u.toString());

            rentalService.findActiveRentalByUserId(u.getId()).ifPresentOrElse(
                    rental -> System.out.println(" -> Wypożyczył pojazd (ID: " + rental.getVehicleId() + ")"),
                    () -> System.out.println(" -> Brak aktywnych wypożyczeń")
            );
        });
    }
}