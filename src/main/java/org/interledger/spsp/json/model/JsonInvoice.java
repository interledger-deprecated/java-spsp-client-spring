package org.interledger.spsp.json.model;

import java.net.URI;

import org.interledger.setup.spsp.model.InvoiceStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonInvoice extends JsonReceiver {

  private String amount;
  private InvoiceStatus status;
  private URI invoiceInfo;
  
  @JsonProperty("amount")
  public String getAmount() {
    return amount;
  }
  
  public void setAmount(String amount) {
    this.amount = amount;
  }

  @JsonProperty("status")
  public InvoiceStatus getStatus() {
    return status;
  }
  
  public void setStatus(InvoiceStatus status) {
    this.status = status;
  }

  @JsonProperty("invoice_info")
  public URI getInvoiceInfo() {
    return invoiceInfo;
  }
  
  public void setInvoiceInfo(URI invoiceInfo) {
    this.invoiceInfo = invoiceInfo;
  }
}

