package br.com.rinha.rinhabackend.model.transaction;

import br.com.rinha.rinhabackend.error.RinhaErrors;

import static java.util.Objects.nonNull;

public record TransactionRequest(String tipo, Number valor, String descricao) {

  public static final String CREDIT = "c";
  public static final String DEBIT = "d";
  public static final int MAX_DESCRIPTION = 10;
  public static final int MIN_VALUE = 0;

  public int getIntValor() {
    return valor.intValue();
  }

  public void validate() {
    if (!isValidType() || !isValidValue() || !isValidDescription()) {
      throw RinhaErrors.UNPROCESSABLE_ENTITY_EXCEPTION;
    }
  }

  private boolean isValidValue() {
    return valor instanceof Integer && valor.intValue() > MIN_VALUE;
  }

  private boolean isValidType() {
    return CREDIT.equals(tipo) || DEBIT.equals(tipo);
  }

  private boolean isValidDescription() {
    return nonNull(descricao) && descricao.length() <= MAX_DESCRIPTION && !descricao.isBlank();
  }

  public boolean isCredit() {
    return CREDIT.equals(tipo);
  }
}