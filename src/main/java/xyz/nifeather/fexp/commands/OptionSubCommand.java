package xyz.nifeather.fexp.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Configuration.ConfigOption;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xyz.nifeather.fexp.CommonPermissions;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.commands.brigadier.IConvertibleBrigadier;
import xyz.nifeather.fexp.commands.builder.OptionSubCommands;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;
import xyz.nifeather.fexp.messages.strings.HelpStrings;

import java.util.List;

public class OptionSubCommand extends FPluginObject implements IConvertibleBrigadier
{
    @Override
    public @NotNull String name()
    {
        return "option";
    }

    @Resolved(shouldSolveImmediately = true)
    private FConfigManager config;

    @Override
    public void registerAsChild(ArgumentBuilder<CommandSourceStack, ?> parentBuilder)
    {
        var thisBuilder = Commands.literal(name()).requires(this::checkPermission);

        for (IConvertibleBrigadier subCommand : this.subCommands)
            subCommand.registerAsChild(thisBuilder);

        parentBuilder.then(thisBuilder);
    }

    public OptionSubCommand()
    {
        subCommands.add(getToggle("bonemeal.coral", FConfigOptions.FEAT_BONEMEAL_ON_CORAL));
        subCommands.add(getToggle("bonemeal.coral_allow_dispenser", FConfigOptions.CORAL_ALLOW_DISPENSER));

        subCommands.add(getToggle("bonemeal.flower", FConfigOptions.FEAT_BONEMEAL_ON_FLOWER));
        subCommands.add(getToggle("bonemeal.flower_allow_dispenser", FConfigOptions.FLOWER_ALLOW_DISPENSER));

        subCommands.add(getToggle("bonemeal.sugarcane", FConfigOptions.FEAT_BONEMEAL_ON_SUGARCANE));
        subCommands.add(getToggle("bonemeal.sugarcane_allow_dispenser", FConfigOptions.SUGARCANE_ALLOW_DISPENSER));

        subCommands.add(getToggle("shulkerbox.open", FConfigOptions.FEAT_OPEN_SHULKERBOX));
        subCommands.add(getInteger("shulkerbox.open_delay", FConfigOptions.SHULKERBOX_OPEN_DELAY));

        subCommands.add(getToggle("deepslate.farm", FConfigOptions.FEAT_DEEPSLATE_FARM));

        subCommands.add(getToggle("bossbar.warden", FConfigOptions.WARDEN_BOSSBAR));
        subCommands.add(getToggle("bossbar.warden_show_anger", FConfigOptions.WARDEN_BOSSBAR_SHOW_ANGER));

        subCommands.add(getToggle("save_tridents.enabled", FConfigOptions.TRIDENT));

        subCommands.add(getToggle("mob_eggs.enabled", FConfigOptions.VILLAGER_EGG));
        subCommands.add(getList("mob_eggs.disabled_worlds", FConfigOptions.EGG_DISABLED_WORLDS, null));
        subCommands.add(getList("mob_eggs.disabled_mobs", FConfigOptions.EGG_DISABLED_MOBS, null));
        subCommands.add(getList("mob_eggs.mob_whitelist", FConfigOptions.EGG_WHITELIST, null));

        subCommands.add(getToggle("protocol.component_whitelist", FConfigOptions.COMPONENT_WHITELIST));

        subCommands.add(getToggle("pvp.toggle.enabled", FConfigOptions.PVP_TOGGLE_ENABLED));

        subCommands.add(getToggle("torch_crossbow.enabled", FConfigOptions.ALLOW_TORCH_CROSSBOW));

        subCommands.add(getToggle("no_exp_cooldown.enabled", FConfigOptions.NO_EXP_COOLDOWN));

        subCommands.add(getToggle("fire_aspect_lit_candles", FConfigOptions.ALLOW_FIRE_ASPECT_LIT_CANDLES));
    }

    private IConvertibleBrigadier getList(String optionName, ConfigOption<List<String>> option,
                                @Nullable FormattableMessage displayName)
    {
        return new OptionSubCommands.StringListOptionBaseCommand(optionName, config, option);
    }

    private IConvertibleBrigadier getInteger(String name, ConfigOption<Integer> option)
    {
        return getInteger(name, option, null);
    }

    private IConvertibleBrigadier getInteger(String name, ConfigOption<Integer> option, @Nullable FormattableMessage displayName)
    {
        return new OptionSubCommands.IntegerOptionCommand(name, config, option);
    }

    private IConvertibleBrigadier getToggle(String name, ConfigOption<Boolean> option)
    {
        return getToggle(name, option, null);
    }

    private IConvertibleBrigadier getToggle(String name, ConfigOption<Boolean> option, @Nullable FormattableMessage displayName)
    {
        return new OptionSubCommands.BooleanOptionCommand(name, config, option);
    }

    private final List<IConvertibleBrigadier> subCommands = new ObjectArrayList<>();

    @Override
    public @Nullable String permission()
    {
        return CommonPermissions.optionCommand;
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return HelpStrings.pluginOptionDescription();
    }
}
