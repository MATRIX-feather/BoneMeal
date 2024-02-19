package xyz.nifeather.fexp.config;

import xiamomc.pluginbase.Configuration.ConfigNode;

public enum ConfigOption
{
    MESSAGE_PREFIX(ConfigNode.create().append("message_pattern"), "<message>"),
    LANGUAGE_CODE(ConfigNode.create().append("language"), "en_US"),
    SINGLE_LANGUAGE(ConfigNode.create().append("single_language"), false),

    FEAT_BONEMEAL_ON_CORAL(featureBonemealNode().append("coral"), true),
    FEAT_BONEMEAL_ON_FLOWER(featureBonemealNode().append("flower"), true),
    FEAT_BONEMEAL_ON_SUGARCANE(featureBonemealNode().append("sugarcane"), true),

    FEAT_OPEN_SHULKERBOX(featureNode().append("shulkerbox.open"), true),

    FEAT_DEEPSLATE_FARM(featureNode().append("deepslate.farm"), true),

    VERSION(ConfigNode.create().append("version"), 0);

    public final ConfigNode node;
    public final Object defaultValue;
    public final boolean excludeFromInit;

    private ConfigOption(ConfigNode node, Object defaultValue, boolean excludeFromInit)
    {
        this.node = node;
        this.defaultValue = defaultValue;
        this.excludeFromInit = excludeFromInit;
    }

    private ConfigOption(ConfigNode node, Object defaultValue)
    {
        this(node, defaultValue, false);
    }

    @Override
    public String toString()
    {
        return node.toString();
    }

    private static ConfigNode featureNode()
    {
        return ConfigNode.create().append("features");
    }
    private static ConfigNode featureBonemealNode()
    {
        return featureNode().append("features");
    }
}
