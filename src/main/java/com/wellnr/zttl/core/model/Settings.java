package com.wellnr.zttl.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.nio.file.Path;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Settings {

   private static final String WORK_DIRECTORY = "work-directory";

   @JsonProperty(WORK_DIRECTORY)
   Path workDirectory;

   @JsonCreator
   public static Settings apply(
      @JsonProperty(WORK_DIRECTORY) Path workDirectory) {

      return new Settings(workDirectory);
   }

}
