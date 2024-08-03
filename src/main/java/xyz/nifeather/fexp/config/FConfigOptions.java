package xyz.nifeather.fexp.config;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import xiamomc.pluginbase.Configuration.ConfigNode;
import xiamomc.pluginbase.Configuration.ConfigOption;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FConfigOptions
{
    public static final ConfigOption<String> MESSAGE_PREFIX = new ConfigOption<>(ConfigNode.create().append("message_pattern"), "[FExp] <message>");
    public static final ConfigOption<String> LANGUAGE_CODE = new ConfigOption<>(ConfigNode.create().append("language"), "en_us");
    public static final ConfigOption<Boolean> SINGLE_LANGUAGE = new ConfigOption<>(ConfigNode.create().append("single_language"), false);

    public static final ConfigOption<Boolean> FEAT_BONEMEAL_ON_CORAL = new ConfigOption<>(featureBonemealNode().append("coral"), true);
    public static final ConfigOption<Boolean> CORAL_ALLOW_DISPENSER = new ConfigOption<>(featureBonemealNode().append("coral_allow_dispenser"), true);

    public static final ConfigOption<Boolean> FEAT_BONEMEAL_ON_FLOWER = new ConfigOption<>(featureBonemealNode().append("flower"), true);
    public static final ConfigOption<Boolean> FLOWER_ALLOW_DISPENSER = new ConfigOption<>(featureBonemealNode().append("flower_allow_dispenser"), true);

    public static final ConfigOption<Boolean> FEAT_BONEMEAL_ON_SUGARCANE = new ConfigOption<>(featureBonemealNode().append("sugarcane"), true);
    public static final ConfigOption<Boolean> SUGARCANE_ALLOW_DISPENSER = new ConfigOption<>(featureBonemealNode().append("sugarcane_allow_dispenser"), true);

    public static final ConfigOption<Boolean> FEAT_OPEN_SHULKERBOX = new ConfigOption<>(featureNode().append("shulkerbox").append("open"), true);
    public static final ConfigOption<Integer> SHULKERBOX_OPEN_DELAY = new ConfigOption<>(featureNode().append("shulkerbox").append("wkar_open_delay"), 40);

    public static final ConfigOption<Boolean> FEAT_DEEPSLATE_FARM = new ConfigOption<>(featureNode().append("deepslate").append("farm"), true);

    public static final ConfigOption<Boolean> WARDEN_BOSSBAR = new ConfigOption<>(featureNode().append("bossbar").append("warden"), true);
    public static final ConfigOption<Boolean> WARDEN_BOSSBAR_SHOW_ANGER = new ConfigOption<>(featureNode().append("bossbar").append("warden_show_anger"), false);

    public static final ConfigOption<Boolean> TRIDENT = new ConfigOption<>(featureNode().append("save_tridents_from_void").append("enabled"), true);

    public static final ConfigOption<Boolean> VILLAGER_EGG = new ConfigOption<>(featureNode().append("mob_eggs").append("enabled"), true);
    public static final ConfigOption<List<String>> EGG_DISABLED_WORLDS = new ConfigOption<>(featureNode().append("mob_eggs").append("disabled_worlds"), List.of("disabled_world1", "disabled_world2"));
    public static final ConfigOption<List<String>> EGG_DISABLED_MOBS = new ConfigOption<>(featureNode().append("mob_eggs").append("disabled_mobs"), List.of("minecraft:disabled_1", "minecraft:disabled_2"));
    public static final ConfigOption<List<String>> EGG_WHITELIST = new ConfigOption<>(featureNode().append("mob_eggs").append("mob_whitelist"), List.of());

    public static final ConfigOption<Boolean> MINECART = new ConfigOption<>(featureNode().append("minecart").append("enabled"), true);
    public static final ConfigOption<List<String>> MINECART_CONFIGS = new ConfigOption<>(featureNode().append("minecart").append("configs"), List.of("minecraft:bedrock=1.5"));

    public static final ConfigOption<Boolean> COMPONENT_WHITELIST = new ConfigOption<>(featureNode().append("protocol").append("component_whitelist"), true);

    public static final ConfigOption<Boolean> PVP_TOGGLE_ENABLED = new ConfigOption<>(featureNode().append("pvp").append("enabled"), true);
    public static final ConfigOption<String> PVP_ENABLED_MESSAGE = new ConfigOption<>(featureNode().append("pvp").append("enabled_message"), "<color:#e0f2f1>PVP已启用</color>");
    public static final ConfigOption<String> PVP_DISABLED_MESSAGE = new ConfigOption<>(featureNode().append("pvp").append("disabled_message"), "<color:#e0f2f1>PVP已禁用</color>");

    public static final ConfigOption<List<String>> SERVER_LINKS = new ConfigOption<>(featureNode().append("server_links"), List.of());

    public static final ConfigOption<Boolean> ALLOW_TORCH_CROSSBOW = new ConfigOption<>(featureNode().append("torch_crossbow").append("enabled"), true);

    public static final ConfigOption<Boolean> NO_EXP_COOLDOWN = new ConfigOption<>(featureNode().append("no_exp_cooldown").append("enabled"), false);

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
