package com.example.demo.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @Column(name = "codigo_barras", length = 50)
    private String codigoBarras;

    @ManyToOne(fetch = FetchType.EAGER)  
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoRopa tipo;

   @ManyToOne(fetch = FetchType.EAGER)  
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(length = 10)
    private String talla;

    @Column(length = 30) // Nuevo campo para el color
    private String color;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    
    @Column(nullable = false)
    private boolean vendido = false; // Solo este campo adicional

    // Getters y Setters
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public TipoRopa getTipo() { return tipo; }
    public void setTipo(TipoRopa tipo) { this.tipo = tipo; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    // Getters y Setters (mantén los que ya tienes y añade)
    public boolean isVendido() {
        return vendido;
    }

    public void setVendido(boolean vendido) {
        this.vendido = vendido;
    }

     public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}