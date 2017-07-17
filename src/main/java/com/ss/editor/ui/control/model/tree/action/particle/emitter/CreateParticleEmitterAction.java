package com.ss.editor.ui.control.model.tree.action.particle.emitter;

import static java.util.Objects.requireNonNull;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action for creating new {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public class CreateParticleEmitterAction extends AbstractNodeAction<ModelChangeConsumer> {

    public CreateParticleEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.EMITTER_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_DEFAULT_PARTICLE_EMITTER;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final AbstractNodeTree<?> nodeTree = getNodeTree();
        final AssetManager assetManager = EDITOR.getAssetManager();

        final Material material = new Material(assetManager,"Common/MatDefs/Misc/Particle.j3md");
        material.setTexture("Texture", assetManager.loadTexture( "Effects/Explosion/flame.png"));

        final ParticleEmitter emitter = createParticleEmitter();
        emitter.setMaterial(material);
        emitter.setImagesX(2);
        emitter.setImagesY(2); // 2x2 texture animation
        emitter.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f));   // red
        emitter.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        emitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        emitter.setStartSize(1.5f);
        emitter.setEndSize(0.1f);
        emitter.setGravity(0, 0, 0);
        emitter.setLowLife(1f);
        emitter.setHighLife(3f);
        emitter.getParticleInfluencer().setVelocityVariation(0.3f);
        emitter.setEnabled(true);

        final ModelNode<?> modelNode = getNode();
        final Node parent = (Node) modelNode.getElement();

        final ChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new AddChildOperation(emitter, parent));
    }

    @NotNull
    protected ParticleEmitter createParticleEmitter() {
        return new ParticleEmitter("Default Emitter", ParticleMesh.Type.Triangle, 30);
    }
}
