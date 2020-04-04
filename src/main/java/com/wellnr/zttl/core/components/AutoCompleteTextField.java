package com.wellnr.zttl.core.components;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class AutoCompleteTextField extends StackPane {


    private final SortedSet<String> entries;

    private List<String> results = new LinkedList<>();

    private TextField actual;

    private int current = 0;

    public AutoCompleteTextField(String promptText) {
        super();
        entries = new TreeSet<>();

        TextField suggestion = new TextField();
        suggestion.getStyleClass().add("zttl--autocomplete-text-field--suggestion");

        actual = new TextField();
        actual.setPromptText(promptText);
        actual.getStyleClass().add("zttl--autocomplete-text-field--actual");

        actual.textProperty().addListener((observableValue, s, s2) -> {
            if (actual.getText().length() == 0) {
                suggestion.setPromptText("");
            } else {
                results = new LinkedList<>();
                results.addAll(entries.subSet(
                        actual.getText() + Character.MIN_VALUE,
                        actual.getText() + Character.MAX_VALUE));

                if (results.size() > 0) {
                    suggestion.setPromptText(results.get(0));
                    current = 0;
                } else {
                    suggestion.setPromptText("");
                }
            }
        });

        actual.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !results.isEmpty()) {
                actual.setText(results.get(current));
            } else if (event.getCode() == KeyCode.ESCAPE) {
                current = 0;
                results = new LinkedList<>();
                suggestion.setPromptText("");
            } else if (event.getCode() == KeyCode.DOWN && results.size() - 1 > current) {
                current += 1;
                suggestion.setPromptText(results.get(current));
            } else if (event.getCode() == KeyCode.UP && results.size() > 0 && current > 0) {
                current -= 1;
                suggestion.setPromptText(results.get(current));
            }
        });

        focusedProperty().addListener((observableValue, aBoolean, aBoolean2) -> suggestion.setPromptText(""));

        this.getChildren().addAll(suggestion, actual);
    }

    public ReadOnlyBooleanProperty getFocusedProperty() {
        return this.actual.focusedProperty();
    }

    public SortedSet<String> getEntries() { return entries; }

    public String getText() {
        return actual.getText();
    }

    public void setText(String value) {
        actual.setText(value);
    }

}
