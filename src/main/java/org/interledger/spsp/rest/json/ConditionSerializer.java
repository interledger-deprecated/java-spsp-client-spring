package org.interledger.spsp.rest.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.interledger.cryptoconditions.Condition;

import java.io.IOException;

@SuppressWarnings("serial")
//TODO: maybe this should be in java-crypto-conditions for anyone to use?
public class ConditionSerializer extends StdSerializer<Condition> {

  public ConditionSerializer() {
    super(Condition.class);
  }

  @Override
  public void serialize(Condition value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeString(value.getUri().toString());
  }
}

