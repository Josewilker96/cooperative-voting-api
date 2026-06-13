CREATE INDEX IF NOT EXISTS idx_sessao_pauta_fechamento
ON sessao_votacao(pauta_id, data_fechamento);