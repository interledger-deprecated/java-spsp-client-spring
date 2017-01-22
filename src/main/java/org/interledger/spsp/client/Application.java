package org.interledger.spsp.client;

import org.interledger.spsp.core.SpspService;
import org.interledger.spsp.core.model.PaymentRequest;
import org.interledger.spsp.core.model.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

import java.net.URI;

@SpringBootApplication
public class Application {

  private static final Logger log = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    SpringApplication.run(Application.class);
  }

  @Bean
  public SpspService spspService(RestTemplateBuilder builder) {
    
    return new SpringSpspClientService(builder.build());
  }
  
  /**
   * Simple command line runner for basic testing.
   *
   * @param service
   *  The Simple Payment Setup Protocol service to use.
   * @return
   *  A commandline runner.
   * @throws Exception
   *  If there was an error using the service.
   */
  @Bean
  public CommandLineRunner run(SpspService service) throws Exception {
    return args -> {
      Receiver bob = service.query(URI.create("https://blue.ilpdemo.org/api/receivers/bob"));
      log.info("asked for receiver bob, got {}", bob);
        
      Receiver alice = service.query(URI.create("https://red.ilpdemo.org/api/receivers/alice"));
      log.info("asked for receiver alice, got {}", alice);
      
      PaymentRequest bobPayReq = service.setupPayment(URI.create("https://blue.ilpdemo.org/api/receivers/bob"), "10.40", "test@ipldemo.org", "totally fake for testing");
      log.info("asked bob to set up a payment, got {}", bobPayReq);
  
      PaymentRequest alicePayReq = service.setupPayment(URI.create("https://red.ilpdemo.org/api/receivers/alice"), "10.40", "test@ipldemo.org", "totally fake for testing");
      log.info("asked alice to set up a payment, got {}", alicePayReq);
    };
  }
}

