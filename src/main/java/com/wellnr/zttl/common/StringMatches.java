package com.wellnr.zttl.common;

import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class StringMatches {

   public static StringMatches EMPTY = new StringMatches(List.of());

   private final List<StringMatch> matches;

   public int getCount() {
      return matches.size();
   }

   public Optional<StringMatch> findNext(int start) {
      return matches
         .stream()
         .filter(m -> m.getStart() >= start)
         .min(Comparator.comparingInt(m -> Math.abs(m.getStart() - start)))
         .or(() -> matches.stream().findFirst());
   }

   public Optional<StringMatch> findPrevious(int start) {
      return matches
         .stream()
         .filter(m -> m.getStart() < start)
         .min(Comparator.comparingInt(m -> Math.abs(m.getStart() - start)))
         .or(() -> matches.stream().reduce((first, second) -> second));
   }

   public Optional<StringMatch> get(int pos) {
      return matches
         .stream()
         .filter(m -> m.getPos() == pos)
         .findFirst();
   }

   public boolean noMatches() {
      return matches.isEmpty();
   }

   public List<StringMatch> toList() {
      return List.copyOf(matches);
   }

}
