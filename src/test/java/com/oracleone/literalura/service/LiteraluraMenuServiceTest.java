package com.oracleone.literalura.service;

import com.oracleone.literalura.dto.LibroGutendexDto;
import com.oracleone.literalura.dto.RespuestaGutendexDto;
import com.oracleone.literalura.model.Libro;
import com.oracleone.literalura.repository.AutorRepository;
import com.oracleone.literalura.repository.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LiteraluraMenuServiceTest {

    @Mock
    ServicioGutendex servicioGutendex;

    @Mock
    LibroRepository libroRepository;

    @Mock
    AutorRepository autorRepository;

    @InjectMocks
    LiteraluraMenuService menuService;

    private void setInput(String input) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        java.util.Scanner scanner = new java.util.Scanner(in);
        Field lectorField = LiteraluraMenuService.class.getDeclaredField("lector");
        lectorField.setAccessible(true);
        lectorField.set(menuService, scanner);
    }

    @BeforeEach
    void resetMocks() {
        reset(servicioGutendex, libroRepository, autorRepository);
    }

    @Test
    void leerOpcion_devuelveNumeroValido() throws Exception {
        setInput("3\n");

        int opcion = menuService.leerOpcion();

        assertEquals(3, opcion);
    }

    @Test
    void leerOpcion_devuelveMenosUnoSiNoEsNumero() throws Exception {
        setInput("abc\n");

        int opcion = menuService.leerOpcion();

        assertEquals(-1, opcion);
    }

    @Test
    void buscarYGuardarLibro_noInvocaApiSiTituloVacio() throws Exception {
        setInput("   \n");

        menuService.buscarYGuardarLibro();

        verifyNoInteractions(servicioGutendex);
    }

    @Test
    void buscarYGuardarLibro_noHaceNadaSiApiNoDevuelveResultados() throws Exception {
        setInput("titulo\n");
        when(servicioGutendex.buscarPorTitulo("titulo"))
                .thenReturn(new RespuestaGutendexDto(0L, null, null, Collections.emptyList()));

        menuService.buscarYGuardarLibro();

        verify(servicioGutendex, never()).guardarDesdeDto(any());
    }

    @Test
    void buscarYGuardarLibro_guardaDirectoCuandoSoloHayUnResultado() throws Exception {
        setInput("titulo\n");
        LibroGutendexDto libroDto = new LibroGutendexDto(1L, "Titulo", Collections.emptyList(), List.of("es"), 10);
        RespuestaGutendexDto respuesta = new RespuestaGutendexDto(1L, null, null, List.of(libroDto));
        when(servicioGutendex.buscarPorTitulo("titulo")).thenReturn(respuesta);

        Libro libroGuardado = new Libro("Titulo", "es", 10L, 1L);
        when(servicioGutendex.guardarDesdeDto(libroDto)).thenReturn(libroGuardado);

        menuService.buscarYGuardarLibro();

        verify(servicioGutendex).guardarDesdeDto(libroDto);
    }

    @Test
    void buscarYGuardarLibro_numeroFueraDeRangoNoGuarda() throws Exception {
        setInput("titulo\n0\n");
        LibroGutendexDto libro1 = new LibroGutendexDto(1L, "Titulo1", Collections.emptyList(), List.of("es"), 10);
        LibroGutendexDto libro2 = new LibroGutendexDto(2L, "Titulo2", Collections.emptyList(), List.of("es"), 5);
        RespuestaGutendexDto respuesta = new RespuestaGutendexDto(2L, null, null, List.of(libro1, libro2));
        when(servicioGutendex.buscarPorTitulo("titulo")).thenReturn(respuesta);

        menuService.buscarYGuardarLibro();

        verify(servicioGutendex, never()).guardarDesdeDto(any());
    }

    @Test
    void buscarYGuardarLibro_entradaNoNumericaEnSeleccionNoGuarda() throws Exception {
        setInput("titulo\nabc\n");
        LibroGutendexDto libro1 = new LibroGutendexDto(1L, "Titulo1", Collections.emptyList(), List.of("es"), 10);
        LibroGutendexDto libro2 = new LibroGutendexDto(2L, "Titulo2", Collections.emptyList(), List.of("es"), 5);
        RespuestaGutendexDto respuesta = new RespuestaGutendexDto(2L, null, null, List.of(libro1, libro2));
        when(servicioGutendex.buscarPorTitulo("titulo")).thenReturn(respuesta);

        menuService.buscarYGuardarLibro();

        verify(servicioGutendex, never()).guardarDesdeDto(any());
    }

    @Test
    void buscarYGuardarLibro_seleccionValidaGuardaLibroEsperado() throws Exception {
        setInput("titulo\n2\n");
        LibroGutendexDto libro1 = new LibroGutendexDto(1L, "Titulo1", Collections.emptyList(), List.of("es"), 10);
        LibroGutendexDto libro2 = new LibroGutendexDto(2L, "Titulo2", Collections.emptyList(), List.of("es"), 5);
        RespuestaGutendexDto respuesta = new RespuestaGutendexDto(2L, null, null, List.of(libro1, libro2));
        when(servicioGutendex.buscarPorTitulo("titulo")).thenReturn(respuesta);

        Libro libroGuardado = new Libro("Titulo2", "es", 5L, 2L);
        when(servicioGutendex.guardarDesdeDto(libro2)).thenReturn(libroGuardado);

        menuService.buscarYGuardarLibro();

        verify(servicioGutendex).guardarDesdeDto(libro2);
    }

    @Test
    void listarAutoresVivosEnAnio_noInvocaRepositorioSiAnioInvalido() throws Exception {
        setInput("abc\n");

        menuService.listarAutoresVivosEnAnio();

        verifyNoInteractions(autorRepository);
    }

    @Test
    void listarAutoresVivosEnAnio_invocaRepositorioConAnioValido() throws Exception {
        setInput("1980\n");

        when(autorRepository.findAutoresVivosEnAnio(1980)).thenReturn(Collections.emptyList());

        menuService.listarAutoresVivosEnAnio();

        verify(autorRepository).findAutoresVivosEnAnio(1980);
    }

    @Test
    void listarLibrosPorIdioma_noInvocaRepositorioSiCodigoVacio() throws Exception {
        setInput("\n");

        menuService.listarLibrosPorIdioma();

        verifyNoInteractions(libroRepository);
    }

    @Test
    void listarLibrosPorIdioma_invocaRepositorioConCodigoValido() throws Exception {
        setInput("es\n");
        when(libroRepository.findLibrosPorIdioma("es")).thenReturn(Collections.emptyList());

        menuService.listarLibrosPorIdioma();

        verify(libroRepository).findLibrosPorIdioma("es");
    }
}

