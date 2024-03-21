package xyz.nifeather.fexp.misc.integrations.coreprotect;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xiamomc.pluginbase.Annotations.Initializer;
import xyz.nifeather.fexp.FPluginObject;

import java.util.Locale;

public class CoreProtectIntegration extends FPluginObject
{
    private final CoreProtectAPI api;

    public CoreProtectIntegration()
    {
        api = CoreProtect.getInstance().getAPI();
    }

    public void logInteract(Player player, Location location)
    {
        api.logInteraction(player.getName(), location);
    }
}
