package io.github.kaurami.wems.repository;

import io.github.kaurami.wems.model.Beverage;
import io.github.kaurami.wems.model.Guest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long>, JpaSpecificationExecutor<Guest> {

    List<Guest> findByBeveragesContaining(Beverage beverage);

    @Override
    @EntityGraph(attributePaths = {"family"})
    List<Guest> findAll();

    @Override
    @EntityGraph(attributePaths = {"family"})
    List<Guest> findAll(Sort sort);

    @Override
    @EntityGraph(attributePaths = {"family"})
    List<Guest> findAll(Specification<Guest> spec);

    @Query("SELECT count(g) FROM Guest g WHERE g.willAttend = true")
    long countConfirmedGuests();

    @Query("SELECT count(DISTINCT g.family) FROM Guest g WHERE g.willAttend = true AND g.family.transferRequired = true")
    long countFamiliesRequiringTransfer();

    @Query("SELECT count(DISTINCT g.family) FROM Guest g WHERE g.willAttend = true AND g.family.placementRequired = true")
    long countFamiliesRequiringPlacement();

    @Query("SELECT g.beverages, count(g) FROM Guest g WHERE g.willAttend = true GROUP BY g.beverages")
    List<Object[]> countBeveragePreferences();

    @Query("SELECT g FROM Guest g JOIN FETCH g.family WHERE g.willAttend = true AND g.family.active = true")
    List<Guest> findAllConfirmedWithFamily();
}