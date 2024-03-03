package br.com.rinha.rinhabackend.repository;

import br.com.rinha.rinhabackend.error.RinhaErrors;
import br.com.rinha.rinhabackend.model.transaction.TransactionResponse;
import io.agroal.api.AgroalDataSource;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.sql.*;
import java.util.Map;

@Singleton
public class RegisterTransactionRepository {

  private static final String QUERY_REGISTER_CREDIT =
    "SELECT * FROM register_credit(?, ?, ?)";

  private static final String QUERY_REGISTER_DEBIT =
    "SELECT * FROM register_debit(?, ?, ?)";

  private static final String QUERY_RESET_DB =
    "SELECT * FROM reset_database()";

  private static final String RESP_LIMIT = "r_lmt";
  private static final String RESP_BALANCE = "r_balance";
  private static final String RESP_STATUS = "r_status";

  private static final char STATUS_SUCCESS = 'S';
  private static final char STATUS_NOT_FOUND = 'N';
  private static final char STATUS_NO_LIMIT = 'L';


  private final AgroalDataSource dataSource;

  @Inject
  RegisterTransactionRepository(AgroalDataSource defaultDataSource) {
    this.dataSource = defaultDataSource;
  }

  private void setParameters(PreparedStatement statement, Map<Integer, Object> params) throws SQLException {
    for (Map.Entry<Integer, Object> entry : params.entrySet()) {
      statement.setObject(entry.getKey(), entry.getValue());
    }
  }

  @Transactional
  public TransactionResponse registerCredit(int clientId, int value, String description) {
    return getTransactionResponse(clientId, value, description, QUERY_REGISTER_CREDIT);
  }

  @Transactional
  public TransactionResponse registerDebit(int clientId, int value, String description) {
    return getTransactionResponse(clientId, value, description, QUERY_REGISTER_DEBIT);
  }

  private TransactionResponse getTransactionResponse(int clientId, int value, String description, String queryRegisterDebit) {
    try (Connection connection = dataSource.getConnection()) {
      Map<Integer, Object> params = Map.of(
        1, clientId,
        2, value,
        3, description
      );
      try (PreparedStatement statement = connection.prepareStatement(queryRegisterDebit)) {
        setParameters(statement, params);
        try (ResultSet resultSet = statement.executeQuery()) {
          if (resultSet.next()) {
            return mapTransactionResponse(resultSet);
          } else {
            throw RinhaErrors.NOT_FOUND_EXCEPTION;
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  private TransactionResponse mapTransactionResponse(ResultSet rs) throws SQLException {
    checkStatus(rs.getString(RESP_STATUS).charAt(0));
    return new TransactionResponse(
      rs.getInt(RESP_LIMIT),
      rs.getInt(RESP_BALANCE)
    );
  }

  private void checkStatus(char status) {
    if (status == STATUS_SUCCESS) return;
    if (status == STATUS_NOT_FOUND) throw RinhaErrors.NOT_FOUND_EXCEPTION;
    if (status == STATUS_NO_LIMIT) throw RinhaErrors.UNPROCESSABLE_ENTITY_EXCEPTION;
  }

  public void resetDb() {
    try (Connection connection = dataSource.getConnection();
         CallableStatement callableStatement = connection.prepareCall(QUERY_RESET_DB)) {
      callableStatement.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}


