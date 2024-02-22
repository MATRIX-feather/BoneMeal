package xyz.nifeather.fexp.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xiamomc.pluginbase.Messages.MessageStore;
import xyz.nifeather.fexp.CommonPermissions;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.messages.FMessageStore;
import xyz.nifeather.fexp.utilities.MessageUtils;

public class ReloadSubCommand extends FPluginObject implements ISubCommand
{
    @Override
    public @NotNull String getCommandName()
    {
        return "reload";
    }

    @Override
    public @Nullable String getPermissionRequirement()
    {
        return CommonPermissions.reloadCommand;
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return new FormattableMessage(plugin, "reload command");
    }

    @Resolved(shouldSolveImmediately = true)
    private FConfigManager config;

    @Resolved(shouldSolveImmediately = true)
    private MessageStore<?> messageStore;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args)
    {
        config.reload();
        messageStore.reloadConfiguration();

        sender.sendMessage(MessageUtils.prefixes(sender, "Done."));

        return ISubCommand.super.onCommand(sender, args);
    }
}
