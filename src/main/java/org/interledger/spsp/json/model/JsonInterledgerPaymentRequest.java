package org.interledger.spsp.json.model;

import java.time.ZonedDateTime;

import org.interledger.cryptoconditions.Condition;
import org.interledger.ilp.InterledgerAddress;
import org.interledger.spsp.json.ConditionDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class JsonInterledgerPaymentRequest {

  private InterledgerAddress address;
  private String amount;
  @JsonDeserialize(using = ConditionDeserializer.class)
  private Condition condition;
  private ZonedDateTime expiresAt;
  private Object data;
  private String additionalHeaders;

  
  @JsonProperty("address")
  public InterledgerAddress getAddress() {
    return address;
  }
  
  public void setAddress(InterledgerAddress address) {
    this.address = address;
  }

  @JsonProperty("amount")
  public String getAmount() {
    return amount;
  }
  
  public void setAmount(String amount) {
    this.amount = amount;
  }

  @JsonProperty("condition")
  public Condition getCondition() {
    return condition;
  }

  public void setCondition(Condition condition) {
    this.condition = condition;
  }

  @JsonProperty("expires_at")
  public ZonedDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(ZonedDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  @JsonProperty("data")
  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  @JsonProperty("additional_headers")
  public String getAdditionalHeaders() {
    return additionalHeaders;
  }

  public void setAdditionalHeaders(String additionalHeaders) {
    this.additionalHeaders = additionalHeaders;
  }
  
  @Override
  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (JsonProcessingException jsonException) {
      throw new RuntimeException(jsonException);
    }
  }

}

