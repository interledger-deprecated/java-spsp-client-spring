package org.interledger.spsp.json;

import org.interledger.ilp.InterledgerPaymentRequest;
import org.interledger.ilp.ledger.money.format.LedgerSpecificDecimalMonetaryAmountFormat;
import org.interledger.setup.model.Receiver;
import org.interledger.setup.spsp.model.ReceiverType;
import org.interledger.setup.spsp.model.SpspReceiver;
import org.interledger.spsp.client.model.ClientInvoice;
import org.interledger.spsp.client.model.ClientPayee;
import org.interledger.spsp.client.model.ClientReceiver;
import org.interledger.spsp.json.model.JsonInterledgerPaymentRequest;
import org.interledger.spsp.json.model.JsonInvoice;
import org.interledger.spsp.json.model.JsonPayee;
import org.interledger.spsp.json.model.JsonReceiver;

import java.net.URI;
import javax.money.CurrencyUnit;
import javax.money.Monetary;

public class SpspJsonConverter {

  /**
   * Converts JSON interledger payment request to InterledgerPaymentRequest.
   * @param receiver Receiver
   * @param jsonIpr JSON interledger payment request
   * @return ipr InterledgerPaymentRequest
   */
  public static InterledgerPaymentRequest convertJsonInterledgerPaymentRequest(Receiver receiver,
                                                          JsonInterledgerPaymentRequest jsonIpr) {

    LedgerSpecificDecimalMonetaryAmountFormat formatter = 
        new LedgerSpecificDecimalMonetaryAmountFormat(receiver.getCurrencyUnit(),
                receiver.getPrecision(),
                receiver.getScale());
    
    InterledgerPaymentRequest ipr = new InterledgerPaymentRequest();
    ipr.setAddress(jsonIpr.getAddress());
    ipr.setAmount(formatter.parse(jsonIpr.getAmount()));
    ipr.setCondition(jsonIpr.getCondition());
    ipr.setExpiresAt(jsonIpr.getExpiresAt());
    ipr.setData(jsonIpr.getData());
    ipr.setAdditionalHeaders(jsonIpr.getAdditionalHeaders());
    return ipr;
  }

  /**
   * Converts JSON receiver to SpspReceiver.
   * @param receiverEndpoint Receiver endpoint
   * @param jsonReceiver JSON receiver
   * @return receiver SpspReceiver
   */
  public static SpspReceiver convertJsonReceiver(URI receiverEndpoint, JsonReceiver jsonReceiver) {
    
    
    
    ReceiverType type = jsonReceiver.getType();
    CurrencyUnit currency = Monetary.getCurrency(jsonReceiver.getCurrencyCode());
    ClientReceiver receiver;
    
    switch (type) {
      case invoice:
        ClientInvoice invoice = new ClientInvoice();
        receiver = invoice;
        JsonInvoice jsonInvoice = (JsonInvoice) jsonReceiver;
        invoice.setInvoiceInfo(jsonInvoice.getInvoiceInfo());
        invoice.setStatus(jsonInvoice.getStatus());
        LedgerSpecificDecimalMonetaryAmountFormat formatter = 
            new LedgerSpecificDecimalMonetaryAmountFormat(currency,
                    jsonInvoice.getPrecision(),
                    jsonReceiver.getScale());
        invoice.setAmount(formatter.parse(jsonInvoice.getAmount()));
        break;
      case payee:
        ClientPayee payee = new ClientPayee();
        receiver = payee;
        JsonPayee jsonPayee = (JsonPayee) jsonReceiver;
        payee.setName(jsonPayee.getName());
        payee.setImageUrl(jsonPayee.getImageUrl());
        break;
      default:
        throw new RuntimeException("Unknown receiver type.");
    }
    
    receiver.setType(type);
    receiver.setAccount(jsonReceiver.getAccount());
    receiver.setCurrencyUnit(currency);
    receiver.setEndpoint(receiverEndpoint);
    receiver.setPrecision(jsonReceiver.getPrecision());
    receiver.setScale(jsonReceiver.getScale());
    
    //TODO Temp fix for older APIs that don't return this info
    if (receiver.getPrecision() == 0 && receiver.getScale() == 0) {
      receiver.setPrecision(10);
      receiver.setScale(2);
    }

    return receiver;
      
  }
  
  

}
