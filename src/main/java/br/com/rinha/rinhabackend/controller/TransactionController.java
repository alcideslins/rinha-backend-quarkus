package br.com.rinha.rinhabackend.controller;

import br.com.rinha.rinhabackend.model.statement.StatementResponse;
import br.com.rinha.rinhabackend.model.transaction.TransactionRequest;
import br.com.rinha.rinhabackend.model.transaction.TransactionResponse;
import br.com.rinha.rinhabackend.service.TransactionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;


@Path("/clientes")
public class TransactionController {


  private final TransactionService service;

  @Inject
  TransactionController(TransactionService service) {
    this.service = service;
  }

  @POST
  @Path("/{id}/transacoes")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public TransactionResponse transaction(@PathParam("id") int id, TransactionRequest transactionRequest) {
    return service.register(id, transactionRequest);
  }

  @GET
  @Path("/{id}/extrato")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public StatementResponse statement(@PathParam("id") int id) {
    return service.list(id);
  }
}
