package com.codingisfun2831t.omegavoxel.entity;

import com.codingisfun2831t.omegavoxel.level.Level;
import com.codingisfun2831t.omegavoxel.model.ModelHuman;
import com.codingisfun2831t.omegavoxel.rendering.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Represents the player, with all the info.
 */
public class EntitySteve extends EntityLiving {
    private static final Logger logger = LoggerFactory.getLogger(EntitySteve.class);

    private float walkTime;

    public int tickCount = 0;

    public EntitySteve(Level level) {
        super(level);
        this.model = new ModelHuman();

        w = 0.6F;
        h = 1.8F;
        eyeHeight = 1.62f;
    }

    private static Random random = new Random();

    @Override
    public void tick() {
        super.tick();

        tickCount++;
        if (tickCount < 20) {
            ya = -1.0F;
        } else if (tickCount == 21) {
            tickCount = 0;
            yaw += random.nextFloat(-90f, 90f);
            clampYaw();
        }
    }

    @Override
    public void tickMotion() {
        super.tickMotion();

        float speed = (float) Math.sqrt(xd * xd + zd * zd);
        walkTime += speed * 2f;
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