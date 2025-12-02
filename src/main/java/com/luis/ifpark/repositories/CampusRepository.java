package com.luis.ifpark.repositories;

import com.luis.ifpark.entities.Campus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CampusRepository extends JpaRepository<Campus, UUID> {
}
