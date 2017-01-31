package org.interledger.spsp.server;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.money.MonetaryAmount;

import org.interledger.cryptoconditions.Fulfillment;
import org.interledger.cryptoconditions.types.PreimageSha256Fulfillment;
import org.interledger.ilp.InterledgerAddress;
import org.interledger.ilp.InterledgerPaymentRequest;
import org.interledger.ilp.client.LedgerClient;
import org.interledger.ilp.ledger.model.LedgerInfo;
import org.interledger.ilp.ledger.money.format.LedgerSpecificDecimalMonetaryAmountFormat;
import org.interledger.setup.SetupService;
import org.interledger.setup.model.Receiver;
import org.interledger.setup.model.ReceiverQuery;
import org.interledger.setup.spsp.model.ReceiverType;
import org.interledger.setup.spsp.model.SpspReceiver;
import org.interledger.setup.spsp.model.SpspReceiverQuery;
import org.interledger.spsp.client.model.ClientPayee;

public abstract class BasicSpspSetupService implements SetupService{
  
  private Map<URI, SpspReceiver> receivers;
  private Map<InterledgerAddress, LedgerClient> clients;
  private Duration defaultExpiryDuration;
  
  public BasicSpspSetupService(Duration defaultExpiryDuration) {
    receivers = new ConcurrentHashMap<>();
    clients = new ConcurrentHashMap<>();
    this.defaultExpiryDuration = defaultExpiryDuration;
  }
  
  //TODO This service only returns payee type receiver's with no additional meta-data (name, image_url)
  public void addReceiver(URI receiverEndpoint, InterledgerAddress address, LedgerClient client) {
        
    LedgerInfo ledgerInfo = client.getLedgerInfo(); 
    
    if(!address.startsWith(client.getAccount())) {
      throw new IllegalArgumentException("Address must be a sub-account address of the client's account.");
    }
    
    ClientPayee payee = new ClientPayee();
    payee.setType(ReceiverType.payee);
    payee.setAccount(address);
    payee.setCurrencyUnit(ledgerInfo.getCurrencyUnit());
    payee.setPrecision(ledgerInfo.getPrecision());
    payee.setScale(ledgerInfo.getScale());
    payee.setEndpoint(receiverEndpoint);
    
    receivers.put(receiverEndpoint, payee);
    clients.put(address, client);
    
  }

  @Override
  public Receiver query(ReceiverQuery query) {
    
    if(!(query instanceof SpspReceiverQuery)){
      throw new IllegalArgumentException("Only SPSP receiver queries are allowed.");
    }
    
    SpspReceiverQuery spspQuery = (SpspReceiverQuery) query;
    URI receiverEndpoint = spspQuery.getReceiverEndpoint();
    return receivers.get(receiverEndpoint);
        
  }

  @Override
  public InterledgerPaymentRequest setupPayment(Receiver receiver, MonetaryAmount amount, String senderIdentifier, String memo) {
    
    if(!(receiver instanceof SpspReceiver)){
      throw new IllegalArgumentException("Only SPSP receivers are allowed.");
    }
    
    SpspReceiver spspReceiver = (SpspReceiver) receiver;
    LedgerClient client = clients.get(spspReceiver.getAccount());
    
    if(client == null) {
      throw new IllegalArgumentException("No client loaded for address."); //TODO Is this the right exception to throw?
    }
    
    LedgerInfo ledgerInfo = client.getLedgerInfo(); 
    ZonedDateTime expiry = calculateExpiry(receiver.getAccount(), amount);
    Fulfillment fulfillment = createCryptoCondition(ledgerInfo, receiver.getAccount(), amount, expiry, memo);
    
    InterledgerPaymentRequest ipr = new InterledgerPaymentRequest();
    ipr.setAddress(receiver.getAccount());
    ipr.setAmount(amount);
    ipr.setCondition(fulfillment.getCondition());
    ipr.setExpiresAt(expiry);
    ipr.setData(memo);
    ipr.setAdditionalHeaders(""); //FIXME Where do we get this?
    
    return ipr;
  }
  
  protected ZonedDateTime calculateExpiry(InterledgerAddress address, MonetaryAmount amount) {
    return ZonedDateTime.now().plus(defaultExpiryDuration);
  }
  
  //FIXME This is not standardized and ignores the sender data
  //FIXME This should use an HMAC of the normalized data
  protected Fulfillment createCryptoCondition(LedgerInfo ledger, InterledgerAddress address, MonetaryAmount amount, ZonedDateTime expiry, Object data) {
    
    LedgerSpecificDecimalMonetaryAmountFormat formatter = new LedgerSpecificDecimalMonetaryAmountFormat(ledger);
    
    StringBuilder sb = new StringBuilder();
    sb.append(address.toString())
      .append(":")
      .append(formatter.format(amount))
      .append(":")
      .append(expiry.toString());
    
    byte[] preimage = sb.toString().getBytes(Charset.forName("UTF-8"));
    
    return new PreimageSha256Fulfillment(preimage);
    
  }
  
}
