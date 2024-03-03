package xyz.nifeather.fexp;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import xiamomc.pluginbase.Command.CommandHelper;
import xiamomc.pluginbase.Messages.MessageStore;
import xiamomc.pluginbase.XiaMoJavaPlugin;
import xyz.nifeather.fexp.commands.FCommandHelper;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.features.bonemeal.BonemealListener;
import xyz.nifeather.fexp.features.bossbar.BossbarListener;
import xyz.nifeather.fexp.features.deepslateFarm.DeepslateListener;
import xyz.nifeather.fexp.features.shulker.ShulkerListener;
import xyz.nifeather.fexp.features.trident.TridentSaverListener;
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

    private Metrics metrics;

    @Override
    public void onEnable()
    {
        super.onEnable();

        var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BonemealListener(), this);
        pluginManager.registerEvents(new DeepslateListener(), this);
        pluginManager.registerEvents(shulkerListener = new ShulkerListener(), this);
        pluginManager.registerEvents(new BossbarListener(), this);
        pluginManager.registerEvents(new TridentSaverListener(), this);

        pluginManager.registerEvents(new TabCompleteListener(), this);

        dependencyManager.cache(new FConfigManager(this));

        var cmdHelper = new FCommandHelper();
        dependencyManager.cache(cmdHelper);
        dependencyManager.cacheAs(CommandHelper.class, cmdHelper);
        dependencyManager.cacheAs(MessageStore.class, new FMessageStore());

        this.metrics = new Metrics(this, 21211);
    }

    private ShulkerListener shulkerListener;

    @Override
    public void onDisable()
    {
        super.onDisable();

        try
        {
            if (shulkerListener != null)
                shulkerListener.onDisable();

            if (metrics != null)
                metrics.shutdown();
        }
        catch (Throwable t)
        {
            logger.warn("Error occurred while disabling: " + t.getMessage());
            t.printStackTrace();
        }
    }

    @Override
    public void startMainLoop(Runnable r)
    {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(this, o -> r.run(), 1, 1);
    }

    @Override
    public void runAsync(Runnable r)
    {
        Bukkit.getAsyncScheduler().runNow(this, o -> r.run());
    }
}
