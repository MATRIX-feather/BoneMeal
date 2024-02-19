package xyz.nifeather.fexp.config;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Bindables.Bindable;
import xiamomc.pluginbase.Bindables.BindableList;
import xiamomc.pluginbase.Configuration.ConfigNode;
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

    public <T> T getOrDefault(Class<T> type, ConfigOption option)
    {
        var val = get(type, option);

        if (val == null)
        {
            set(option, option.defaultValue);
            return (T) option.defaultValue;
        }

        return val;
    }

    public <T> T getOrDefault(Class<T> type, ConfigOption option, @Nullable T defaultValue)
    {
        var val = get(type, option);

        if (val == null)
        {
            set(option, defaultValue);
            return defaultValue;
        }

        return val;
    }

    @NotNull
    @Override
    public Map<ConfigNode, Object> getAllNotDefault()
    {
        var options = ConfigOption.values();
        var map = new Object2ObjectOpenHashMap<ConfigNode, Object>();

        for (var o : options)
        {
            if (o.excludeFromInit) continue;

            var val = getOrDefault(Object.class, o);

            if (!val.equals(o.defaultValue)) map.put(o.node, val);
        }

        return map;
    }

    private Map<String, BindableList<?>> bindableLists;

    public <T> BindableList<T> getBindableList(Class<T> clazz, ConfigOption option)
    {
        ensureBindableListNotNull();

        //System.out.println("GET LIST " + option.toString());

        var val = bindableLists.getOrDefault(option.toString(), null);
        if (val != null)
        {
            //System.out.println("FIND EXISTING LIST, RETURNING " + val);
            return (BindableList<T>) val;
        }

        List<?> originalList = backendConfig.getList(option.toString(), new ArrayList<T>());
        originalList.removeIf(listVal -> !clazz.isInstance(listVal)); //Don't work for somehow

        var list = new BindableList<T>();
        list.addAll((List<T>)originalList);

        list.onListChanged((diffList, reason) ->
        {
            //System.out.println("LIST CHANGED: " + diffList + " WITH REASON " + reason);
            backendConfig.set(option.toString(), list);
            save();
        }, true);

        bindableLists.put(option.toString(), list);

        //System.out.println("RETURN " + list);

        return list;
    }

    public <T> Bindable<T> getBindable(Class<T> type, ConfigOption option)
    {
        if (type.isInstance(option.defaultValue))
            return getBindable(type, option, (T)option.defaultValue);

        throw new IllegalArgumentException(option + "的类型和" + type + "不兼容");
    }

    public <T> void bind(Bindable<T> bindable, ConfigOption option)
    {
        var bb = this.getBindable(option.defaultValue.getClass(), option);

        if (bindable.getClass().isInstance(bb))
            bindable.bindTo((Bindable<T>) bb);
        else
            throw new IllegalArgumentException("尝试将一个Bindable绑定在不兼容的配置(" + option + ")上");
    }

    public <T> void bind(Class<T> clazz, BindableList<T> bindable, ConfigOption option)
    {
        var bb = this.getBindableList(clazz, option);

        if (bindable.getClass().isInstance(bb))
            bindable.bindTo(bb);
        else
            throw new IllegalArgumentException("尝试将一个Bindable绑定在不兼容的配置(" + option + ")上");
    }

    public <T> Bindable<T> getBindable(Class<T> type, ConfigOption path, T defaultValue)
    {
        return super.getBindable(type, path.node, defaultValue);
    }

    private void ensureBindableListNotNull()
    {
        if (bindableLists == null)
            bindableLists = new Object2ObjectOpenHashMap<>();
    }

    @Override
    public void reload()
    {
        super.reload();

        ensureBindableListNotNull();
        bindableLists.forEach((node, list) ->
        {
            var configList = backendConfig.getList(node);
            list.clear();
            list.addAllInternal(configList);
        });

        //更新配置
        int targetVersion = 28;

        var configVersion = getOrDefault(Integer.class, ConfigOption.VERSION);

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
                    var matching = Arrays.stream(ConfigOption.values())
                            .filter(option -> option.node.toString().equals(n.toString()))
                            .findFirst().orElse(null);

                    if (matching != null)
                    {
                        Collection<?> defaultVal = null;

                        if (matching.defaultValue instanceof Collection<?> c1)
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

            newConfig.set(ConfigOption.VERSION.toString(), targetVersion);

            plugin.saveConfig();
            reload();
        }
    }

    public <T> T get(Class<T> type, ConfigOption option)
    {
        return get(type, option.node);
    }

    public void set(ConfigOption option, Object val)
    {
        this.set(option.node, val);
    }
}