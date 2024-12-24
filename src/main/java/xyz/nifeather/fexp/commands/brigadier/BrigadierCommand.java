package xyz.nifeather.fexp.commands.brigadier;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.fexp.FPluginObject;

public abstract class BrigadierCommand extends FPluginObject implements IConvertibleBrigadier
{
    public abstract String getPermissionRequirement();

    @Override
    public @Nullable final String permission()
    {
        return getPermissionRequirement();
    }

    @Override
    public boolean checkPermission(CommandSourceStack cmdSourceStack)
    {
        var perm = this.getPermissionRequirement();
        return perm == null || cmdSourceStack.getSender().hasPermission(perm);
    }
}
