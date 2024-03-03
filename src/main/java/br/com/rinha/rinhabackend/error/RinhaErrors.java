package br.com.rinha.rinhabackend.error;


public class RinhaErrors {

  private RinhaErrors() {
  }

  public static final NotFoundException NOT_FOUND_EXCEPTION = new NotFoundException();
  public static final UnprocessableEntityException UNPROCESSABLE_ENTITY_EXCEPTION = new UnprocessableEntityException();
}
