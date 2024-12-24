package xyz.nifeather.fexp.commands.brigadier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xyz.nifeather.fexp.FeatherExperience;

import java.util.List;

public interface IHaveFormattableHelp
{
    @Nullable
    public String permission();

    @NotNull
    public String name();

    public FormattableMessage getHelpMessage();

    default public List<FormattableMessage> getNotes()
    {
        return List.of(new FormattableMessage(FeatherExperience.namespace(), "_", "_"));
    }
}
