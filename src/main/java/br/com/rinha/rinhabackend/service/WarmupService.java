package br.com.rinha.rinhabackend.service;

import br.com.rinha.rinhabackend.client.WarmupClient;
import br.com.rinha.rinhabackend.client.WarmupConfig;
import br.com.rinha.rinhabackend.repository.RegisterTransactionRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.IntStream;

import static br.com.rinha.rinhabackend.model.transaction.TransactionRequest.CREDIT;
import static br.com.rinha.rinhabackend.model.transaction.TransactionRequest.DEBIT;

@ApplicationScoped
public class WarmupService {

  private final WarmupClient warmupClient;
  private final RegisterTransactionRepository registerRepository;
  private final WarmupConfig config;

  private static final Logger LOGGER = LoggerFactory.getLogger(WarmupService.class);


  @Inject
  public WarmupService(@Any WarmupClient warmupClient, RegisterTransactionRepository registerRepository, WarmupConfig config) {
    this.warmupClient = warmupClient;
    this.registerRepository = registerRepository;
    this.config = config;
  }

  void onStart(@Observes StartupEvent ev) {
    if (config.isEnabled()) {
      LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(config.getDelayInSeconds()));
      CompletableFuture.runAsync(this::performWarmup);
    }
  }

  public void performWarmup() {
    LOGGER.info("Starting warmup...");

    IntStream.of(config.getRequests()).forEach(i -> IntStream.rangeClosed(4, 10).forEach(id -> {
      executeStatement(id);
      executeTransaction(id, "{\"tipo\": \"" + CREDIT + "\", \"valor\": 100, \"descricao\": \"Example\"}");
      executeTransaction(id, "{\"tipo\": \"" + DEBIT + "\", \"valor\": 100, \"descricao\": \"Example\"}");
    }));

    registerRepository.resetDb();
    System.gc();
    LOGGER.info("finished warmup");
  }

  private void executeStatement(int id) {
    try {
      warmupClient.fetchStatement(id);
    } catch (Exception exception) {
      handleError(exception);
    }
  }

  private void executeTransaction(int id, String request) {
    try {
      warmupClient.registerTransaction(id, request);
    } catch (Exception exception) {
      handleError(exception);
    }
  }

  private void handleError(Exception ex) {
    if (ex instanceof WebApplicationException clientError) {
      int status = clientError.getResponse().getStatus();
      if (status < Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
        return;
      }
    }
    LOGGER.error("Error on warmup request", ex);
  }
}
