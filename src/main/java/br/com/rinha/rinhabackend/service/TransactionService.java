package br.com.rinha.rinhabackend.service;

import br.com.rinha.rinhabackend.error.RinhaErrors;
import br.com.rinha.rinhabackend.model.statement.StatementResponse;
import br.com.rinha.rinhabackend.model.transaction.TransactionRequest;
import br.com.rinha.rinhabackend.model.transaction.TransactionResponse;
import br.com.rinha.rinhabackend.repository.RegisterTransactionRepository;
import br.com.rinha.rinhabackend.repository.StatementTransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static java.util.Objects.isNull;

@ApplicationScoped
public class TransactionService {

  private final RegisterTransactionRepository registerRepository;
  private final StatementTransactionRepository statementRepository;

  @Inject
  TransactionService(RegisterTransactionRepository registerRepository,
                     StatementTransactionRepository statementRepository) {
    this.registerRepository = registerRepository;
    this.statementRepository = statementRepository;
  }

  public TransactionResponse register(int id, TransactionRequest transactionRequest) {
    transactionRequest.validate();

    if (transactionRequest.isCredit()) {
      return registerRepository.registerCredit(id, transactionRequest.getIntValor(), transactionRequest.descricao());
    }

    return registerRepository.registerDebit(id, transactionRequest.getIntValor(), transactionRequest.descricao());
  }

  public StatementResponse list(int id) {
    StatementResponse statement = statementRepository.findStatement(id);

    if (isNull(statement.saldo())) {
      throw RinhaErrors.NOT_FOUND_EXCEPTION;
    }

    return statement;
  }

}
