package org.interledger.spsp.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.interledger.cryptoconditions.Condition;
import org.interledger.cryptoconditions.types.PreimageSha256Condition;
import org.interledger.cryptoconditions.uri.CryptoConditionUri;
import org.interledger.cryptoconditions.uri.URIEncodingException;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;

@SuppressWarnings("serial")
//TODO: maybe this should be in java-crypto-conditions for anyone to use?
public class ConditionDeserializer extends StdDeserializer<Condition> {

  public ConditionDeserializer() {
    super(Condition.class);
  }

  @Override
  public Condition deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    
    try {
      URI conditionUri = URI.create(parser.getValueAsString());
      
      //FIXME Temporary fix for old URI format
      if ("cc".equals(conditionUri.getScheme())) {
        return getConditionFromOldUri(conditionUri);
      }
      
      return CryptoConditionUri.parse(conditionUri);
    } catch (URIEncodingException uriException) {
      throw new JsonParseException(parser, "invalid crypto condition uri", uriException);
    }
  }
  
  private Condition getConditionFromOldUri(URI uri) {
    
    String[] parts = uri.toString().split(":");
    
    if (!"0".equals(parts[1])) {
      throw new RuntimeException("Only PreimageSha256 conditions are supported in the old format.");
    }
    
    byte[] fingerprint = Base64.getUrlDecoder().decode(parts[3]);
    int cost = Integer.valueOf(parts[4]);
    return new PreimageSha256Condition(fingerprint, cost);
  }

}

