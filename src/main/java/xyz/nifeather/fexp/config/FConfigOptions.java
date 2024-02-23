package xyz.nifeather.fexp.config;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import xiamomc.pluginbase.Configuration.ConfigNode;
import xiamomc.pluginbase.Configuration.ConfigOption;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class FConfigOptions
{
    public static final ConfigOption<String> MESSAGE_PREFIX = new ConfigOption<>(ConfigNode.create().append("message_pattern"), "[FExp] <message>");
    public static final ConfigOption<String> LANGUAGE_CODE = new ConfigOption<>(ConfigNode.create().append("language"), "en_us");
    public static final ConfigOption<Boolean> SINGLE_LANGUAGE = new ConfigOption<>(ConfigNode.create().append("single_language"), false);

    public static final ConfigOption<Boolean> FEAT_BONEMEAL_ON_CORAL = new ConfigOption<>(featureBonemealNode().append("coral"), true);
    public static final ConfigOption<Boolean> FEAT_BONEMEAL_ON_FLOWER = new ConfigOption<>(featureBonemealNode().append("flower"), true);
    public static final ConfigOption<Boolean> FEAT_BONEMEAL_ON_SUGARCANE = new ConfigOption<>(featureBonemealNode().append("sugarcane"), true);

    public static final ConfigOption<Boolean> FEAT_OPEN_SHULKERBOX = new ConfigOption<>(featureNode().append("shulkerbox").append("open"), true);

    public static final ConfigOption<Boolean> FEAT_DEEPSLATE_FARM = new ConfigOption<>(featureNode().append("deepslate").append("farm"), true);

    public static final ConfigOption<Boolean> WARDEN_BOSSBAR = new ConfigOption<>(featureNode().append("bossbar").append("warden"), true);
    public static final ConfigOption<Boolean> WARDEN_BOSSBAR_SHOW_ANGER = new ConfigOption<>(featureNode().append("bossbar").append("warden_show_anger"), false);

    public static final ConfigOption<Integer> VERSION = new ConfigOption<>(ConfigNode.create().append("version"), 0);

    public static List<ConfigOption<?>> values()
    {
        var fields = Arrays.stream(FConfigOptions.class.getFields())
                .filter(f -> f.getType().equals(ConfigOption.class) && Modifier.isStatic(f.getModifiers()))
                .toList();

        var list = fields.stream().map(f ->
        {
            try
            {
                return (ConfigOption<?>) f.get(null);
            }
            catch (Throwable t)
            {
                throw new RuntimeException(t);
            }
        }).toList();

        return new ObjectArrayList<>(list);
    }

    private static ConfigNode featureNode()
    {
        return ConfigNode.create().append("features");
    }

    private static ConfigNode featureBonemealNode()
    {
        return featureNode().append("bone_meal");
    }
}
