package com.ss.builder.fx.component.painting;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;
import com.ss.rlib.common.util.StringUtils;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of {@link ListCell} to show {@link PaintingComponent}.
 *
 * @author JavaSaBr
 */
public class PaintingComponentListCell extends ListCell<PaintingComponent> {

    /**
     * The image view to show icons.
     */
    @NotNull
    private final ImageView imageView;

    public PaintingComponentListCell(@Nullable ListView<PaintingComponent> listView) {
        this.imageView = new ImageView();
        setGraphic(imageView);
    }

    @Override
    @FxThread
    protected void updateItem(@Nullable PaintingComponent item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            imageView.setImage(null);
            setText(StringUtils.EMPTY);
            return;
        }

        imageView.setImage(item.getIcon());
        setText(item.getName());
    }
}
