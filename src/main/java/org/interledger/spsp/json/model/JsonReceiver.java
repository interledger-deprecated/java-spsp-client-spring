package org.interledger.spsp.json.model;

import org.interledger.ilp.InterledgerAddress;
import org.interledger.spsp.json.InterledgerAddressSerializer;
import org.interledger.setup.spsp.model.ReceiverType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = Include.NON_NULL)
@JsonTypeInfo(
    use = Id.NAME,
    include = As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes(
    {@Type(value = JsonPayee.class, name = "payee"),
    @Type(value = JsonInvoice.class, name = "invoice")})
public abstract class JsonReceiver {

  private ReceiverType type;
  private InterledgerAddress account;
  private String currencyCode;
  private String currencySymbol;
  private int scale;
  private int precision;

  @JsonProperty("type")
  public ReceiverType getType() {
    return type;
  }
  
  public void setType(ReceiverType type) {
    this.type = type;
  }

  @JsonProperty("account")
  public InterledgerAddress getAccount() {
    return account;
  }
  
  public void setAccount(InterledgerAddress account) {
    this.account = account;
  }

  @JsonProperty("currency_code")
  public String getCurrencyCode() {
    return currencyCode;
  }
  
  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  @JsonProperty("currency_symbol")
  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public void setCurrencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
  }
  
  @JsonProperty("precison")
  public int getPrecision() {
    return precision;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }
  
  @JsonProperty("scale")
  public int getScale() {
    return scale;
  }

  public void setScale(int scale) {
    this.scale = scale;
  }
  
  @Override
  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    //necessary as jackson gets the address pretty wrong
    SimpleModule module = new SimpleModule();
    module.addSerializer(new InterledgerAddressSerializer());
    mapper.registerModule(module);
    
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (JsonProcessingException jsonException) {
      throw new RuntimeException(jsonException);
    }
  }
}

