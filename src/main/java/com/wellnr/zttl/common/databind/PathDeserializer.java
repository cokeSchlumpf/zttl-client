package com.wellnr.zttl.common.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathDeserializer extends StdDeserializer<Path> {

   public PathDeserializer() {
      super(Path.class);
   }


   @Override
   public Path deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return Paths.get(p.readValueAs(String.class));
   }

}
