package org.interledger.spsp.client.model;

import org.interledger.ilp.InterledgerAddress;
import org.interledger.setup.spsp.model.ReceiverType;
import org.interledger.setup.spsp.model.SpspReceiver;

import java.net.URI;
import javax.money.CurrencyUnit;

public abstract class ClientReceiver implements SpspReceiver {

  private URI endpoint;
  private ReceiverType type;
  private InterledgerAddress account;
  private CurrencyUnit currency;
  private int precision;
  private int scale;

  @Override
  public URI getEndpoint() {
    return endpoint;
  }

  @Override
  public ReceiverType getType() {
    return type;
  }

  @Override
  public InterledgerAddress getAccount() {
    return account;
  }

  @Override
  public CurrencyUnit getCurrencyUnit() {
    return currency;
  }

  @Override
  public int getPrecision() {
    return precision;
  }

  @Override
  public int getScale() {
    return scale;
  }

  public void setEndpoint(URI endpoint) {
    this.endpoint = endpoint;
  }
  
  public void setType(ReceiverType type) {
    this.type = type;
  }

  public void setAccount(InterledgerAddress account) {
    this.account = account;
  }

  public void setCurrencyUnit(CurrencyUnit currency) {
    this.currency = currency;
  }
  
  public void setScale(int scale) {
    this.scale = scale;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }
  
}
