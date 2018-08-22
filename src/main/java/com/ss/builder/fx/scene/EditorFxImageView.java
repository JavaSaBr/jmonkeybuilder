package com.ss.builder.fx.scene;

import javafx.scene.image.ImageView;

/**
 * The resizable image view.
 *
 * @author JavaSaBr
 */
public class EditorFxImageView extends ImageView {

    @Override
    public double minHeight(double width) {
        return 64;
    }

    @Override
    public double maxHeight(double width) {
        return 1000;
    }

    @Override
    public double prefHeight(double width) {
        return minHeight(width);
    }

    @Override
    public double minWidth(double height) {
        return 0;
    }

    @Override
    public double maxWidth(double height) {
        return 10000;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        super.setFitWidth(width);
        super.setFitHeight(height);
    }
}
