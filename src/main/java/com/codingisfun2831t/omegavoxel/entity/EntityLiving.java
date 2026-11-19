package com.codingisfun2831t.omegavoxel.entity;

import com.codingisfun2831t.omegavoxel.assets.Texture;
import com.codingisfun2831t.omegavoxel.level.Level;
import com.codingisfun2831t.omegavoxel.model.ModelBase;
import com.codingisfun2831t.omegavoxel.rendering.Renderer;

public abstract class EntityLiving extends Entity {
    public float xa;
    public float ya;

    protected ModelBase model;

    public EntityLiving(Level level) {
        super(level);

        this.xa = 0;
        this.ya = 0;
    }

    @Override
    public void tickMotion() {
        super.tickMotion();

        // move
        this.moveRelative(xa, ya, this.onGround ? 0.02F : 0.005F);
        this.yd = (float)((double)this.yd - 0.005D);

        this.move(this.xd, this.yd, this.zd);

        // friction/gravity/air resistance
        this.xd *= 0.91F;
        this.yd *= 0.98F;
        this.zd *= 0.91F;
        if(this.onGround) {
            this.xd *= 0.8F;
            this.zd *= 0.8F;
        }
    }

    @Override
    public void render(Renderer r, float t, boolean interpolated) {
        r.bind((Texture) null);
        r.c(255, 0, 0, 255);

        model.yawRotation = yaw;

        if (interpolated)
            model.render(r, xo + (x - xo) * t, yo + (y - yo) * t, zo + (z - zo) * t);
        else
            model.render(r, x, y, z);
    }
}
