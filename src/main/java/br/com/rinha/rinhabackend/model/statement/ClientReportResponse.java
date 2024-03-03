package br.com.rinha.rinhabackend.model.statement;

import java.time.LocalDateTime;

public record ClientReportResponse(
  int total,
  LocalDateTime data_extrato,
  int limite
) {
}
