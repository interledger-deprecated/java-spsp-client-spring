package org.interledger.spsp.server;

import java.net.URI;

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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriTemplate;

@RestController
public class SpspController {
  
    private static final String URL_TEMPLATE = "/receiver/{id}";
  
    private UriTemplate uriTemplate;
    private SetupService setupService;
  
    public SpspController(URI baseUrl, SetupService setupService) {
      this.setupService = setupService;
      uriTemplate = new UriTemplate(baseUrl.resolve(URL_TEMPLATE).toString());
    }
    
    public URI getReceiverEndpoint(String id) {
      return uriTemplate.expand(id);
    }

    @RequestMapping(path=URL_TEMPLATE, method=RequestMethod.GET)
    public JsonReceiver getReceiver(@PathVariable(name="id", required=true) String id) {
      SpspReceiver receiver = getSpspReceiver(id);
      return SpspJsonConverter.convertSpspReceiver(receiver); 
    }
    
    @RequestMapping(path=URL_TEMPLATE, method=RequestMethod.POST)
    public JsonInterledgerPaymentRequest createInterledgerPaymentRequest(@PathVariable(name="id", required=true) String id, @RequestBody JsonRequest request) {
      SpspReceiver receiver = getSpspReceiver(id);
      
      LedgerSpecificDecimalMonetaryAmountFormat formatter = 
          new LedgerSpecificDecimalMonetaryAmountFormat(receiver.getCurrencyUnit(), receiver.getPrecision(), receiver.getScale());
      MonetaryAmount amount = formatter.parse(request.getAmount());
      InterledgerPaymentRequest ipr = setupService.setupPayment(receiver, amount, request.getSenderIdentifier(), request.getMemo());
      
      return SpspJsonConverter.convertInterledgerPaymentRequest(receiver, ipr);
    }

    private SpspReceiver getSpspReceiver(String id) {
      
      ReceiverQuery query = new SpspReceiverQuery(getReceiverEndpoint(id));
      Receiver receiver = setupService.query(query);
      
      if(receiver == null) {
        throw new HttpServerErrorException(HttpStatus.NOT_FOUND);
      }
      
      if(!(receiver instanceof SpspReceiver)) {
        throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Invalid receiver.");
      }
      
      return (SpspReceiver) receiver;
    }
}