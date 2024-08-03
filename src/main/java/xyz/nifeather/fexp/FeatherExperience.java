package xyz.nifeather.fexp;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import xiamomc.pluginbase.Command.CommandHelper;
import xiamomc.pluginbase.Messages.MessageStore;
import xiamomc.pluginbase.XiaMoJavaPlugin;
import xyz.nifeather.fexp.commands.FCommandHelper;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.features.ac.ListenerHub;
import xyz.nifeather.fexp.features.ac.eventlisteners.BeaconListener;
import xyz.nifeather.fexp.features.bonemeal.BonemealListener;
import xyz.nifeather.fexp.features.bossbar.BossbarListener;
import xyz.nifeather.fexp.features.crossbow.CrossbowListener;
import xyz.nifeather.fexp.features.deepslateFarm.DeepslateListener;
import xyz.nifeather.fexp.features.minecart.MinecartListener;
import xyz.nifeather.fexp.features.mobbucket.MobBucketListener;
import xyz.nifeather.fexp.features.pvp.PvPListener;
import xyz.nifeather.fexp.features.serverLink.ServerLinkListener;
import xyz.nifeather.fexp.features.shulker.ShulkerListener;
import xyz.nifeather.fexp.features.trident.TridentSaverListener;
import xyz.nifeather.fexp.messages.FMessageStore;
import xyz.nifeather.fexp.misc.integrations.coreprotect.CoreProtectIntegration;

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
    private PvPListener pvpListener;

    private static final boolean enablePacketEvents = false;

    @Override
    public void onLoad()
    {
        super.onLoad();

        if (enablePacketEvents)
        {
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
            PacketEvents.getAPI().getSettings().reEncodeByDefault(true).checkForUpdates(false);

            PacketEvents.getAPI().load();
        }
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
        pluginManager.registerEvents(new TridentSaverListener(), this);
        pluginManager.registerEvents(new MobBucketListener(), this);
        pluginManager.registerEvents(new MinecartListener(), this);
        pluginManager.registerEvents(new BeaconListener(), this);
        pluginManager.registerEvents(new ServerLinkListener(), this);
        pluginManager.registerEvents(new CrossbowListener(), this);

        pvpListener = new PvPListener();
        pluginManager.registerEvents(pvpListener, this);
        dependencyManager.cache(pvpListener);

        softDeps.setHandle("CoreProtect", pl ->
                dependencyManager.cache(new CoreProtectIntegration()), true);

        softDeps.setHandle("Towny", pl -> MobBucketListener.townyInstalled = true, true);

        dependencyManager.cache(new FConfigManager(this));

        var cmdHelper = new FCommandHelper();
        dependencyManager.cache(cmdHelper);
        dependencyManager.cacheAs(CommandHelper.class, cmdHelper);
        dependencyManager.cacheAs(MessageStore.class, new FMessageStore());

        this.metrics = new Metrics(this, 21211);

        if (enablePacketEvents)
        {
            PacketEvents.getAPI().init();
            dependencyManager.cache(new ListenerHub());
        }
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

            if (pvpListener != null)
                pvpListener.dispose();

            if (enablePacketEvents)
                PacketEvents.getAPI().terminate();
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
