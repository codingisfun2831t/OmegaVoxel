package com.codingisfun2831t.omegavoxel.ui.widgets;

import com.codingisfun2831t.omegavoxel.ui.Color;
import com.codingisfun2831t.omegavoxel.ui.Rectangle;
import com.codingisfun2831t.omegavoxel.ui.UIRenderer;
import com.codingisfun2831t.omegavoxel.ui.Widget;

import java.util.function.BiConsumer;

import static com.codingisfun2831t.omegavoxel.ui.widgets.Button.drawButtonSprite;

public class Slider extends Widget {
    public String text;
    public int value;
    private Rectangle knob;

    private boolean active = false;

    public int min;
    public int max;
    public int step;

    public BiConsumer<Slider, Integer> valueChange;

    public Slider(String text, int width, int value) {
        super();

        this.text = text;
        this.value = value;
        this.knob = new Rectangle(0, 0, 0, 0);
        min = 0;
        max = 100;
        step = 0;
        setSize(width, 20);
    }

    public Slider range(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min cannot be greater than max");
        }

        this.min = min;
        this.max = max;
        setValue(value);
        return this;
    }

    public Slider step(int step) {
        if (step < 0) {
            throw new IllegalArgumentException("step cannot be negative");
        }

        this.step = step;
        setValue(value);
        return this;
    }

    public Slider onChange(BiConsumer<Slider, Integer> handler) {
        valueChange = handler;
        return this;
    }

    private void setValue(int value) {
        value = Math.max(min, Math.min(max, value));

        if (step > 0) {
            value = min + Math.round((float) (value - min) / step) * step;
            value = Math.max(min, Math.min(max, value));
        }

        if (this.value == value) return;

        this.value = value;

        if (valueChange != null) {
            valueChange.accept(this, value);
        }
    }

    private void updateValue(int x) {
        float position = (float) (x - KNOB_SIZE / 2) /
                (float) (getWidth() - KNOB_SIZE);

        position = Math.max(0.0F, Math.min(1.0F, position));

        setValue(min + Math.round((max - min) * position));
    }

    private float getSliderPosition() {
        if (max == min) return 0.0F;

        return (float) (value - min) / (float) (max - min);
    }

    @Override
    public void mouseDown(int x, int y, int button) {
        active = true;
        updateValue(x);
    }

    @Override
    public void mouseUp(int x, int y, int button) {
        active = false;
    }

    @Override
    public void mouseMove(int x, int y) {
        if (!active) return;

        updateValue(x);
    }

    private static final int KNOB_SIZE = 8;

    @Override
    public void renderContent(UIRenderer renderer) {
        drawButtonSprite(renderer, -1, getBounds());

        knob.set(
                getLeft() + (int) (getSliderPosition() * (getWidth() - KNOB_SIZE)),
                0,
                KNOB_SIZE,
                20
        );

        drawButtonSprite(renderer, (active || hovered) ? 1 : 0, knob);

        renderer.drawCenteredTextWithShadow(
                getCenterX(),
                getTop() + 6,
                text,
                (active || hovered) ? Color.BUTTON_TEXT_HOVER : Color.WHITE
        );
    }
}