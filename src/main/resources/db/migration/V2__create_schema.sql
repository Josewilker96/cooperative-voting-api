-- Flyway migration V2: create schema for voting

CREATE TABLE pauta (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP NOT NULL
);

CREATE TABLE sessao_votacao (
    id BIGSERIAL PRIMARY KEY,
    pauta_id BIGINT NOT NULL REFERENCES pauta(id) ON DELETE CASCADE,
    data_abertura TIMESTAMP NOT NULL,
    data_fechamento TIMESTAMP
);

CREATE TABLE voto (
    id BIGSERIAL PRIMARY KEY,
    pauta_id BIGINT NOT NULL REFERENCES pauta(id) ON DELETE CASCADE,
    identificador_associado VARCHAR(255) NOT NULL,
    voto VARCHAR(10) NOT NULL,
    data_voto TIMESTAMP NOT NULL,
    CONSTRAINT uk_voto_pauta_associado UNIQUE (pauta_id, identificador_associado)
);

-- Índices que podem ajudar consultas
CREATE INDEX idx_sessao_pauta ON sessao_votacao(pauta_id);
CREATE INDEX idx_voto_pauta ON voto(pauta_id);
CREATE INDEX idx_voto_associado ON voto(identificador_associado);
