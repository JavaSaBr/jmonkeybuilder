package com.ss.editor.ui.component.asset;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.RequestedRefreshAssetEvent;
import com.ss.rlib.fx.util.FXUtils;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * The toolbar of the {@link AssetComponent} with actions.
 *
 * @author JavaSaBr.
 */
public class AssetBarComponent extends HBox {

    private static final FxEventManager FX_EVENT_MANAGER = FxEventManager.getInstance();

    /**
     * Instantiates a new Asset bar component.
     */
    public AssetBarComponent() {

        final Button refreshAction = new Button();
        refreshAction.setGraphic(new ImageView(Icons.REFRESH_18));
        refreshAction.setOnAction(event -> FX_EVENT_MANAGER.notify(new RequestedRefreshAssetEvent()));

        FXUtils.addToPane(refreshAction, this);
    }
}
