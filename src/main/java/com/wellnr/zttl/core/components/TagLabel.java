package com.wellnr.zttl.core.components;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TagLabel extends HBox {

    public TagLabel(String label) {
        Label lbl = new Label(label);
        Label spacing = new Label("  ");
        Label close = new Label("x");

        HBox box = new HBox(lbl, spacing, close);
        box.getStyleClass().add("zttl--tag-label");

        this.getChildren().add(box);
        this.getStyleClass().add("zttl--tag-label--container");
    }

}
