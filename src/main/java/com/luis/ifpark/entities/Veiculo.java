package com.luis.ifpark.entities;

import com.luis.ifpark.entities.enums.StatusAprovacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_veiculo")
@Getter
@Setter
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String placa;

    @Column(nullable = false)
    private String modelo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAprovacao statusAprovacao;

    private String motivoRejeicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Movimentacao> movimentacoes = new ArrayList<>();

    public Veiculo() {
    }

    public Veiculo(UUID id, String placa, String modelo, StatusAprovacao statusAprovacao, String motivoRejeicao, Pessoa pessoa) {
        this.id = id;
        this.placa = placa;
        this.modelo = modelo;
        this.statusAprovacao = statusAprovacao;
        this.motivoRejeicao = motivoRejeicao;
        this.pessoa = pessoa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Veiculo veiculo = (Veiculo) o;
        return Objects.equals(id, veiculo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
