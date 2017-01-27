package org.interledger.spsp.json;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Base64;

import org.interledger.cryptoconditions.Condition;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings("serial")
//TODO: maybe this should be in java-crypto-conditions for anyone to use?
public class ConditionSerializer extends StdSerializer<Condition> {

  public ConditionSerializer() {
    super(Condition.class);
  }

  @Override
  public void serialize(Condition value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    
    //FIXME Switch to the new URI format
    //gen.writeString(value.getUri().toString());
    gen.writeString(getOldConditionUri(value).toString());
  }
  
  private URI getOldConditionUri(Condition condition) {
    
    String fingerprint = new String(Base64.getUrlEncoder().encode(condition.getFingerprint()), Charset.forName("UTF-8"));
    String cost = Long.toUnsignedString(condition.getCost());
    return URI.create("cc:0:3:" + fingerprint + ":" + cost);
  }
}

