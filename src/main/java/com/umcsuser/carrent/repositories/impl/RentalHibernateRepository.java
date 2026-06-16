package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class RentalHibernateRepository implements RentalRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Rental> findAll() {
        return entityManager.createQuery("FROM Rental", Rental.class).getResultList();
    }

    @Override
    public Optional<Rental> findById(String id) {
        return Optional.ofNullable(entityManager.find(Rental.class, id));
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return entityManager.createQuery(
                        "FROM Rental r WHERE r.vehicle.id = :vehicleId AND r.returnDateTime IS NULL", Rental.class)
                .setParameter("vehicleId", vehicleId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Rental save(Rental rental) {
        if (rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(UUID.randomUUID().toString());
        }
        return entityManager.merge(rental);
    }

    @Override
    public void deleteById(String id) {
        Rental rental = entityManager.find(Rental.class, id);
        if (rental != null) {
            entityManager.remove(rental);
        }
    }
}
