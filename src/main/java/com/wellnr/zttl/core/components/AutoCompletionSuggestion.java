package com.wellnr.zttl.core.components;

import lombok.Value;

@Value
public class AutoCompletionSuggestion {

   String label;

   String replaceText;

   public String toString() {
      return label;
   }

}
