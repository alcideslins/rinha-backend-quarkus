package br.com.rinha.rinhabackend.error;

public class NoStackTrace extends RuntimeException {

  NoStackTrace() {
    super(null, null, false, false);
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
