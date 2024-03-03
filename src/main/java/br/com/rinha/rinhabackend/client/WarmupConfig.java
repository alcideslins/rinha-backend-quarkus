package br.com.rinha.rinhabackend.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class WarmupConfig {

  @ConfigProperty(name = "warmup.enabled")
  boolean enabled;

  @ConfigProperty(name = "warmup.requests")
  int requests;

  @ConfigProperty(name = "warmup.delay.seconds")
  int delayInSeconds;

  @Inject
  public WarmupConfig() {
    // Required for CDI
  }

  public boolean isEnabled() {
    return enabled;
  }

  public int getRequests() {
    return requests;
  }

  public int getDelayInSeconds() {
    return delayInSeconds;
  }
}
