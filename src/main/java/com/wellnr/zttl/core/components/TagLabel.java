package com.wellnr.zttl.core.components;

import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class TagLabel extends HBox {

    private final String label;

    public TagLabel(String label, Consumer<String> onRemove) {
        this.label = label;

        Label lbl = new Label(label);
        Label spacing = new Label(" ");

        Label close = new Label("x");
        close.getStyleClass().add("zttl--tag-label--close");
        close.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                onRemove.accept(label);
            }
        });

        HBox box = new HBox(lbl, spacing, close);
        box.getStyleClass().add("zttl--tag-label");

        this.getChildren().add(box);
        this.getStyleClass().add("zttl--tag-label--container");
    }

    public String getLabel() {
        return label;
    }
}
