package com.ss.editor.util;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.ss.editor.annotation.JmeThread;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.pools.Reusable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * The reusable implementation of collision result.
 *
 * @author JavaSaBr
 */
public class ReusableCollisionResults extends CollisionResults implements Reusable {

    /**
     * The list of sorted collision results.
     */
    @NotNull
    private final Array<CollisionResult> collisions;

    /**
     * The list collision results.
     */
    @NotNull
    private final Array<CollisionResult> original;

    public ReusableCollisionResults() {
        this.collisions = ArrayFactory.newSortedArray(CollisionResult.class);
        this.original = ArrayFactory.newArray(CollisionResult.class);
    }

    @Override
    @JmeThread
    public void addCollision(@NotNull final CollisionResult result) {
        collisions.add(result);
        original.add(result);
    }

    @Override
    public Iterator<CollisionResult> iterator() {
        return collisions.iterator();
    }

    @Override
    @JmeThread
    public CollisionResult getCollisionDirect(final int index) {
        return original.get(index);
    }

    @Override
    @JmeThread
    public int size() {
        return original.size();
    }

    @Override
    @JmeThread
    public @Nullable CollisionResult getClosestCollision() {
        return collisions.first();
    }

    @Override
    @JmeThread
    public void free() {
        collisions.clear();
        original.clear();
    }
}
