package com.oracleone.literalura.repository;

import com.oracleone.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    @Query("SELECT l FROM Libro l LEFT JOIN FETCH l.autor")
    List<Libro> findAllWithAutor();

    Optional<Libro> findByIdGutenberg(Long idGutenberg);

    @Query("SELECT l FROM Libro l WHERE LOWER(l.idiomas) LIKE LOWER(CONCAT(CONCAT('%', :codigoIdioma), '%'))")
    List<Libro> findLibrosPorIdioma(String codigoIdioma);
}
