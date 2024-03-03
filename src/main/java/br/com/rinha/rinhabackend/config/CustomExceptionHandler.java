package br.com.rinha.rinhabackend.config;

import br.com.rinha.rinhabackend.error.NotFoundException;
import br.com.rinha.rinhabackend.error.UnprocessableEntityException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


@Provider
public class CustomExceptionHandler implements ExceptionMapper<Exception> {

  private static final Response NOT_FOUND = Response.status(Response.Status.NOT_FOUND).build();
  private static final Response UNPROCESSABLE_ENTITY = Response.status(422).build();

  @Override
  public Response toResponse(Exception exception) {
    if (exception instanceof NotFoundException) {
      return NOT_FOUND;
    }
    if (exception instanceof UnprocessableEntityException) {
      return UNPROCESSABLE_ENTITY;
    }

    return Response.serverError().build();
  }
}