package com.wellnr.zttl.common.databind;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.nio.file.Path;

public class ObjectMapperFactory {

   private ObjectMapperFactory() {

   }

   public static ObjectMapper create(boolean pretty) {
      ObjectMapper om = new ObjectMapper();

      om.getSerializationConfig()
         .getDefaultVisibilityChecker()
         .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
         .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
         .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
         .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
         .withCreatorVisibility(JsonAutoDetect.Visibility.ANY);

      om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
      om.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
      om.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
      om.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
      om.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY);

      SimpleModule module = new SimpleModule();
      module.addSerializer(Path.class, new PathSerializer());
      module.addDeserializer(Path.class, new PathDeserializer());
      om.registerModule(module);

      if (pretty) {
         om.enable(SerializationFeature.INDENT_OUTPUT);
      }

      om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

      return om;
   }

}
