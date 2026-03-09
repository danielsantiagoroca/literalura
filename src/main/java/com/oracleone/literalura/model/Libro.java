package com.oracleone.literalura.model;

import jakarta.persistence.*;

/**
 * Entidad Libro (tabla {@code libro}).
 * Un libro tiene un solo autor (según especificación del desafío).
 */
@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String titulo;

    @Column(length = 200)
    private String idiomas;

    @Column(name = "download_count")
    private Long cantidadDescargas;

    @Column(name = "gutenberg_id", nullable = false, unique = true)
    private Long idGutenberg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    public Libro() {
    }

    public Libro(String titulo, String idiomas, Long cantidadDescargas, Long idGutenberg) {
        this.titulo = titulo;
        this.idiomas = idiomas;
        this.cantidadDescargas = cantidadDescargas;
        this.idGutenberg = idGutenberg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(String idiomas) {
        this.idiomas = idiomas;
    }

    public Long getCantidadDescargas() {
        return cantidadDescargas;
    }

    public void setCantidadDescargas(Long cantidadDescargas) {
        this.cantidadDescargas = cantidadDescargas;
    }

    public Long getIdGutenberg() {
        return idGutenberg;
    }

    public void setIdGutenberg(Long idGutenberg) {
        this.idGutenberg = idGutenberg;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }
}
