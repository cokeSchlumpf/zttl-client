package com.wellnr.zttl.core.components;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputHandler;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import org.fxmisc.wellbehaved.event.template.InputMapTemplate;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownEditor extends BorderPane {

   private static final String HEADLINE_PATTERN = "^#[^\n]*|\n#[^\n]*";
   private static final String BULLET_LIST_ITEM_PATTERN = "^[\\s]*\\*\\s|\n[\\s]*\\*\\s";
   private static final String DASH_LIST_ITEM_PATTERN = "^[\\s]*\\*\\s|\n[\\s]*\\*\\s";
   private static final String LINK_PATTERN = "\\[[^\n]*]";
   private static final String CODE_PATTERN = "\n```.*\n```|^```.*\n```|`[^\n^`]+`";
   private static final String URL_PATTERN = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]*[-A-Za-z0-9+&@#/%=~_\\.|]";
   private static final String RELATIVE_PATH_PATTERN = "[\\.]{1,2}/[-A-Za-z0-9+&@#/%=~_\\.|]*";

   private static final Pattern PATTERN = Pattern.compile(
      "(?<HEADLINE>" + HEADLINE_PATTERN + ")"
         + "|(?<BULLETLISTITEM>" + BULLET_LIST_ITEM_PATTERN + ")"
         + "|(?<DASHLISTITEM>" + DASH_LIST_ITEM_PATTERN + ")"
         + "|(?<CODE>" + CODE_PATTERN + ")"
         + "|(?<URL>" + URL_PATTERN + ")"
         + "|(?<RELATIVEPATH>" + RELATIVE_PATH_PATTERN + ")"
         + "|(?<LINK>" + LINK_PATTERN + ")", Pattern.DOTALL);

   public final StringProperty textProperty;

   private final CodeArea codeArea;

   public MarkdownEditor() {
      final Pattern whiteSpace = Pattern.compile("^\\s*");

      this.getStyleClass().add("zttl--markdown-editor");

      this.codeArea = new CodeArea();
      this.codeArea.getStyleClass().add("zttl--markdown-editor--text-area");
      this.codeArea.setWrapText(true);
      // this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));

      this.textProperty = new SimpleStringProperty();
      this.textProperty.addListener((observable, oldValue, newValue) -> {
         if (!newValue.equals(this.codeArea.getText())) {
            this.codeArea.replaceText(newValue);
         }
      });

      /*
       * Insert tab indent like previous line.
       */
      InputMap<KeyEvent> im = InputMap
         .consume(
            EventPattern.keyPressed(KeyCode.TAB),
            e -> this.codeArea.replaceSelection("   ")
         );

      InputMap<KeyEvent> tabs = InputMap
         .process(
            EventPattern.keyPressed(KeyCode.ENTER),
            e -> {
               Platform.runLater(() -> {
                  int caretPosition = this.codeArea.getCaretPosition();
                  int currentParagraph = this.codeArea.getCurrentParagraph();

                  String prevParagraph = this.codeArea.getParagraph(currentParagraph - 1).getText();

                  System.out.println("'" + prevParagraph + "'");

                  Matcher m0 = whiteSpace.matcher(this.codeArea.getParagraph(currentParagraph - 1).getSegments().get(0));
                  if (m0.find()) Platform.runLater(() -> this.codeArea.insertText(caretPosition, m0.group()));
               });

               return InputHandler.Result.PROCEED;
            });

      Nodes.addInputMap(this.codeArea, im);
      Nodes.addInputMap(this.codeArea, tabs);

      this
         .codeArea
         .multiPlainChanges()
         .successionEnds(Duration.ofMillis(500))
         .subscribe(ignore -> {
            computeHighlighting();
            textProperty.set(codeArea.getText());
         });

      this.setCenter(this.codeArea);
   }

   private void computeHighlighting() {
      Matcher matcher = PATTERN.matcher(codeArea.getText());
      int lastKwEnd = 0;

      StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

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

      codeArea.setStyleSpans(0, spansBuilder.create());
   }

   public String getText() {
      return textProperty.get();
   }

   public void setText(String value) {
      this.textProperty.set(value);
   }

}
