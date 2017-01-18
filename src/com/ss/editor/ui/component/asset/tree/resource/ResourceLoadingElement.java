package com.ss.editor.ui.component.asset.tree.resource;

/**
 * The implementing of resource element to show loading process.
 *
 * @author JavaSaBr
 */
public class ResourceLoadingElement extends ResourceElement {

    private static final ResourceLoadingElement INSTANCE = new ResourceLoadingElement();

    public static ResourceLoadingElement getInstance() {
        return INSTANCE;
    }

    public ResourceLoadingElement() {
        super(null);
    }
}
