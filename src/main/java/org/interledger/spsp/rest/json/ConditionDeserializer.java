package org.interledger.spsp.rest.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.interledger.cryptoconditions.Condition;
import org.interledger.cryptoconditions.uri.CryptoConditionUri;
import org.interledger.cryptoconditions.uri.URIEncodingException;

import java.io.IOException;
import java.net.URI;

@SuppressWarnings("serial")
//TODO: maybe this should be in java-crypto-conditions for anyone to use?
public class ConditionDeserializer extends StdDeserializer<Condition> {

  public ConditionDeserializer() {
    super(Condition.class);
  }

  @Override
  public Condition deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    String conditionUri = parser.getValueAsString();
    
    try {
      return CryptoConditionUri.parse(URI.create(conditionUri));
    } catch (URIEncodingException uriException) {
      throw new JsonParseException(parser, "invalid crypto condition uri", uriException);
    }
  }

}

