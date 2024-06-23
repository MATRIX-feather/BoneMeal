package xyz.nifeather.fexp.config;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Bindables.BindableList;
import xiamomc.pluginbase.Configuration.ConfigNode;
import xiamomc.pluginbase.Configuration.ConfigOption;
import xiamomc.pluginbase.Configuration.PluginConfigManager;
import xyz.nifeather.fexp.FeatherExperience;

import java.util.*;

public class FConfigManager extends PluginConfigManager
{
    public FConfigManager(FeatherExperience plugin)
    {
        super(plugin);

        instance = this;
    }

    private static FConfigManager instance;

    public static FConfigManager getInstance()
    {
        return instance;
    }

    @NotNull
    @Override
    public Map<ConfigNode, Object> getAllNotDefault()
    {
        var options = FConfigOptions.values();
        var map = new Object2ObjectOpenHashMap<ConfigNode, Object>();

        for (var o : options)
        {
            if (o.excludeFromInit()) continue;

            var val = getOrDefault((ConfigOption<Object>) o, Object.class);

            if (!val.equals(o.getDefault())) map.put(o.node(), val);
        }

        return map;
    }

    @Override
    public void reload()
    {
        super.reload();

        //更新配置
        int targetVersion = 7;

        var configVersion = getOrDefault(FConfigOptions.VERSION, 0);

        if (configVersion < targetVersion)
        {
            var nonDefaults = this.getAllNotDefault();

            plugin.saveResource("config.yml", true);
            plugin.reloadConfig();

            var newConfig = plugin.getConfig();

            nonDefaults.forEach((n, v) ->
            {
                //noinspection rawtypes
                if (v instanceof Collection collection)
                {
                    var matching = FConfigOptions.values().stream()
                            .filter(option -> option.node().toString().equals(n.toString()))
                            .findFirst().orElse(null);

                    if (matching != null)
                    {
                        Collection<?> defaultVal = null;

                        if (matching.getDefault() instanceof Collection<?> c1)
                            defaultVal = c1;

                        if (defaultVal != null)
                        {
                            defaultVal.forEach(c ->
                            {
                                if (!collection.contains(c))
                                    collection.add(c);
                            });
                        }

                        newConfig.set(n.toString(), v);
                    }
                }
                else
                {
                    newConfig.set(n.toString(), v);
                }
            });

            //region Migrate
            //endregion Migrate

            newConfig.set(FConfigOptions.VERSION.node().toString(), targetVersion);

            plugin.saveConfig();
            reload();
        }
    }
}