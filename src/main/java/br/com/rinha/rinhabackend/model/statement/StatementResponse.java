package br.com.rinha.rinhabackend.model.statement;

import java.util.List;

public record StatementResponse(
  ClientReportResponse saldo,
  List<StatementTransaction> ultimas_transacoes
) {
}
