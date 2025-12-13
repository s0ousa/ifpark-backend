package com.luis.ifpark.repositories;

import com.luis.ifpark.entities.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {
    boolean existsByCpf(String cpf);
    Optional<Pessoa> findByCpf(String cpf);
}
