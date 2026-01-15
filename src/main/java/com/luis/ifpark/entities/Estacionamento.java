package com.luis.ifpark.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_estacionamento")
@Getter
@Setter
public class Estacionamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer capacidadeTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean ativo = true;

    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Movimentacao> movimentacoes = new ArrayList<>();

    public Estacionamento() {
    }

    public Estacionamento(UUID id, String nome, Integer capacidadeTotal, Campus campus) {
        this.id = id;
        this.nome = nome;
        this.capacidadeTotal = capacidadeTotal;
        this.campus = campus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estacionamento that = (Estacionamento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
