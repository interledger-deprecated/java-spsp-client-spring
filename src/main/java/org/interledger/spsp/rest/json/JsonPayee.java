package org.interledger.spsp.rest.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.interledger.spsp.core.model.Payee;

import java.net.URL;

public class JsonPayee extends JsonReceiver implements Payee {

  private String name;
  private URL imageUrl;

  @Override
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  @Override
  @JsonProperty("image_url")
  public URL getImageUrl() {
    return imageUrl;
  }  
  
  public void setImageUrl(URL imageUrl) {
    this.imageUrl = imageUrl;
  }
}

