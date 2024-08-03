package xyz.nifeather.fexp.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Bindables.Bindable;
import xiamomc.pluginbase.Command.IPluginCommand;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;
import xyz.nifeather.fexp.features.pvp.PvPListener;
import xyz.nifeather.fexp.features.pvp.PvPStatus;
import xyz.nifeather.fexp.utilities.MessageUtils;

public class MainTogglePvPCommand extends FPluginObject implements IPluginCommand
{
    @Resolved(shouldSolveImmediately = true)
    private PvPListener pvpListener;

    @Resolved(shouldSolveImmediately = true)
    private FConfigManager config;

    public MainTogglePvPCommand()
    {
        config.bind(enabledString, FConfigOptions.PVP_ENABLED_MESSAGE);
        config.bind(disabledString, FConfigOptions.PVP_DISABLED_MESSAGE);
        config.bind(toggleAllowed, FConfigOptions.PVP_TOGGLE_ENABLED);
    }

    @Override
    public String getCommandName()
    {
        return "fpvp";
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return null;
    }

    private final Bindable<Boolean> toggleAllowed = new Bindable<>(false);
    private final Bindable<String> enabledString = new Bindable<>("<yellow>missingno");
    private final Bindable<String> disabledString = new Bindable<>("<yellow>missingno");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (!toggleAllowed.get()) return false;

        if (!(sender instanceof Player player))
        {
            sender.sendMessage(Component.text("只有玩家可以执行此指令"));
            return true;
        }

        var result = pvpListener.toggleFor(player);
        if (result == PvPStatus.ENABLED)
            player.sendMessage(MiniMessage.miniMessage().deserialize(enabledString.get()));
        else
            player.sendMessage(MiniMessage.miniMessage().deserialize(disabledString.get()));

        return true;
    }
}
