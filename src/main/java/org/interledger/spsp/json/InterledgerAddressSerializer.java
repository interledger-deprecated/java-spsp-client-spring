package org.interledger.spsp.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.interledger.ilp.InterledgerAddress;

import java.io.IOException;

@SuppressWarnings("serial")
//TODO: maybe this should be in java-ilp-core?
public class InterledgerAddressSerializer extends StdSerializer<InterledgerAddress> {

  public InterledgerAddressSerializer() {
    super(InterledgerAddress.class);
  }

  @Override
  public void serialize(InterledgerAddress value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeString(value.toString());
  }  
}

