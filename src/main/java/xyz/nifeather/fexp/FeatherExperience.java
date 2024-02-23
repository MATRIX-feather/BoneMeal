package xyz.nifeather.fexp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xiamomc.pluginbase.Command.CommandHelper;
import xiamomc.pluginbase.Messages.MessageStore;
import xiamomc.pluginbase.XiaMoJavaPlugin;
import xyz.nifeather.fexp.commands.FCommandHelper;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.features.bonemeal.BonemealListener;
import xyz.nifeather.fexp.features.bossbar.BossbarListener;
import xyz.nifeather.fexp.features.deepslateFarm.DeepslateListener;
import xyz.nifeather.fexp.features.shulker.ShulkerListener;
import xyz.nifeather.fexp.listener.TabCompleteListener;
import xyz.nifeather.fexp.messages.FMessageStore;

public final class FeatherExperience extends XiaMoJavaPlugin
{
    public static FeatherExperience getInstance()
    {
        return instance;
    }

    private static FeatherExperience instance;

    private final static String namespace = "fexp";

    public static String namespace()
    {
        return namespace;
    }

    @Override
    public String getNameSpace()
    {
        return namespace;
    }

    public FeatherExperience()
    {
        instance = this;
    }

    @Override
    public void onEnable()
    {
        super.onEnable();

        var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BonemealListener(), this);
        pluginManager.registerEvents(new DeepslateListener(), this);
        pluginManager.registerEvents(shulkerListener = new ShulkerListener(), this);
        pluginManager.registerEvents(new BossbarListener(), this);

        pluginManager.registerEvents(new TabCompleteListener(), this);

        dependencyManager.cache(new FConfigManager(this));

        var cmdHelper = new FCommandHelper();
        dependencyManager.cache(cmdHelper);
        dependencyManager.cacheAs(CommandHelper.class, cmdHelper);
        dependencyManager.cacheAs(MessageStore.class, new FMessageStore());
    }

    private ShulkerListener shulkerListener;

    @Override
    public void onDisable()
    {
        super.onDisable();

        if (shulkerListener != null)
            shulkerListener.onDisable();
    }
}
