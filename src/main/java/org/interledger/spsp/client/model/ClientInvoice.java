package org.interledger.spsp.client.model;

import org.interledger.setup.spsp.model.Invoice;
import org.interledger.setup.spsp.model.InvoiceStatus;

import java.net.URI;
import javax.money.MonetaryAmount;

public class ClientInvoice extends ClientReceiver implements Invoice {

  private MonetaryAmount amount;
  private InvoiceStatus status;
  private URI invoiceInfo;

  @Override
  public MonetaryAmount getAmount() {
    return amount;
  }

  @Override
  public InvoiceStatus getStatus() {
    return status;
  }

  @Override
  public URI getInvoiceInfo() {
    return invoiceInfo;
  }

  public void setAmount(MonetaryAmount amount) {
    this.amount = amount;
  }

  public void setStatus(InvoiceStatus status) {
    this.status = status;
  }

  public void setInvoiceInfo(URI invoiceInfo) {
    this.invoiceInfo = invoiceInfo;
  }

}
