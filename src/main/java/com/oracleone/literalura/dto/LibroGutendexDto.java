package com.oracleone.literalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO para un libro en la respuesta de la API Gutendex.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LibroGutendexDto(
        Long id,
        @JsonProperty("title") String titulo,
        @JsonProperty("authors") List<AutorGutendexDto> autores,
        @JsonProperty("languages") List<String> idiomas,
        @JsonProperty("download_count") Integer cantidadDescargas
) {}

