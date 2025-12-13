package com.luis.ifpark.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_movimentacao")
@Getter
@Setter
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime dataEntrada;

    private LocalDateTime dataSaida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacionamento_id", nullable = false)
    private Estacionamento estacionamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vigia_entrada_id", nullable = false)
    private Usuario vigiaEntrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vigia_saida_id")
    private Usuario vigiaSaida;

    public Movimentacao() {
    }

    public Movimentacao(UUID id, LocalDateTime dataEntrada, LocalDateTime dataSaida, Veiculo veiculo, Estacionamento estacionamento, Usuario vigiaEntrada, Usuario vigiaSaida) {
        this.id = id;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.veiculo = veiculo;
        this.estacionamento = estacionamento;
        this.vigiaEntrada = vigiaEntrada;
        this.vigiaSaida = vigiaSaida;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movimentacao that = (Movimentacao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
