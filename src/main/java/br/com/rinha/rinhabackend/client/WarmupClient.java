package br.com.rinha.rinhabackend.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "my-api")
public interface WarmupClient {


  @GET
  @Path("/clientes/{clientId}/extrato")
  String fetchStatement(@PathParam("clientId") int clientId);

  @POST
  @Path("/clientes/{clientId}/transacoes")
  String registerTransaction(@PathParam("clientId") int id, String request);
}
