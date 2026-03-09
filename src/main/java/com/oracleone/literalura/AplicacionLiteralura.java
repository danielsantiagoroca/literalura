package com.oracleone.literalura;

import com.oracleone.literalura.service.LiteraluraMenuService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AplicacionLiteralura implements CommandLineRunner {

    private final LiteraluraMenuService servicioMenu;
    private final ConfigurableApplicationContext contexto;

    public AplicacionLiteralura(LiteraluraMenuService servicioMenu,
                                ConfigurableApplicationContext contexto) {
        this.servicioMenu = servicioMenu;
        this.contexto = contexto;
    }

    public static void main(String[] args) {
        SpringApplication.run(AplicacionLiteralura.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("¡Bienvenido a LiterAlura! Catálogo de libros (API Gutendex + PostgreSQL).");
        int opcion;
        do {
            servicioMenu.mostrarMenu();
            opcion = servicioMenu.leerOpcion();
            switch (opcion) {
                case 1 -> servicioMenu.buscarYGuardarLibro();
                case 2 -> servicioMenu.listarLibrosRegistrados();
                case 3 -> servicioMenu.listarAutoresRegistrados();
                case 4 -> servicioMenu.listarAutoresVivosEnAnio();
                case 5 -> servicioMenu.listarLibrosPorIdioma();
                case 0 -> {
                    System.out.println("Hasta pronto.");
                    SpringApplication.exit(contexto, () -> 0);
                }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }
}

