package xyz.nifeather.fexp.features.xpCooldown;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpCooldownChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;

public class ExpCooldownListener extends FPluginObject implements Listener
{
    private final Bindable<Boolean> enabled = new Bindable<>(false);

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(enabled, FConfigOptions.NO_EXP_COOLDOWN);

        enabled.onValueChanged((o, n) ->
        {
            var players = Bukkit.getOnlinePlayers();

            if (n)
                players.forEach(p -> p.setExpCooldown(0));
            else
                players.forEach(p -> p.setExpCooldown(1));
        }, true);
    }

    @EventHandler
    public void onCooldownChanged(PlayerExpCooldownChangeEvent e)
    {
        if (!enabled.get()) return;

        if (e.getReason() == PlayerExpCooldownChangeEvent.ChangeReason.PICKUP_ORB)
            e.setNewCooldown(0);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        if (!enabled.get()) return;

        e.getPlayer().setExpCooldown(0);
    }
}
