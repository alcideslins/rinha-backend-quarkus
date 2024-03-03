package br.com.rinha.rinhabackend.repository;

import br.com.rinha.rinhabackend.model.statement.ClientReportResponse;
import br.com.rinha.rinhabackend.model.statement.StatementResponse;
import br.com.rinha.rinhabackend.model.statement.StatementTransaction;
import io.agroal.api.AgroalDataSource;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Singleton
public class StatementTransactionRepository {

  private static final String QUERY_STATEMENT_TRANSACTION =
    "SELECT c.balance, c.lmt, t.value, t.type, t.created_at, t.description " +
      "FROM client c " +
      "         LEFT JOIN LATERAL ( " +
      "    SELECT t.value, t.type, t.created_at, t.description " +
      "    FROM transaction t " +
      "    WHERE t.client_id = c.id " +
      "    ORDER BY t.created_at DESC " +
      "    LIMIT 10 " +
      "    ) t ON TRUE " +
      "WHERE c.id = ?";

  private final AgroalDataSource dataSource;

  @Inject
  public StatementTransactionRepository(AgroalDataSource defaultDataSource) {
    this.dataSource = defaultDataSource;
  }

  @Transactional(Transactional.TxType.NEVER)
  public StatementResponse findStatement(int clientId) {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement statement = connection.prepareStatement(QUERY_STATEMENT_TRANSACTION);
    ) {
      statement.setInt(1, clientId);

      try (ResultSet resultSet = statement.executeQuery()) {
        return mapResultSetToStatementResponse(resultSet);
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private StatementResponse mapResultSetToStatementResponse(ResultSet rs) throws SQLException {
    StatementRowHandler statementRowHandler = new StatementRowHandler();
    while (rs.next()) {
      statementRowHandler.processRow(rs);
    }
    return statementRowHandler.getStatementResponse();
  }
}


class StatementRowHandler {
  private static final String RESP_BALANCE = "balance";
  private static final String RESP_LIMIT = "lmt";
  private static final String RESP_VALUE = "value";
  private static final String RESP_TYPE = "type";
  private static final String RESP_DESCRIPTION = "description";
  private static final String RESP_CREATED_AT = "created_at";

  private ClientReportResponse balance;
  private List<StatementTransaction> transactions;

  public void processRow(ResultSet rs) throws SQLException {

    if (isNull(balance)) {
      balance = new ClientReportResponse(rs.getInt(RESP_BALANCE), LocalDateTime.now(), rs.getInt(RESP_LIMIT));
    }

    String type = rs.getString(RESP_TYPE);
    if (type != null) {
      addTransaction(
        new StatementTransaction(
          rs.getInt(RESP_VALUE),
          type.charAt(0),
          rs.getString(RESP_DESCRIPTION),
          rs.getTimestamp(RESP_CREATED_AT).toLocalDateTime()
        )
      );
    }
  }

  public StatementResponse getStatementResponse() {
    return new StatementResponse(balance, transactions);
  }

  private void addTransaction(StatementTransaction transaction) {
    if (isNull(transactions)) {
      transactions = new ArrayList<>();
    }
    transactions.add(transaction);
  }
}


