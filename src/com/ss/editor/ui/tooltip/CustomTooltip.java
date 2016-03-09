package com.ss.editor.ui.tooltip;

import com.ss.editor.ui.css.CSSIds;

import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * Базовая реализация для костомного тултипа.
 *
 * @author Ronn
 */
public class CustomTooltip extends Tooltip {

    /**
     * Рутовый контейнер описания.
     */
    private final VBox root;

    public CustomTooltip() {
        this.root = new VBox();
        this.root.setId(CSSIds.IMAGE_CHANNEL_PREVIEW);

        final Scene scene = getScene();
        scene.setRoot(root);

        createContent(root);
    }

    /**
     * Создание контента для кастомного тултипа.
     */
    protected void createContent(final VBox root) {

    }

    @Override
    public void show(final Window ownerWindow, final double anchorX, final double anchorY) {
        super.show(ownerWindow, anchorX + 6, anchorY + 3);
    }
}
