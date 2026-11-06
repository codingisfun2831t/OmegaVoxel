package com.codingisfun2831t.omegavoxel;

import com.codingisfun2831t.omegavoxel.assets.Texture;
import com.codingisfun2831t.omegavoxel.level.Block;
import com.codingisfun2831t.omegavoxel.level.Level;
import com.codingisfun2831t.omegavoxel.model.ModelHuman;
import com.codingisfun2831t.omegavoxel.rendering.Renderer;
import net.querz.nbt.tag.CompoundTag;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Player {
    private static final Logger logger = LoggerFactory.getLogger(Player.class);
    public float xo;
    public float yo;
    public float zo;
    public float x;
    public float y;
    public float z;
    public float xd;
    public float yd;
    public float zd;
    public float yaw;
    public float pitch;
    public AABB bb;
    public boolean onGround = false;
    public Block[] hotbar = new Block[] {
            Block.DIRT, Block.GRASS, Block.STONE, Block.COBBLESTONE,
            Block.LOG, Block.PLANKS, Block.SAND, Block.GRAVEL, Block.BEDROCK
    };
    public int selectedSlot = 0;
    private ModelHuman model = new ModelHuman();
    private float walkTime;

    public void resetPos(Level level) {
        float x = (float)Math.random() * (float) level.getWidth();
        float y = (float)(level.getDepth());
        float z = (float)Math.random() * (float) level.getHeight();
        this.setPos(x, y, z);

        logger.info("Position reset to: XYZ {} / {} / {}", x, y, z);
    }

    public static final float w = 0.6F;
    public static final float h = 1.8F;
    public static final float eyeHeight = 1.62f;

    public void setPos(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.bb = new AABB(x - w / 2, y, z - w / 2, x + w / 2, y + h, z + w / 2);
    }

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

    public void turn(float xo, float yo) {
        this.yaw = (float)((double)this.yaw + (double)xo * 0.15D);
        this.pitch = (float)((double)this.pitch + (double)yo * 0.15D);
        if(this.pitch < -90.0F) {
            this.pitch = -90.0F;
        }

        if(this.pitch > 90.0F) {
            this.pitch = 90.0F;
        }

        clampYaw();
    }

    public void clampYaw() {
        yaw = (yaw % 360 + 360) % 360;

        if (yaw > 180) {
            yaw -= 360;
        }
    }

    private boolean isKeyDown(long window, int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }

    public void tick(Level level, long window) {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float xa = 0.0F;
        float ya = 0.0F;

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

        if(isKeyDown(window, GLFW.GLFW_KEY_SPACE) && this.onGround) {
            this.yd = 0.12F;
        }

        this.moveRelative(xa, ya, this.onGround ? 0.02F : 0.005F);
        this.yd = (float)((double)this.yd - 0.005D);
        this.move(level, this.xd, this.yd, this.zd);
        this.xd *= 0.91F;
        this.yd *= 0.98F;
        this.zd *= 0.91F;
        if(this.onGround) {
            this.xd *= 0.8F;
            this.zd *= 0.8F;
        }

        float speed = (float) Math.sqrt(xd * xd + zd * zd);
        walkTime += speed * 2f;
    }

    public void move(Level level, float xa, float ya, float za) {
        float xaOrg = xa;
        float yaOrg = ya;
        float zaOrg = za;
        ArrayList aABBs = level.getCubes(this.bb.expand(xa, ya, za));

        int i;
        for(i = 0; i < aABBs.size(); ++i) {
            ya = ((AABB)aABBs.get(i)).clipYCollide(this.bb, ya);
        }

        this.bb.move(0.0F, ya, 0.0F);

        for(i = 0; i < aABBs.size(); ++i) {
            xa = ((AABB)aABBs.get(i)).clipXCollide(this.bb, xa);
        }

        this.bb.move(xa, 0.0F, 0.0F);

        for(i = 0; i < aABBs.size(); ++i) {
            za = ((AABB)aABBs.get(i)).clipZCollide(this.bb, za);
        }

        this.bb.move(0.0F, 0.0F, za);
        this.onGround = yaOrg != ya && yaOrg < 0.0F;
        if(xaOrg != xa) {
            this.xd = 0.0F;
        }

        if(yaOrg != ya) {
            this.yd = 0.0F;
        }

        if(zaOrg != za) {
            this.zd = 0.0F;
        }

        this.x = (this.bb.x0 + this.bb.x1) / 2.0F;
        this.y = this.bb.y0;
        this.z = (this.bb.z0 + this.bb.z1) / 2.0F;
    }

    public void moveRelative(float xa, float za, float speed) {
        float dist = xa * xa + za * za;
        if(dist >= 0.01F) {
            dist = speed / (float)Math.sqrt(dist);
            xa *= dist;
            za *= dist;
            float sin = (float)Math.sin((double)this.yaw * Math.PI / 180.0D);
            float cos = (float)Math.cos((double)this.yaw * Math.PI / 180.0D);
            this.xd += xa * cos + za * sin;
            this.zd -= za * cos - xa * sin;
        }
    }

    public CompoundTag getNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("X", x);
        nbt.putFloat("Y", y);
        nbt.putFloat("Z", z);
        nbt.putFloat("Yaw", yaw);
        nbt.putFloat("Pitch", pitch);
        return nbt;
    }

    public void loadFromNBT(CompoundTag tag) {
        OmegaVoxel.ensureNBTValue(tag, "X");
        OmegaVoxel.ensureNBTValue(tag, "Y");
        OmegaVoxel.ensureNBTValue(tag, "Z");
        OmegaVoxel.ensureNBTValue(tag, "Yaw");
        OmegaVoxel.ensureNBTValue(tag, "Pitch");

        setPos(tag.getFloat("X"), tag.getFloat("Y"), tag.getFloat("Z"));
        yaw = tag.getFloat("Yaw");
        pitch = tag.getFloat("Pitch");
    }

    public Vector3f getInterpolatedPosition(float t) {
        return new Vector3f(xo + (x - xo) * t, yo + (y - yo) * t, zo + (z - zo) * t);
    }

    public AABB getBox(Vector3f pos) {
        return new AABB(pos.x - w / 2, pos.y, pos.z - w / 2, pos.x + w / 2, pos.y + h, pos.z + w / 2);
    }

    public void render(Renderer r, float t) {
        r.bind((Texture) null);
        r.c(255, 0, 0, 255);

        float speed = (float) Math.sqrt(xd * xd + zd * zd);
        float swing = (float) Math.sin(walkTime);

        swing *= Math.min(speed * 16f, 2.0F);

        model.yawRotation = yaw;
        model.setSwing(swing);
        model.setPitch(-pitch);
        model.render(r, xo + (x - xo) * t, yo + (y - yo) * t, zo + (z - zo) * t);
    }

    private void renderCube(Renderer r, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1) {
        r.start(GL11.GL_QUADS);

        // FRONT
        r.vuv(x0, y0, z1, u0, v0);
        r.vuv(x1, y0, z1, u1, v0);
        r.vuv(x1, y1, z1, u1, v1);
        r.vuv(x0, y1, z1, u0, v1);

        // BACK
        r.vuv(x1, y0, z0, u0, v0);
        r.vuv(x0, y0, z0, u1, v0);
        r.vuv(x0, y1, z0, u1, v1);
        r.vuv(x1, y1, z0, u0, v1);

        // TOP
        r.vuv(x0, y1, z1, u0, v0);
        r.vuv(x1, y1, z1, u1, v0);
        r.vuv(x1, y1, z0, u1, v1);
        r.vuv(x0, y1, z0, u0, v1);

        // BOTTOM
        r.vuv(x0, y0, z0, u0, v0);
        r.vuv(x1, y0, z0, u1, v0);
        r.vuv(x1, y0, z1, u1, v1);
        r.vuv(x0, y0, z1, u0, v1);

        // RIGHT
        r.vuv(x1, y0, z1, u0, v0);
        r.vuv(x1, y0, z0, u1, v0);
        r.vuv(x1, y1, z0, u1, v1);
        r.vuv(x1, y1, z1, u0, v1);

        // LEFT
        r.vuv(x0, y0, z0, u0, v0);
        r.vuv(x0, y0, z1, u1, v0);
        r.vuv(x0, y1, z1, u1, v1);
        r.vuv(x0, y1, z0, u0, v1);
    }
}