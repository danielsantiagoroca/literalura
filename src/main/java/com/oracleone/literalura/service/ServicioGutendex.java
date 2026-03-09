package com.oracleone.literalura.service;

import com.oracleone.literalura.dto.LibroGutendexDto;
import com.oracleone.literalura.dto.RespuestaGutendexDto;
import com.oracleone.literalura.model.Autor;
import com.oracleone.literalura.model.Libro;
import com.oracleone.literalura.repository.AutorRepository;
import com.oracleone.literalura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Cliente de la API Gutendex y conversión a entidades persistentes.
 */
@Service
public class ServicioGutendex {

    private final RestTemplate plantillaRest;
    private final String urlBaseApi;
    private final LibroRepository repositorioLibro;
    private final AutorRepository repositorioAutor;

    public ServicioGutendex(
            RestTemplate plantillaRest,
            @Value("${app.api.gutendex.url}") String urlBaseApi,
            LibroRepository repositorioLibro,
            AutorRepository repositorioAutor) {
        this.plantillaRest = plantillaRest;
        this.urlBaseApi = urlBaseApi.endsWith("/") ? urlBaseApi : urlBaseApi + "/";
        this.repositorioLibro = repositorioLibro;
        this.repositorioAutor = repositorioAutor;
    }

    /**
     * Obtiene una página de resultados de la API (lista de libros).
     */
    public RespuestaGutendexDto buscarPagina(int pagina) {
        String url = urlBaseApi + "?page=" + pagina;
        return plantillaRest.getForObject(url, RespuestaGutendexDto.class);
    }

    /**
     * Busca libros por título en la API (parámetro search).
     */
    public RespuestaGutendexDto buscarPorTitulo(String titulo) {
        String tituloCodificado = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
        String url = urlBaseApi + "?search=" + tituloCodificado;
        return plantillaRest.getForObject(url, RespuestaGutendexDto.class);
    }

    /**
     * Busca un libro por ID de Gutenberg (endpoint /books/{id}/).
     */
    public Optional<LibroGutendexDto> buscarLibroPorIdGutenberg(Long idGutenberg) {
        String url = urlBaseApi + idGutenberg + "/";
        try {
            LibroGutendexDto dto = plantillaRest.getForObject(url, LibroGutendexDto.class);
            return Optional.ofNullable(dto);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Convierte un DTO de la API a entidades Libro y Autor, las persiste y devuelve el Libro.
     */
    public Libro guardarDesdeDto(LibroGutendexDto dto) {
        if (dto == null) return null;

        Optional<Libro> existente = repositorioLibro.findByIdGutenberg(dto.id());
        if (existente.isPresent()) {
            return existente.get();
        }

        String idiomasStr = dto.idiomas() != null && !dto.idiomas().isEmpty()
                ? String.join(", ", dto.idiomas())
                : null;
        Long cantidadDescargas = dto.cantidadDescargas() != null ? dto.cantidadDescargas().longValue() : null;

        Libro libro = new Libro(
                dto.titulo(),
                idiomasStr,
                cantidadDescargas,
                dto.id()
        );

        // Un libro, un autor (especificación del desafío): tomamos el primero de la API
        if (dto.autores() != null && !dto.autores().isEmpty()) {
            var autorDto = dto.autores().get(0);
            Autor autor = repositorioAutor.findByNombre(autorDto.nombre())
                    .orElseGet(() -> {
                        Autor nuevoAutor = new Autor(autorDto.nombre(), autorDto.anioNacimiento(), autorDto.anioFallecimiento());
                        return repositorioAutor.save(nuevoAutor);
                    });
            libro.setAutor(autor);
        }
        return repositorioLibro.save(libro);
    }
}

