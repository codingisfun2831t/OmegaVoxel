package com.codingisfun2831t.omegavoxel.entity;

import com.codingisfun2831t.omegavoxel.level.Block;
import com.codingisfun2831t.omegavoxel.level.Level;
import com.codingisfun2831t.omegavoxel.model.ModelHuman;
import com.codingisfun2831t.omegavoxel.rendering.Renderer;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the player, with all the info.
 */
public class EntityPlayer extends EntityLiving {
    private static final Logger logger = LoggerFactory.getLogger(EntityPlayer.class);

    public Block[] hotbar = new Block[] {
            Block.DIRT, Block.GRASS, Block.STONE, Block.COBBLESTONE,
            Block.LOG, Block.PLANKS, Block.SAND, Block.GRAVEL, Block.BEDROCK
    };
    public int selectedSlot = 0;
    private float walkTime;

    public EntityPlayer(Level level) {
        super(level);
        this.model = new ModelHuman();

        w = 0.6F;
        h = 1.8F;
        eyeHeight = 1.62f;
    }

    public void resetPos() {
        Level level = getLevel();
        float x = (float)Math.random() * (float) level.getWidth();
        float y = (float)(level.getDepth());
        float z = (float)Math.random() * (float) level.getHeight();
        this.setPos(x, y, z);

        logger.info("Position reset to: XYZ {} / {} / {}", x, y, z);
    }

    /**
     * Switch to a block in the inventory, adding it to the current slot if not currently in hotbar.
     */
    public void switchToBlock(Block b) {
        for (int i = 0; i < hotbar.length; i++) {
            if (hotbar[i] == b) {
                hotbar[i] = hotbar[selectedSlot];
                hotbar[selectedSlot] = b;

                return;
            }
        }

        hotbar[selectedSlot] = b;
    }

    /**
     * Turn using X and Y mouse delta values.
     */
    public void turnMouse(float xo, float yo) {
        float sensitivity = 0.15F;
        turn(xo * sensitivity, yo * sensitivity);
    }

    private boolean isKeyDown(long window, int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }

    public void input(long window) {

        xa = 0.0f;
        ya = 0.0f;

        if(isKeyDown(window, GLFW.GLFW_KEY_UP) || isKeyDown(window, GLFW.GLFW_KEY_W)) {
            --ya;
        }

        if(isKeyDown(window, GLFW.GLFW_KEY_DOWN) || isKeyDown(window, GLFW.GLFW_KEY_S)) {
            ++ya;
        }

        if(isKeyDown(window, GLFW.GLFW_KEY_LEFT) || isKeyDown(window, GLFW.GLFW_KEY_A)) {
            ++xa;
        }

        if(isKeyDown(window, GLFW.GLFW_KEY_RIGHT) || isKeyDown(window, GLFW.GLFW_KEY_D)) {
            --xa;
        }

        // jump
        if(isKeyDown(window, GLFW.GLFW_KEY_SPACE) && this.onGround) {
            this.yd = 0.12F;
        }
    }

    @Override
    public void tickMotion() {
        super.tickMotion();

        float speed = (float) Math.sqrt(xd * xd + zd * zd);
        walkTime += speed * 2f;
    }

    @Override
    public void tick() { // stop deleting
    }

    public void render(Renderer r, float t, boolean interpolated) {
        if (model instanceof ModelHuman human) {
            float speed = (float) Math.sqrt(xd * xd + zd * zd);
            float swing = (float) Math.sin(walkTime);

            swing *= Math.min(speed * 16f, 2.0F);

            model.yawRotation = yaw;
            human.setSwing(swing);
            human.setPitch(-pitch);

            super.render(r, t, interpolated);
        }

    }
}