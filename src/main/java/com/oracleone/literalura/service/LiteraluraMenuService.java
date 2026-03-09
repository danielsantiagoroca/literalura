package com.oracleone.literalura.service;

import com.oracleone.literalura.dto.LibroGutendexDto;
import com.oracleone.literalura.dto.RespuestaGutendexDto;
import com.oracleone.literalura.model.Autor;
import com.oracleone.literalura.model.Libro;
import com.oracleone.literalura.repository.AutorRepository;
import com.oracleone.literalura.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;

/**
 * Menú de consola e interacción con el usuario (consultas, listados, guardar libro).
 */
@Service
public class LiteraluraMenuService {

    private final ServicioGutendex servicioGutendex;
    private final LibroRepository repositorioLibro;
    private final AutorRepository repositorioAutor;
    private final Scanner lector = new Scanner(System.in);

    public LiteraluraMenuService(ServicioGutendex servicioGutendex,
                                 LibroRepository repositorioLibro,
                                 AutorRepository repositorioAutor) {
        this.servicioGutendex = servicioGutendex;
        this.repositorioLibro = repositorioLibro;
        this.repositorioAutor = repositorioAutor;
    }

    public void mostrarMenu() {
        System.out.println("\n--- LiterAlura ---");
        System.out.println("1) Buscar libro por título (API) y guardar");
        System.out.println("2) Listar libros registrados");
        System.out.println("3) Listar autores registrados");
        System.out.println("4) Listar autores vivos en un año determinado");
        System.out.println("5) Listar libros por idioma");
        System.out.println("0) Salir");
            System.out.print("Elija una opción: ");
    }

    public int leerOpcion() {
        try {
            return Integer.parseInt(lector.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void buscarYGuardarLibro() {
        System.out.print("Escriba el título (o parte) del libro: ");
        String titulo = lector.nextLine().trim();
        if (titulo.isBlank()) {
            System.out.println("Título vacío.");
            return;
        }
        RespuestaGutendexDto respuesta = servicioGutendex.buscarPorTitulo(titulo);
        if (respuesta == null || respuesta.resultados() == null || respuesta.resultados().isEmpty()) {
            System.out.println("No se encontraron resultados en la API.");
            return;
        }
        if (respuesta.resultados().size() == 1) {
            Libro guardado = servicioGutendex.guardarDesdeDto(respuesta.resultados().get(0));
            System.out.println("Libro guardado: " + guardado.getTitulo() + " (ID Gutenberg: " + guardado.getIdGutenberg() + ")");
            return;
        }
        System.out.println("Varios resultados. Elija el número del libro a guardar:");
        for (int i = 0; i < respuesta.resultados().size(); i++) {
            LibroGutendexDto libroDto = respuesta.resultados().get(i);
            String autores = libroDto.autores() != null && !libroDto.autores().isEmpty()
                    ? libroDto.autores().stream().map(a -> a.nombre()).reduce((x, y) -> x + ", " + y).orElse("—")
                    : "—";
            System.out.println((i + 1) + ") " + libroDto.titulo() + " | " + autores);
        }
        try {
            int num = Integer.parseInt(lector.nextLine().trim());
            if (num < 1 || num > respuesta.resultados().size()) {
                System.out.println("Número inválido.");
                return;
            }
            Libro guardado = servicioGutendex.guardarDesdeDto(respuesta.resultados().get(num - 1));
            System.out.println("Libro guardado: " + guardado.getTitulo());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }
    }

    public void listarLibrosRegistrados() {
        List<Libro> libros = repositorioLibro.findAllWithAutor();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        System.out.println("\n--- Libros registrados ---");
        libros.forEach(l -> {
            String autorStr = l.getAutor() != null ? l.getAutor().getNombre() : "—";
            System.out.println("• " + l.getTitulo() + " | Idiomas: " + (l.getIdiomas() != null ? l.getIdiomas() : "—") + " | Autor: " + autorStr);
        });
    }

    public void listarAutoresRegistrados() {
        List<Autor> autores = repositorioAutor.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }
        System.out.println("\n--- Autores registrados ---");
        autores.forEach(a -> System.out.println("• " + a.getNombre() + " (" + (a.getAnioNacimiento() != null ? a.getAnioNacimiento() : "?") + " - " + (a.getAnioFallecimiento() != null ? a.getAnioFallecimiento() : "?") + ")"));
    }

    public void listarAutoresVivosEnAnio() {
        System.out.print("Año: ");
        String linea = lector.nextLine().trim();
        int anio;
        try {
            anio = Integer.parseInt(linea);
        } catch (NumberFormatException e) {
            System.out.println("Año inválido.");
            return;
        }
        List<Autor> autores = repositorioAutor.findAutoresVivosEnAnio(anio);
        if (autores.isEmpty()) {
            System.out.println("No hay autores vivos en " + anio + " registrados.");
            return;
        }
        System.out.println("\n--- Autores vivos en " + anio + " ---");
        autores.forEach(a -> System.out.println("• " + a.getNombre() + " (" + (a.getAnioNacimiento() != null ? a.getAnioNacimiento() : "?") + " - " + (a.getAnioFallecimiento() != null ? a.getAnioFallecimiento() : "?") + ")"));
    }

    public void listarLibrosPorIdioma() {
        System.out.print("Código de idioma (ej: en, es, pt): ");
        String codigo = lector.nextLine().trim();
        if (codigo.isBlank()) {
            System.out.println("Código vacío.");
            return;
        }
        List<Libro> libros = repositorioLibro.findLibrosPorIdioma(codigo);
        if (libros.isEmpty()) {
            System.out.println("No hay libros en ese idioma.");
            return;
        }
        System.out.println("\n--- Libros en " + codigo + " ---");
        System.out.println("Cantidad: " + libros.size() + " libro(s)");
        libros.forEach(l -> System.out.println("• " + l.getTitulo()));
    }
}
