package org.interledger.spsp.json.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonRequest {
  private String amount;
  private String senderIdentifier;
  private String memo;
  
  @JsonProperty("amount")
  public String getAmount() {
    return amount;
  }
  
  public void setAmount(String amount) {
    this.amount = amount;
  }
  
  @JsonProperty("sender_identifier")
  public String getSenderIdentifier() {
    return senderIdentifier;
  }
  
  public void setSenderIdentifier(String senderIdentifier) {
    this.senderIdentifier = senderIdentifier;
  }
  
  @JsonProperty("memo")
  public String getMemo() {
    return memo;
  }
  
  public void setMemo(String memo) {
    this.memo = memo;
  }
}

