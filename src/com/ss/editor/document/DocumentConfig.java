package com.ss.editor.document;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.InputStream;

import rlib.data.AbstractStreamDocument;
import rlib.util.VarTable;

/**
 * The config reader.
 *
 * @author JavaSaBr
 */
public final class DocumentConfig extends AbstractStreamDocument<VarTable> {

    public static final String NODE_LIST = "list";
    public static final String NODE_SET = "set";

    public static final String ATTR_NAME = "name";
    public static final String ATTR_VALUE = "value";

    public DocumentConfig(@NotNull final InputStream stream) {
        super(stream);
    }

    @Override
    protected VarTable create() {
        return VarTable.newInstance();
    }

    @Override
    protected void parse(@NotNull final Document document) {
        for (Node child = document.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (NODE_LIST.equals(child.getNodeName())) {
                result.parse(child, NODE_SET, ATTR_NAME, ATTR_VALUE);
            }
        }
    }
}
