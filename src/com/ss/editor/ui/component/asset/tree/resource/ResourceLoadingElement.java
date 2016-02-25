package com.ss.editor.ui.component.asset.tree.resource;

/**
 * Реализация элемента для отображения загрузки.
 *
 * @author Ronn.
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
