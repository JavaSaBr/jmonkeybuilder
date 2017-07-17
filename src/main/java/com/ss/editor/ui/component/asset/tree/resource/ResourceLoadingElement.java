package com.ss.editor.ui.component.asset.tree.resource;

import java.nio.file.Paths;

/**
 * The implementing of resource element to show loading process.
 *
 * @author JavaSaBr
 */
public class ResourceLoadingElement extends ResourceElement {

    private static final ResourceLoadingElement INSTANCE = new ResourceLoadingElement();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ResourceLoadingElement getInstance() {
        return INSTANCE;
    }

    private ResourceLoadingElement() {
        super(Paths.get("./"));
    }
}
