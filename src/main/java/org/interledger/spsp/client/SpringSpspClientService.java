package org.interledger.spsp.client;

import org.interledger.spsp.core.SpspService;
import org.interledger.spsp.core.model.PaymentRequest;
import org.interledger.spsp.core.model.Receiver;
import org.interledger.spsp.rest.json.JsonPaymentRequest;
import org.interledger.spsp.rest.json.JsonReceiver;
import org.interledger.spsp.rest.json.JsonRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

/**
 * An implemetation of a Simple Payment Setup client service using a Spring rest template. 
 */
public class SpringSpspClientService implements SpspService {

  private RestTemplate restTemplate;
  
  public SpringSpspClientService() {
    this.restTemplate = new RestTemplateBuilder().build();
  }
  
  public SpringSpspClientService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  
  @Override
  public Receiver query(URI endpoint) {
    Objects.requireNonNull(endpoint);
    
    return restTemplate.getForObject(endpoint, JsonReceiver.class);
  }

  @Override
  public PaymentRequest setupPayment(URI endpoint, String amount, String senderIdentifier,
      String memo) {
    Objects.requireNonNull(endpoint);
    Objects.requireNonNull(senderIdentifier);

    JsonRequest req = new JsonRequest();
    req.setAmount(amount);
    req.setSenderIdentifier(senderIdentifier);
    req.setMemo(memo);

    return restTemplate.postForObject(endpoint, req, JsonPaymentRequest.class);
  }

}

