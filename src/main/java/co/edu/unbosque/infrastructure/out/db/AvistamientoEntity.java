package co.edu.unbosque.infrastructure.out.db;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "avistamientos")
public class AvistamientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String especie;

    @Column(nullable = false)
    private String fecha;

    @Column(nullable = false)
    private String lugar;

    @Column(nullable = false)
    private String observador;

    public AvistamientoEntity() {
    }

    public AvistamientoEntity(Long id, String especie, String fecha, String lugar, String observador) {
        this.id = id;
        this.especie = especie;
        this.fecha = fecha;
        this.lugar = lugar;
        this.observador = observador;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public String getObservador() { return observador; }
    public void setObservador(String observador) { this.observador = observador; }
}