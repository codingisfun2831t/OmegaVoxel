package com.codingisfun2831t.omegavoxel.entity;

import com.codingisfun2831t.omegavoxel.AABB;
import com.codingisfun2831t.omegavoxel.level.Level;
import com.codingisfun2831t.omegavoxel.rendering.Renderer;
import net.querz.nbt.tag.CompoundTag;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import static com.codingisfun2831t.omegavoxel.NBTUtils.ensureNBTValue;
import static com.codingisfun2831t.omegavoxel.NBTUtils.getFloatDefault;

/**
 * Represents a entity
 */
public abstract class Entity {
    private static final Logger logger = LoggerFactory.getLogger(Entity.class);

    private Level level;

    // last tick pos
    public float xo;
    public float yo;
    public float zo;

    // current pos
    public float x;
    public float y;
    public float z;

    // velocity
    public float xd;
    public float yd;
    public float zd;

    // turn angle
    public float yaw;
    public float pitch;

    public boolean deleted = false;

    /**
     * Represents our hitbox.
     */
    public AABB bb;

    public boolean onGround = false;

    public Entity(Level level) {
        this.level = level;
        setPos(0, 0, 0);
    }

    public float w = 1.0F;
    public float h = 1.0F;
    public float eyeHeight = 0.5f;

    /**
     * Set position and update hitbox.
     */
    public void setPos(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.bb = new AABB(x - w / 2, y, z - w / 2, x + w / 2, y + h, z + w / 2);
    }

    /**
     * Turn using X and Y delta values.
     */
    public void turn(float xd, float yd) {
        double sensitivity = 0.15D;
        this.yaw += xd;
        this.pitch += yd;

        // limit pitch
        if(this.pitch < -90.0F) {
            this.pitch = -90.0F;
        }

        if(this.pitch > 90.0F) {
            this.pitch = 90.0F;
        }

        clampYaw();
    }

    /**
     * Clamp yaw from -180 to 180.
     */
    public void clampYaw() {
        yaw = (yaw % 360 + 360) % 360;

        if (yaw > 180) {
            yaw -= 360;
        }
    }

    public void tickMotion() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
    }

    public void tick() {
        if (y < -25) deleted = true;
    }

    /**
     * Move in XYZ, checking collision.
     */
    public void move(float xa, float ya, float za) {
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

    /**
     * Set xd, yd, and zd values based off X, Z, and speed - with XZ being 0-1 (WASD/joystick/whatever).
     */
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

    // NBT
    public CompoundTag getNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("X", x);
        nbt.putFloat("Y", y);
        nbt.putFloat("Z", z);
        nbt.putFloat("Yaw", yaw);
        nbt.putFloat("Pitch", pitch);
        nbt.putFloat("MotionX", xd);
        nbt.putFloat("MotionY", yd);
        nbt.putFloat("MotionZ", zd);
        return nbt;
    }

    public void loadFromNBT(CompoundTag tag) {
        ensureNBTValue(tag, "X");
        ensureNBTValue(tag, "Y");
        ensureNBTValue(tag, "Z");
        ensureNBTValue(tag, "Yaw");
        ensureNBTValue(tag, "Pitch");

        setPos(tag.getFloat("X"), tag.getFloat("Y"), tag.getFloat("Z"));
        yaw = tag.getFloat("Yaw");
        pitch = tag.getFloat("Pitch");

        xd = getFloatDefault(tag, "MotionX", 0);
        yd = getFloatDefault(tag, "MotionY", 0);
        zd = getFloatDefault(tag, "MotionZ", 0);
    }

    public Vector3f getInterpolatedPosition(float t) {
        return new Vector3f(xo + (x - xo) * t, yo + (y - yo) * t, zo + (z - zo) * t);
    }

    public AABB getBox(Vector3f pos) {
        return new AABB(pos.x - w / 2, pos.y, pos.z - w / 2, pos.x + w / 2, pos.y + h, pos.z + w / 2);
    }

    public abstract void render(Renderer r, float t, boolean interpolated);

    public Level getLevel() {
        return level;
    }

    public String getId() {
        return EntityRegistry.getIdFromClass(this.getClass());
    }
}