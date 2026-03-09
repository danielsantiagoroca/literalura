package com.oracleone.literalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para un autor en la respuesta de la API Gutendex.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AutorGutendexDto(
        @JsonProperty("name") String nombre,
        @JsonProperty("birth_year") Integer anioNacimiento,
        @JsonProperty("death_year") Integer anioFallecimiento
) {}

