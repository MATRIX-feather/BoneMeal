package xyz.nifeather.fexp.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xiamomc.pluginbase.Messages.MessageStore;
import xyz.nifeather.fexp.CommonPermissions;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.commands.brigadier.BrigadierCommand;
import xyz.nifeather.fexp.commands.brigadier.IConvertibleBrigadier;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.messages.FMessageStore;
import xyz.nifeather.fexp.messages.strings.HelpStrings;
import xyz.nifeather.fexp.utilities.MessageUtils;

public class ReloadSubCommand extends BrigadierCommand
{
    @Override
    public @NotNull String name()
    {
        return "reload";
    }

    @Override
    public @Nullable String getPermissionRequirement()
    {
        return CommonPermissions.reloadCommand;
    }

    @Resolved(shouldSolveImmediately = true)
    private FConfigManager config;

    @Resolved(shouldSolveImmediately = true)
    private MessageStore<?> messageStore;

    @Override
    public void registerAsChild(ArgumentBuilder<CommandSourceStack, ?> parentBuilder)
    {
        parentBuilder.then(Commands.literal(name())
                .executes(this::execute));

        super.registerAsChild(parentBuilder);
    }

    private int execute(CommandContext<CommandSourceStack> context)
    {
        config.reload();
        messageStore.reloadConfiguration();

        var sender = context.getSource().getSender();
        sender.sendMessage(MessageUtils.prefixes(sender, "Done."));

        return 1;
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return HelpStrings.reloadDescription();
    }
}
