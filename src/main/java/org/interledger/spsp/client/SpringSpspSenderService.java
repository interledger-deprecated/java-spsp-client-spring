package org.interledger.spsp.client;

import java.util.Objects;

import javax.money.MonetaryAmount;

import org.interledger.ilp.InterledgerPaymentRequest;
import org.interledger.ilp.ledger.money.format.LedgerSpecificDecimalMonetaryAmountFormat;
import org.interledger.setup.SetupService;
import org.interledger.setup.model.Receiver;
import org.interledger.setup.model.ReceiverQuery;
import org.interledger.setup.spsp.model.SpspReceiver;
import org.interledger.setup.spsp.model.SpspReceiverQuery;
import org.interledger.spsp.json.SpspJsonConverter;
import org.interledger.spsp.json.model.JsonInterledgerPaymentRequest;
import org.interledger.spsp.json.model.JsonReceiver;
import org.interledger.spsp.json.model.JsonRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

/**
 * An implementation of a Simple Payment Setup Protocol sender client service using a Spring rest template. 
 */
public class SpringSpspSenderService implements SetupService {

  private RestTemplate restTemplate;
  
  public SpringSpspSenderService() {
    this.restTemplate = new RestTemplateBuilder().build();
  }
  
  public SpringSpspSenderService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  
  @Override
  public SpspReceiver query(ReceiverQuery query) {
    
    if(!(query instanceof SpspReceiverQuery)){
      throw new IllegalArgumentException("Only SPSP receiver queries are allowed.");
    }
    
    SpspReceiverQuery spspQuery = (SpspReceiverQuery) query;
    
    Objects.requireNonNull(spspQuery.getReceiverEndpoint());
    
    JsonReceiver jsonReceiver = restTemplate.getForObject(spspQuery.getReceiverEndpoint(), JsonReceiver.class);
    return SpspJsonConverter.convertJsonReceiver(spspQuery.getReceiverEndpoint(), jsonReceiver);
  }

  @Override
  public InterledgerPaymentRequest setupPayment(Receiver receiver, MonetaryAmount amount, String senderIdentifier, String memo) {
    
    if(!(receiver instanceof SpspReceiver)) {
      throw new IllegalArgumentException("Only SPSP receivers are allowed.");
    }
    
    SpspReceiver spspReceiver = (SpspReceiver) receiver;
    
    Objects.requireNonNull(spspReceiver.getEndpoint());
    Objects.requireNonNull(amount);
    Objects.requireNonNull(senderIdentifier);

    LedgerSpecificDecimalMonetaryAmountFormat formatter = 
        new LedgerSpecificDecimalMonetaryAmountFormat(spspReceiver.getCurrencyUnit(), spspReceiver.getPrecision(), spspReceiver.getScale());
    
    JsonRequest req = new JsonRequest();
    req.setAmount(formatter.format(amount));
    req.setSenderIdentifier(senderIdentifier);
    req.setMemo(memo);

    JsonInterledgerPaymentRequest jsonIpr = restTemplate.postForObject(spspReceiver.getEndpoint(), req, JsonInterledgerPaymentRequest.class);
    return SpspJsonConverter.convertJsonInterledgerPaymentRequest(spspReceiver, jsonIpr);
    
  }
  
}

