package xyz.nifeather.fexp.features.serverLink;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.nifeather.fexp.FPluginObject;

public class ServerLinkListener extends FPluginObject implements Listener
{
    private final ServerLinkHandler linkHandler = new ServerLinkHandler();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        linkHandler.handlePlayer(event.getPlayer());
    }
}
