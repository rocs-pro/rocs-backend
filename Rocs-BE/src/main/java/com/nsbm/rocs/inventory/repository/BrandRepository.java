package com.nsbm.rocs.inventory.repository;

import com.nsbm.rocs.inventory.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    List<Brand> findByIsActiveTrue();

    Optional<Brand> findByNameAndIsActiveTrue(String name);

    boolean existsByName(String name);
}

