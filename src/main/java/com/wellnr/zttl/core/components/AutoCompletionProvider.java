package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.ports.NotesRepository;
import javafx.scene.control.IndexRange;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AutoCompletionProvider {

   private static final Pattern REFERENCE_NAME_PATTERN = Pattern.compile("\\[(?<LABEL>[^\n\\]]*)$");

   private static final Pattern LINK_NAME_PATTERN = Pattern.compile("]\\([-A-Za-z0-9+&@#/%?=~_|!:,.;]*\\)");

   public final BiFunction<String, Integer, Collection<AutoCompletionSuggestion>> suggestionProvider;

   public final BiFunction<String, Integer, IndexRange> replaceTextProvider;

   public AutoCompletionProvider() {
      this.replaceTextProvider = (text, caret) -> IndexRange.normalize(0, 0);
      this.suggestionProvider = (text, caret) -> List.of();
   }

   public AutoCompletionProvider(NotesRepository notes) {
      suggestionProvider = (text, caret) -> {
         Matcher referenceMatcher = REFERENCE_NAME_PATTERN.matcher(getStringBeforeCaret(text, caret));

         if (referenceMatcher.matches()) {
            String label = referenceMatcher.group("LABEL");

            return notes
               .getNotes()
               .stream()
               .filter(note -> note.getTitle().contains(label))
               .map(note -> new AutoCompletionSuggestion(
                  note.getTitle(),
                  String.format("[%s](note://%s)", note.getTitle(), note.getId())))
               .collect(Collectors.toList());
         } else {
            return List.of();
         }
      };

      replaceTextProvider = (text, caret) -> {
         Matcher referenceMatcher = REFERENCE_NAME_PATTERN.matcher(getStringBeforeCaret(text, caret));
         Matcher linkMatcher = LINK_NAME_PATTERN.matcher(getStringAfterCaret(text, caret));

         int start = caret;
         int end = caret;

         if (referenceMatcher.matches()) {
            start = start - referenceMatcher.group().length();
         }

         if (linkMatcher.matches()) {
            end = end + linkMatcher.group().length();
         }

         return IndexRange.normalize(start, end);
      };
   }

   private static String getStringBeforeCaret(String text, int caret) {
      String chars = text.substring(Math.max(0, caret - 420), caret);
      String[] words = chars.split("\\s");
      return words.length > 0 && !chars.endsWith(" ") ? words[words.length - 1] : "";
   }

   private static String getStringAfterCaret(String text, int caret) {
      String chars = text.substring(caret, Math.min(caret + 420, text.length()));
      String[] words = chars.split("\\s");
      return words.length > 0 && !chars.startsWith(" ") ? words[0] : "";
   }

}
