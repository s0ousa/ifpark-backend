package com.luis.ifpark.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_campus")
@Getter
@Setter
public class Campus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", nullable = false)
    private Endereco endereco;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean ativo = true;

    @OneToMany(mappedBy = "campus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Estacionamento> estacionamentos = new ArrayList<>();

    public Campus() {
    }

    public Campus(UUID id, String nome, Endereco endereco) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campus campus = (Campus) o;
        return Objects.equals(id, campus.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
