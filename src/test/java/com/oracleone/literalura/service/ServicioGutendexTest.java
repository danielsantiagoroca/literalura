package com.oracleone.literalura.service;

import com.oracleone.literalura.dto.AutorGutendexDto;
import com.oracleone.literalura.dto.LibroGutendexDto;
import com.oracleone.literalura.dto.RespuestaGutendexDto;
import com.oracleone.literalura.model.Autor;
import com.oracleone.literalura.model.Libro;
import com.oracleone.literalura.repository.AutorRepository;
import com.oracleone.literalura.repository.LibroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioGutendexTest {

    @Mock
    RestTemplate plantillaRest;

    @Mock
    LibroRepository repositorioLibro;

    @Mock
    AutorRepository repositorioAutor;

    private ServicioGutendex servicioGutendex() {
        return new ServicioGutendex(plantillaRest, "https://gutendex.com/books", repositorioLibro, repositorioAutor);
    }

    @Test
    void buscarPorTitulo_codificaTituloEnUtf8() {
        when(plantillaRest.getForObject(anyString(), eq(RespuestaGutendexDto.class)))
                .thenReturn(new RespuestaGutendexDto(0L, null, null, Collections.emptyList()));

        servicioGutendex().buscarPorTitulo("Cien años de soledad");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(plantillaRest).getForObject(urlCaptor.capture(), eq(RespuestaGutendexDto.class));
        String urlLlamada = urlCaptor.getValue();
        assertTrue(urlLlamada.contains("search="));
    }

    @Test
    void buscarLibroPorIdGutenberg_devuelveEmptySiRestTemplateLanzaExcepcion() {
        when(plantillaRest.getForObject(anyString(), eq(LibroGutendexDto.class)))
                .thenThrow(new RuntimeException("Error de red"));

        Optional<LibroGutendexDto> resultado = servicioGutendex().buscarLibroPorIdGutenberg(123L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarLibroPorIdGutenberg_devuelveEmptySiRespuestaEsNull() {
        when(plantillaRest.getForObject(anyString(), eq(LibroGutendexDto.class)))
                .thenReturn(null);

        Optional<LibroGutendexDto> resultado = servicioGutendex().buscarLibroPorIdGutenberg(123L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarLibroPorIdGutenberg_devuelveOptionalConDtoSiRespuestaValida() {
        LibroGutendexDto dto = new LibroGutendexDto(123L, "Titulo", Collections.emptyList(), List.of("es"), 10);
        when(plantillaRest.getForObject(anyString(), eq(LibroGutendexDto.class)))
                .thenReturn(dto);

        Optional<LibroGutendexDto> resultado = servicioGutendex().buscarLibroPorIdGutenberg(123L);

        assertTrue(resultado.isPresent());
        assertEquals(dto, resultado.get());
    }

    @Test
    void guardarDesdeDto_devuelveNullSiDtoEsNull() {
        Libro resultado = servicioGutendex().guardarDesdeDto(null);

        assertNull(resultado);
        verifyNoInteractions(repositorioLibro, repositorioAutor);
    }

    @Test
    void guardarDesdeDto_retornaLibroExistenteSiYaFueGuardado() {
        Libro existente = new Libro("Titulo", "es", 10L, 123L);
        LibroGutendexDto dto = new LibroGutendexDto(123L, "Otro titulo", Collections.emptyList(), List.of("en"), 5);
        when(repositorioLibro.findByIdGutenberg(123L)).thenReturn(Optional.of(existente));

        Libro resultado = servicioGutendex().guardarDesdeDto(dto);

        assertSame(existente, resultado);
        verify(repositorioLibro, never()).save(any());
        verifyNoInteractions(repositorioAutor);
    }

    @Test
    void guardarDesdeDto_guardaLibroSinAutorNiIdiomasCuandoNoHayDatos() {
        LibroGutendexDto dto = new LibroGutendexDto(123L, "Titulo", null, null, null);
        when(repositorioLibro.findByIdGutenberg(123L)).thenReturn(Optional.empty());
        when(repositorioLibro.save(any(Libro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Libro resultado = servicioGutendex().guardarDesdeDto(dto);

        assertNotNull(resultado);
        assertEquals("Titulo", resultado.getTitulo());
        assertNull(resultado.getIdiomas());
        assertNull(resultado.getCantidadDescargas());
        assertEquals(123L, resultado.getIdGutenberg());
        verifyNoInteractions(repositorioAutor);
    }

    @Test
    void guardarDesdeDto_reutilizaAutorExistenteSiYaEstaEnBase() {
        Autor autorExistente = new Autor("Autor", 1900, 1980);
        LibroGutendexDto dto = new LibroGutendexDto(123L, "Titulo", List.of(
                new AutorGutendexDto("Autor", 1900, 1980)
        ), List.of("es"), 10);
        when(repositorioLibro.findByIdGutenberg(123L)).thenReturn(Optional.empty());
        when(repositorioAutor.findByNombre("Autor")).thenReturn(Optional.of(autorExistente));
        when(repositorioLibro.save(any(Libro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Libro resultado = servicioGutendex().guardarDesdeDto(dto);

        assertNotNull(resultado.getAutor());
        assertSame(autorExistente, resultado.getAutor());
        verify(repositorioAutor, never()).save(any());
    }

    @Test
    void guardarDesdeDto_creaAutorNuevoCuandoNoExiste() {
        AutorGutendexDto autorDto = new AutorGutendexDto("Autor Nuevo", 1900, null);
        LibroGutendexDto dto = new LibroGutendexDto(123L, "Titulo", List.of(autorDto), List.of("es"), 10);

        when(repositorioLibro.findByIdGutenberg(123L)).thenReturn(Optional.empty());
        when(repositorioAutor.findByNombre("Autor Nuevo")).thenReturn(Optional.empty());

        Autor autorGuardado = new Autor("Autor Nuevo", 1900, null);
        when(repositorioAutor.save(any(Autor.class))).thenReturn(autorGuardado);
        when(repositorioLibro.save(any(Libro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Libro resultado = servicioGutendex().guardarDesdeDto(dto);

        assertNotNull(resultado.getAutor());
        assertEquals("Autor Nuevo", resultado.getAutor().getNombre());
        verify(repositorioAutor).save(any(Autor.class));
    }
}

