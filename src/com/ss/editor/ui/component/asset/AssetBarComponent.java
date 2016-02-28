package com.ss.editor.ui.component.asset;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedRefreshAssetEvent;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

import static com.ss.editor.ui.css.CSSClasses.TOOLBAR_BUTTON;
import static com.ss.editor.ui.css.CSSIds.ASSET_COMPONENT_BAR_BUTTON;

/**
 * Компонент для реализации тулбара с акшенами.
 *
 * @author Ronn
 */
public class AssetBarComponent extends HBox {

    public static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    public AssetBarComponent() {
        setId(CSSIds.ASSET_COMPONENT_BAR);

        final Button refreshAction = new Button();
        refreshAction.setId(ASSET_COMPONENT_BAR_BUTTON);
        refreshAction.setGraphic(new ImageView(Icons.REFRESH_18));
        refreshAction.setOnAction(event -> FX_EVENT_MANAGER.notify(new RequestedRefreshAssetEvent()));

        FXUtils.addClassTo(refreshAction, TOOLBAR_BUTTON);
        FXUtils.addToPane(refreshAction, this);
    }
}
