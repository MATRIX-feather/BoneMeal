package xyz.nifeather.fexp.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.CommandHelper;
import xyz.nifeather.fexp.FPluginObject;

public class TabCompleteListener extends FPluginObject implements Listener
{
    @Resolved
    private CommandHelper<?> helper;

    @EventHandler
    public void onTabComplete(TabCompleteEvent e)
    {
        if (e.isCancelled()) return;

        var result = helper.onTabComplete(e.getBuffer(), e.getSender());
        if (result != null) e.setCompletions(result);
    }
}
