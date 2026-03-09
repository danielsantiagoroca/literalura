-- Tabla de autores (API Gutendex: name, birth_year, death_year)
CREATE TABLE autor (
    id      BIGSERIAL PRIMARY KEY,
    nombre  VARCHAR(500) NOT NULL,
    birth_year INTEGER,
    death_year INTEGER
);
