-- Tabla de libros (API Gutendex: id, title, languages, download_count).
-- Un libro tiene un solo autor (autor_id nullable por si la API no trae autores).
CREATE TABLE libro (
    id             BIGSERIAL PRIMARY KEY,
    titulo         VARCHAR(500) NOT NULL,
    idiomas        VARCHAR(200),
    download_count BIGINT,
    gutenberg_id   BIGINT NOT NULL UNIQUE,
    autor_id       BIGINT REFERENCES autor (id)
);
