package com.ss.editor.document;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.InputStream;

import com.ss.rlib.data.AbstractStreamDocument;
import com.ss.rlib.util.VarTable;

/**
 * The config reader.
 *
 * @author JavaSaBr
 */
public final class DocumentConfig extends AbstractStreamDocument<VarTable> {

    private static final String NODE_LIST = "list";
    private static final String NODE_SET = "set";

    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "value";

    /**
     * Instantiates a new Document config.
     *
     * @param stream the stream
     */
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
