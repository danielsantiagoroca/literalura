package com.oracleone.literalura.repository;

import com.oracleone.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNombre(String nombre);

    @Query("""
            SELECT a
            FROM Autor a
            WHERE a.anioNacimiento IS NOT NULL
              AND a.anioNacimiento <= :year
              AND (a.anioFallecimiento IS NULL OR a.anioFallecimiento >= :year)
            """)
    List<Autor> findAutoresVivosEnAnio(int year);
}
