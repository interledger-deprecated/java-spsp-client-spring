package org.interledger.spsp.json.model;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonPayee extends JsonReceiver{

  private String name;
  private URI imageUrl;

  @JsonProperty("name")
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("image_url")
  public URI getImageUrl() {
    return imageUrl;
  }  
  
  public void setImageUrl(URI imageUrl) {
    this.imageUrl = imageUrl;
  }
}

