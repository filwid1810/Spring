package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.db.HibernateConfig;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.impl.RentalHibernateRepository;
import com.umcsuser.carrent.repositories.impl.VehicleHibernateRepository;
import com.umcsuser.carrent.services.VehicleServiceInterface;
import com.umcsuser.carrent.services.VehicleValidator;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class VehicleHibernateService implements VehicleServiceInterface {
    private final VehicleHibernateRepository vehicleRepo;
    private final RentalHibernateRepository rentalRepo;
    private final VehicleValidator validator;

    public VehicleHibernateService(VehicleHibernateRepository vehicleRepo, RentalHibernateRepository rentalRepo, VehicleValidator validator) {
        this.vehicleRepo = vehicleRepo;
        this.rentalRepo = rentalRepo;
        this.validator = validator;
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepo.setSession(session);
            return vehicleRepo.findAll();
        }
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepo.setSession(session);
            rentalRepo.setSession(session);
            return vehicleRepo.findAll().stream()
                    .filter(v -> rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                    .toList();
        }
    }

    @Override
    public Vehicle findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepo.setSession(session);
            return vehicleRepo.findById(id).orElse(null);
        }
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        validator.validate(vehicle);
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            vehicleRepo.setSession(session);
            Vehicle saved = vehicleRepo.save(vehicle);
            tx.commit();
            return saved;
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public void removeVehicle(String vehicleId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            vehicleRepo.setSession(session);
            rentalRepo.setSession(session);

            if (rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent()) {
                throw new IllegalStateException("Nie można usunąć pojazdu, bo jest wypożyczony.");
            }

            vehicleRepo.deleteById(vehicleId);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            rentalRepo.setSession(session);
            return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        }
    }
}