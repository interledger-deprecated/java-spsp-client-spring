package org.interledger.spsp.rest.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.interledger.spsp.core.model.Invoice;
import org.interledger.spsp.core.model.InvoiceStatus;

import java.net.URI;

public class JsonInvoice extends JsonReceiver implements Invoice {

  private String amount;
  private InvoiceStatus status;
  private URI invoiceInfo;
  
  @Override
  @JsonProperty("amount")
  public String getAmount() {
    return amount;
  }
  
  public void setAmount(String amount) {
    this.amount = amount;
  }

  @Override
  @JsonProperty("status")
  public InvoiceStatus getStatus() {
    return status;
  }
  
  public void setStatus(InvoiceStatus status) {
    this.status = status;
  }

  @Override
  @JsonProperty("invoice_info")
  public URI getInvoiceInfo() {
    return invoiceInfo;
  }
  
  public void setInvoiceInfo(URI invoiceInfo) {
    this.invoiceInfo = invoiceInfo;
  }
}

