package com.wellnr.zttl.core.components;

import com.wellnr.zttl.adapters.InMemoryNotesRepository;
import com.wellnr.zttl.common.SearchReplaceString;
import com.wellnr.zttl.common.StringMatch;
import com.wellnr.zttl.common.StringMatches;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputHandler;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownEditor extends AnchorPane {

   private static final String HEADLINE_PATTERN = "^#[^\n]*|\n#[^\n]*";
   private static final String BULLET_LIST_ITEM_PATTERN = "^[\\s]*\\*\\s|\n[\\s]*\\*\\s";
   private static final String DASH_LIST_ITEM_PATTERN = "^[\\s]*-\\s|\n[\\s]*\\*\\s";
   private static final String LINK_PATTERN = "\\[[^\n\\[\\]]*]";
   private static final String CODE_PATTERN = "```[a-z]*\\n[\\s\\S]*?\\n```|`[^\\n^`]+?`"; //""\n```[(?!.*(```))]*\n```|^```[(?!```)]*\n```|`[^\n^`]+`";
   private static final String URL_PATTERN = "(https?|ftp|file|note)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]*[-A-Za-z0-9+&@#/%=~_.|]";
   private static final String RELATIVE_PATH_PATTERN = "[.]{1,2}/[-A-Za-z0-9+&@#/%=~_.|]*";

   private static final Pattern PATTERN = Pattern.compile(
      "(?<HEADLINE>" + HEADLINE_PATTERN + ")"
         + "|(?<BULLETLISTITEM>" + BULLET_LIST_ITEM_PATTERN + ")"
         + "|(?<DASHLISTITEM>" + DASH_LIST_ITEM_PATTERN + ")"
         + "|(?<CODE>" + CODE_PATTERN + ")"
         + "|(?<URL>" + URL_PATTERN + ")"
         + "|(?<RELATIVEPATH>" + RELATIVE_PATH_PATTERN + ")"
         + "|(?<LINK>" + LINK_PATTERN + ")", Pattern.DOTALL);

   public final StringProperty textProperty;

   public final BooleanProperty replaceActive;

   public final BooleanProperty searchActive;

   private final SearchReplaceBox searchReplaceBox;

   private final ObjectProperty<StringMatches> matches;

   private final CodeArea codeArea;

   private final AutoCompletionCodeAreaBind autoCompletionCodeAreaBind;


   public MarkdownEditor() {
      final Pattern whiteSpace = Pattern.compile("^\\s*");

      this.getStyleClass().add("zttl--markdown-editor");

      this.replaceActive = new SimpleBooleanProperty();
      this.searchActive = new SimpleBooleanProperty();

      this.matches = new SimpleObjectProperty<>(StringMatches.EMPTY);
      this.matches.addListener((observable, oldValue, newValue) -> this.computeHighlighting());

      this.codeArea = new CodeArea();
      this.autoCompletionCodeAreaBind = new AutoCompletionCodeAreaBind(this.codeArea, new AutoCompletionProvider());
      this.codeArea.getStyleClass().add("zttl--markdown-editor--text-area");
      this.codeArea.setWrapText(true);
      this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));
      this.codeArea.setOnMouseClicked(event -> {
         autoCompletionCodeAreaBind.hidePopup();


         if (event.isMetaDown()) {
            /*
             * Open Link if linked is clicked
             */
            this.codeArea.getCaretPosition();
            var pos = this.codeArea.getCaretPosition();
            var matcher = PATTERN.matcher(this.codeArea.getText());

            while (matcher.find()) {
               if (matcher.group("URL") != null && matcher.start() <= pos && matcher.end() >= pos) {
                  System.out.println(matcher.group());
               }
            }
         }
      });

      this.textProperty = new SimpleStringProperty();
      this.textProperty.addListener((observable, oldValue, newValue) -> {
         if (!newValue.equals(this.codeArea.getText())) {
            this.codeArea.replaceText(newValue);
         }
      });

      this.searchReplaceBox = new SearchReplaceBox();

      /*
       * Insert tab indent like previous line.
       */
      InputMap<KeyEvent> im = InputMap
         .consume(
            EventPattern.keyPressed(KeyCode.TAB),
            e -> this.codeArea.replaceSelection("   ")
         );

      InputMap<KeyEvent> search = InputMap
         .consume(
            EventPattern.keyPressed(new KeyCodeCombination(KeyCode.F, KeyCombination.META_DOWN)),
            e -> {
               this.searchActive.setValue(true);

               if (this.codeArea.getSelectedText().length() > 0) {
                  this.searchReplaceBox.searchTextProperty.set(this.codeArea.getSelectedText());
               }

               Platform.runLater(this.searchReplaceBox::requestFocus);
            });

      InputMap<KeyEvent> replace = InputMap
         .consume(
            EventPattern.keyPressed(new KeyCodeCombination(KeyCode.R, KeyCombination.META_DOWN)),
            e -> {
               if (this.searchActive.get()) {
                  this.replaceActive.setValue(true);
                  Platform.runLater(() -> this.searchReplaceBox.requestFocus(true));
               } else {
                  if (this.codeArea.getSelectedText().length() > 0) {
                     this.searchReplaceBox.searchTextProperty.set(this.codeArea.getSelectedText());
                  }

                  this.searchActive.setValue(true);
                  this.replaceActive.setValue(true);
                  Platform.runLater(this.searchReplaceBox::requestFocus);
               }
            });

      InputMap<KeyEvent> closeSearch = InputMap
         .consume(
            EventPattern.keyPressed(KeyCode.ESCAPE),
            e -> {
               if (this.searchActive.get()) {
                  this.searchActive.setValue(false);
                  this.replaceActive.setValue(false);
                  this.searchReplaceBox.clear();
                  e.consume();
               }
            });

      InputMap<KeyEvent> tabs = InputMap
         .process(
            EventPattern.keyPressed(KeyCode.ENTER),
            e -> {
               Platform.runLater(() -> {
                  int caretPosition = this.codeArea.getCaretPosition();
                  int currentParagraph = this.codeArea.getCurrentParagraph();

                  Matcher m0 = whiteSpace.matcher(this.codeArea.getParagraph(currentParagraph - 1).getSegments().get(0));
                  if (m0.find()) Platform.runLater(() -> this.codeArea.insertText(caretPosition, m0.group()));
               });

               return InputHandler.Result.PROCEED;
            });

      Nodes.addInputMap(this.codeArea, closeSearch);
      Nodes.addInputMap(this.codeArea, im);
      Nodes.addInputMap(this.codeArea, search);
      Nodes.addInputMap(this.codeArea, replace);
      Nodes.addInputMap(this.codeArea, tabs);

      this
         .codeArea
         .multiPlainChanges()
         .successionEnds(Duration.ofMillis(500))
         .subscribe(ignore -> {
            textProperty.set(codeArea.getText());

            if (this.searchActive.get()) {
               this.search();
            } else {
               this.computeHighlighting();
            }
         });

      this.getChildren().add(this.codeArea);

      this.searchReplaceBox.setMaxWidth(200);
      this.searchReplaceBox.onClose().addHandler(() -> {
         this.replaceActive.setValue(false);
         this.searchActive.setValue(false);
         this.searchReplaceBox.clear();
      });

      this.searchReplaceBox.onSearch().addHandler(() -> {
         this.search();
         this.matches
            .get()
            .findNext(this.codeArea.getCaretPosition())
            .ifPresent(match -> {
               this.codeArea.selectRange(match.getStart(), match.getEnd());
               this.searchReplaceBox.currentMatchProperty.set(match.getPos());
            });
      });

      this.searchReplaceBox.onGotoNext().addHandler(() -> this
         .matches
         .get()
         .findNext(this.codeArea.getCaretPosition() + 1)
         .ifPresent(match -> {
            this.codeArea.selectRange(match.getStart(), match.getEnd());
            this.searchReplaceBox.currentMatchProperty.set(match.getPos());
         }));

      this.searchReplaceBox.onGotoPrev().addHandler(() -> this
         .matches
         .get()
         .findPrevious(this.codeArea.getCaretPosition() - this.codeArea.getSelection().getLength())
         .ifPresent(match -> {
            this.codeArea.selectRange(match.getStart(), match.getEnd());
            this.searchReplaceBox.currentMatchProperty.set(match.getPos());
         }));

      this.searchReplaceBox.onReplace().addHandler(() -> this
         .matches
         .get()
         .get(this.searchReplaceBox.currentMatchProperty.get())
         .ifPresent(m -> {
            this.codeArea.selectRange(m.getStart(), m.getEnd());
            this.codeArea.replaceSelection(this.searchReplaceBox.replaceTextProperty.get());
            this.search();
         }));

      this.searchReplaceBox.onReplaceAll().addHandler(() -> {
         this
            .matches
            .get()
            .toList()
            .stream()
            .sorted(Comparator.comparingInt(StringMatch::getStart).reversed())
            .forEach(m -> {
               this.codeArea.selectRange(m.getStart(), m.getEnd());
               this.codeArea.replaceSelection(this.searchReplaceBox.replaceTextProperty.get());
            });

         this.search();
      });

      this.searchReplaceBox.onClearSearch().addHandler(() -> {
         this.matches.setValue(StringMatches.EMPTY);
         this.searchReplaceBox.matchedCasesProperty.set(0);
      });

      AnchorPane.setTopAnchor(this.codeArea, 0.0);
      AnchorPane.setBottomAnchor(this.codeArea, 0.0);
      AnchorPane.setLeftAnchor(this.codeArea, 0.0);
      AnchorPane.setRightAnchor(this.codeArea, 0.0);

      this.searchActive.addListener((observable, oldValue, newValue) ->

      {
         if (newValue) {
            this.getChildren().add(this.searchReplaceBox);
            AnchorPane.setLeftAnchor(this.searchReplaceBox, 0.0);
            AnchorPane.setRightAnchor(this.searchReplaceBox, 0.0);
            AnchorPane.setTopAnchor(this.searchReplaceBox, 0.0);
            AnchorPane.setTopAnchor(this.codeArea, 50.0);
         } else {
            this.getChildren().remove(this.searchReplaceBox);
            this.matches.setValue(StringMatches.EMPTY);
            this.searchReplaceBox.searchTextProperty.set("");
            AnchorPane.setTopAnchor(this.codeArea, 0.0);
            Platform.runLater(this.codeArea::requestFocus);
         }
      });

      this.replaceActive.bindBidirectional(this.searchReplaceBox.replaceVisibleProperty);
   }

   private void computeHighlighting() {
      Matcher matcher = PATTERN.matcher(codeArea.getText());
      int lastKwEnd = 0;

      StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
      spansBuilder.add(Collections.emptyList(), 0);

      while (matcher.find()) {
         spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);

         if (matcher.group("HEADLINE") != null) {
            spansBuilder.add(Collections.singleton("zttl--markdown-editor--headline"), matcher.end() - matcher.start());
         } else if (matcher.group("BULLETLISTITEM") != null || matcher.group("DASHLISTITEM") != null) {
            spansBuilder.add(Collections.singleton("zttl--markdown-editor--list-item"), matcher.end() - matcher.start());
         } else if (matcher.group("CODE") != null) {
            spansBuilder.add(Collections.singleton("zttl--markdown-editor--code"), matcher.end() - matcher.start());
         } else if (matcher.group("LINK") != null) {
            spansBuilder.add(Collections.emptyList(), 1);
            spansBuilder.add(Collections.singleton("zttl--markdown-editor--alt"), matcher.end() - matcher.start() - 2);
            spansBuilder.add(Collections.emptyList(), 1);
         } else if (matcher.group("URL") != null || matcher.group("RELATIVEPATH") != null) {
            spansBuilder.add(Collections.singleton("zttl--markdown-editor--link"), matcher.end() - matcher.start());
         }

         lastKwEnd = matcher.end();
      }

      StyleSpansBuilder<Collection<String>> searchHighlightBuilder = new StyleSpansBuilder<>();
      searchHighlightBuilder.add(Collections.emptyList(), 0);
      lastKwEnd = 0;

      for (StringMatch match : this.matches.get().toList()) {
         searchHighlightBuilder.add(Collections.emptyList(), match.getStart() - lastKwEnd);
         searchHighlightBuilder.add(Collections.singleton("zttl--markdown-editor--search-highlight"), match.getMatch().length());
         lastKwEnd = match.getEnd();
      }

      StyleSpans<Collection<String>> styles = spansBuilder
         .create()
         .overlay(searchHighlightBuilder.create(), (style, search) -> {
            List<String> combined = new ArrayList<>();
            combined.addAll(style);
            combined.addAll(search);
            return combined;
         });


      codeArea.setStyleSpans(0, styles);
   }

   private void search() {
      var matches = new SearchReplaceString(this.codeArea.getText())
         .findString(
            this.searchReplaceBox.searchTextProperty.get(),
            this.searchReplaceBox.matchCaseProperty.get());

      this.matches.setValue(matches);
      this.searchReplaceBox.matchedCasesProperty.set(matches.getCount());
   }

   public void setAutoCompletionProvider(AutoCompletionProvider provider) {
      this.autoCompletionCodeAreaBind.setAutoCompletionProvider(provider);
   }

   public void setText(String value) {
      this.textProperty.set(value);
   }

}
