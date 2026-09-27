package com.codingisfun2831t.omegavoxel.options;

public class IntOption extends Option<Integer> {
    public int min;
    public int max;

    public IntOption(Integer defaultValue, String saveKey) {
        super(defaultValue, saveKey);
    }

    public IntOption range(int min, int max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public String formatValue() {
        return serialize();
    }

    @Override
    public void deserialize(String source) {
        try {
            value = Integer.parseInt(source);
        } catch (NumberFormatException e) {
            System.out.println("Integer option \"" + saveKey + "\" has invalid value: " + source);
        }
    }

    @Override
    public String serialize() {
        return String.valueOf(value);
    }
}
