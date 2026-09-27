package com.codingisfun2831t.omegavoxel.options;

public abstract class Option<T> {
    protected T value;
    protected T defaultValue;
    protected String saveKey;

    public Option(T defaultValue, String saveKey) {
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.saveKey = saveKey;
    }

    public T getValue() {
        return value;
    }
    public void setValue(T val) {
        this.value = val;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public String getSaveKey() {
        return saveKey;
    }

    public String getTranslationKey() {
        return "options." + saveKey;
    }

    public abstract String formatValue();
    public abstract void deserialize(String source);
    public abstract String serialize();
}
