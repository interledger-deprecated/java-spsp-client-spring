package org.interledger.spsp.json;

import java.net.URI;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.interledger.ilp.InterledgerPaymentRequest;
import org.interledger.ilp.ledger.money.format.LedgerSpecificDecimalMonetaryAmountFormat;
import org.interledger.spsp.client.model.ClientInvoice;
import org.interledger.spsp.client.model.ClientPayee;
import org.interledger.spsp.client.model.ClientReceiver;
import org.interledger.spsp.json.model.JsonInterledgerPaymentRequest;
import org.interledger.spsp.json.model.JsonInvoice;
import org.interledger.spsp.json.model.JsonPayee;
import org.interledger.spsp.json.model.JsonReceiver;
import org.interledger.setup.model.Receiver;
import org.interledger.setup.spsp.model.Invoice;
import org.interledger.setup.spsp.model.Payee;
import org.interledger.setup.spsp.model.ReceiverType;
import org.interledger.setup.spsp.model.SpspReceiver;

public class SpspJsonConverter {
  
  public static InterledgerPaymentRequest convertJsonInterledgerPaymentRequest(Receiver receiver, JsonInterledgerPaymentRequest jsonIpr) {

    LedgerSpecificDecimalMonetaryAmountFormat formatter = 
        new LedgerSpecificDecimalMonetaryAmountFormat(receiver.getCurrencyUnit(), receiver.getPrecision(), receiver.getScale());
    
    InterledgerPaymentRequest ipr = new InterledgerPaymentRequest();
    ipr.setAddress(jsonIpr.getAddress());
    ipr.setAmount(formatter.parse(jsonIpr.getAmount()));
    ipr.setCondition(jsonIpr.getCondition());
    ipr.setExpiresAt(jsonIpr.getExpiresAt());
    ipr.setData(jsonIpr.getData());
    ipr.setAdditionalHeaders(jsonIpr.getAdditionalHeaders());
    return ipr;
  }
  
  public static SpspReceiver convertJsonReceiver(URI receiverEndpoint, JsonReceiver jsonReceiver) {
    
    
    
    ReceiverType type = jsonReceiver.getType();
    CurrencyUnit currency = Monetary.getCurrency(jsonReceiver.getCurrencyCode());
    ClientReceiver receiver;
    
    switch(type) {
      case invoice:
        ClientInvoice invoice = new ClientInvoice();
        receiver = invoice;
        JsonInvoice jsonInvoice = (JsonInvoice) jsonReceiver;
        invoice.setInvoiceInfo(jsonInvoice.getInvoiceInfo());
        invoice.setStatus(jsonInvoice.getStatus());
        LedgerSpecificDecimalMonetaryAmountFormat formatter = 
            new LedgerSpecificDecimalMonetaryAmountFormat(currency, jsonInvoice.getPrecision(), jsonReceiver.getScale());
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
    if(receiver.getPrecision() == 0 && receiver.getScale() == 0){
      receiver.setPrecision(10);
      receiver.setScale(2);
    }

    return receiver;
      
  }
  
  public static JsonReceiver convertSpspReceiver(SpspReceiver receiver) {
    
    
    LedgerSpecificDecimalMonetaryAmountFormat formatter = new LedgerSpecificDecimalMonetaryAmountFormat(receiver.getCurrencyUnit(), receiver.getPrecision(), receiver.getScale());
    
    JsonReceiver jsonReceiver;
    if(receiver instanceof Invoice){
      
      Invoice invoice = (Invoice) receiver;
      JsonInvoice jsonInvoice = new JsonInvoice();
      jsonReceiver = jsonInvoice;
      
      jsonInvoice.setType(ReceiverType.invoice);
      
      jsonInvoice.setInvoiceInfo(invoice.getInvoiceInfo());
      jsonInvoice.setAmount(formatter.format(invoice.getAmount()));
      jsonInvoice.setStatus(invoice.getStatus());
      
    } else if(receiver instanceof Payee) {
      
      Payee payee = (Payee) receiver;
      JsonPayee jsonPayee = new JsonPayee();
      jsonReceiver = jsonPayee;
      
      jsonPayee.setType(ReceiverType.payee);
      
      jsonPayee.setName(payee.getName());
      jsonPayee.setImageUrl(payee.getImageUrl());      
      
    } else {
      throw new IllegalArgumentException("Unknown SPSP receiver type.");
    }
    
    jsonReceiver.setAccount(receiver.getAccount());
    jsonReceiver.setCurrencyCode(receiver.getCurrencyUnit().getCurrencyCode());
    jsonReceiver.setCurrencySymbol(""); //TODO Should this come from the formatter?
    jsonReceiver.setPrecision(receiver.getPrecision());
    jsonReceiver.setScale(receiver.getScale());
    
    return jsonReceiver;
    
  }

  public static JsonInterledgerPaymentRequest convertInterledgerPaymentRequest(Receiver receiver, InterledgerPaymentRequest ipr) {
    
    LedgerSpecificDecimalMonetaryAmountFormat formatter = 
        new LedgerSpecificDecimalMonetaryAmountFormat(receiver.getCurrencyUnit(), receiver.getPrecision(), receiver.getScale());
    
    JsonInterledgerPaymentRequest jsonIpr = new JsonInterledgerPaymentRequest();
    jsonIpr.setAddress(ipr.getAddress());
    jsonIpr.setAmount(formatter.format(ipr.getAmount()));
    jsonIpr.setCondition(ipr.getCondition());
    jsonIpr.setExpiresAt(ipr.getExpiresAt());
    jsonIpr.setData(ipr.getData());
    jsonIpr.setAdditionalHeaders(ipr.getAdditionalHeaders());
    return jsonIpr;  
    
  }

}
