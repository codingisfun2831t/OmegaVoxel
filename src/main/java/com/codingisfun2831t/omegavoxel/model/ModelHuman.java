package com.codingisfun2831t.omegavoxel.model;

public class ModelHuman extends ModelBase {
    private ModelPart leftLeg;
    private ModelPart rightLeg;
    private ModelPart leftArm;
    private ModelPart rightArm;
    private ModelPart torso;
    private ModelPart head;

    public ModelHuman() {
        super();
        leftLeg = new ModelPart(-2, 0, 0, 4, 12, 4, 0, 16, "mob/char.png").pivot(-2, 12, 0);
        rightLeg = new ModelPart(2, 0, 0, 4, 12, 4, 0, 16, "mob/char.png").pivot(2, 12, 0);
        leftArm = new ModelPart(-6, 12, 0, 4, 12, 4, 40, 16, "mob/char.png").pivot(-6, 24, 0);
        rightArm = new ModelPart(6, 12, 0, 4, 12, 4, 40, 16, "mob/char.png").pivot(6, 24, 0);
        torso = new ModelPart(0, 12, 0, 8, 12, 4, 16, 16, "mob/char.png");
        head = new ModelPart(0, 24, 0, 8, 8, 8, 0, 0, "mob/char.png").pivot(0, 24, 0);
        part(leftLeg);
        part(rightLeg);
        part(leftArm);
        part(rightArm);
        part(torso);
        part(head);
    }

    public void setSwing(float swing) {
        leftLeg.pitch(swing * 45f);
        rightLeg.pitch(-swing * 45f);
        rightArm.pitch(swing * 45f);
        leftArm.pitch(-swing * 45f);
    }

    public void setPitch(float pitch) {
        head.pitch(pitch);
    }
}
