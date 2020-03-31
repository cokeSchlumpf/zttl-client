package com.wellnr.zttl.common;

import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class SearchReplaceString {

   private final String value;

   public StringMatches findString(String search, boolean matchCase) {
      List<StringMatch> matches = new LinkedList<>();

      String searchIn = matchCase ? value : value.toLowerCase();
      String searchFor = matchCase ? search : search.toLowerCase();

      int pos = searchIn.indexOf(searchFor);
      int listPos = 0;

      while (pos > -1) {
         StringMatch m = new StringMatch(listPos++, pos, pos + searchFor.length(), search);
         matches.add(m);
         pos = searchIn.indexOf(searchFor, pos + searchFor.length());
      }

      return new StringMatches(List.copyOf(matches));
   }

}
