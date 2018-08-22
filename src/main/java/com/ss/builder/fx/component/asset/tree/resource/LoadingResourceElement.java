package com.ss.builder.ui.component.asset.tree.resource;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

/**
 * The implementing of resource element to show loading process.
 *
 * @author JavaSaBr
 */
public class LoadingResourceElement extends ResourceElement {

    @NotNull
    private static final LoadingResourceElement INSTANCE = new LoadingResourceElement();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static LoadingResourceElement getInstance() {
        return INSTANCE;
    }

    private LoadingResourceElement() {
        super(Paths.get("./"));
    }

    @Override
    public String toString() {
        return "LoadingResourceElement";
    }
}
