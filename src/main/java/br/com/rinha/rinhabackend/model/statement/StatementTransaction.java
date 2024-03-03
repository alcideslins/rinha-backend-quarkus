package br.com.rinha.rinhabackend.model.statement;

import java.time.LocalDateTime;

public record StatementTransaction(
  int valor,
  char tipo,
  String descricao,
  LocalDateTime realizada_em) {
}
