package com.oracleone.literalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO para la respuesta paginada de la API Gutendex (/books).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RespuestaGutendexDto(
        @JsonProperty("count") Long total,
        @JsonProperty("next") String siguiente,
        @JsonProperty("previous") String anterior,
        @JsonProperty("results") List<LibroGutendexDto> resultados
) {}

