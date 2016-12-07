package com.ss.editor.ui.component.asset;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedRefreshAssetEvent;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * The toolbar of the {@link AssetComponent} with actions.
 *
 * @author JavaSaBr.
 */
public class AssetBarComponent extends HBox {

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    public AssetBarComponent() {
        setId(CSSIds.ASSET_COMPONENT_BAR);

        final Button refreshAction = new Button();
        refreshAction.setId(CSSIds.ASSET_COMPONENT_BAR_BUTTON);
        refreshAction.setGraphic(new ImageView(Icons.REFRESH_18));
        refreshAction.setOnAction(event -> FX_EVENT_MANAGER.notify(new RequestedRefreshAssetEvent()));

        FXUtils.addClassTo(refreshAction, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addToPane(refreshAction, this);
    }
}
