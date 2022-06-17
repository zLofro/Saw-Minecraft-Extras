package me.lofro.eufonia.util;

public class DefaultConfig implements SimpleConfig.DefaultConfig {
    private String content = "";

    public DefaultConfig addVal(String key, Object value) {
        content += key + " = " + value + '\n';
        return this;
    }

    @Override
    public String get(String namespace) {
        return content;
    }
}
