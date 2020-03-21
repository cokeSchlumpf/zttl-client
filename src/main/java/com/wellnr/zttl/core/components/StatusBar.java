package com.wellnr.zttl.core.components;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.fxmisc.easybind.EasyBind;

import java.util.Optional;

public class StatusBar extends BorderPane {

    public StatusBar(IntegerProperty notesCount, ObjectProperty<Optional<Integer>> wordCount) {
        super();

        Label left = new Label("Left text");
        left.getStyleClass().add("zttl--label");
        left.textProperty().bind(EasyBind.map(notesCount, n -> n + " Notes"));

        Label rightLabel = new Label("Right text");
        rightLabel.getStyleClass().add("zttl--label");
        rightLabel.textProperty().bind(EasyBind.map(wordCount, opt -> opt.map(i -> i + " word(s)").orElse("")));

        this.setPadding(new Insets(5, 10, 5, 10));
        this.setPrefHeight(20);
        this.setMaxHeight(20);
        this.getStyleClass().add("zttl--statusbar");
        this.setLeft(left);
        this.setRight(rightLabel);
    }

}
