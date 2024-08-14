package xyz.nifeather.fexp.commands;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.pluginbase.Command.SubCommandHandler;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.commands.helpsections.Entry;
import xyz.nifeather.fexp.commands.helpsections.Section;
import xyz.nifeather.fexp.messages.strings.HelpStrings;
import xyz.nifeather.fexp.utilities.MessageUtils;

import java.util.List;

public class HelpSubCommand extends FPluginObject implements ISubCommand
{

    @Override
    public String getCommandName()
    {
        return "help";
    }

    @Initializer
    private void load()
    {
        setupCommandSections();
    }

    @Resolved
    private FCommandHelper cmdHelper;

    private final List<Section> commandSections = new ObjectArrayList<>();

    /**
     * 设置用于构建帮助信息的Section
     */
    private void setupCommandSections()
    {
        //不属于任何section的指令丢到这里
        //var miscCommandSection = new Section("/", HelpStrings.otherCommandDescription(), ObjectList.of());

        //commandSections.add(miscCommandSection);

        //遍历所有指令
        for (var c : cmdHelper.getCommands())
        {
            //如果指令拥有子指令，新建section
            if (c instanceof SubCommandHandler<?> sch)
            {
                //此section下所有指令的父级指令
                var parentCommandName = sch.getCommandName();

                List<FormattableMessage> notes = new ObjectArrayList<>(sch.getNotes());

                var section = new Section(parentCommandName,
                        sch.getHelpMessage(),
                        notes);

                //添加指令到section中
                for (var sc : sch.getSubCommands())
                {
                    var cmdName = parentCommandName + " " + sc.getCommandName() + " ";
                    section.add(new Entry(sc.getPermissionRequirement(),
                            cmdName,
                             sc.getHelpMessage(),
                            "/" + cmdName));
                }

                commandSections.add(section);
            }
            else
            {
                logger.warn("Adding misc commands are not supported yet.");
                /*miscCommandSection.add(new Entry(c.getPermissionRequirement(),
                        c.getCommandName(),
                        c.getHelpMessage(),
                        "/" + c.getCommandName() + " "));*/
            }
        }
    }

    private List<Component> constructSectionMessage(CommandSender sender, Section section)
    {
        var entries = section.getEntries();
        var locale = MessageUtils.getLocale(sender);

        //添加section的标题
        var list = ObjectArrayList.of(
                Component.empty(),
                HelpStrings.commandSectionHeaderString()
                        .resolve("basename", section.getCommandBaseName()).toComponent(locale));

        //build entry
        for (var entry : entries)
        {
            var perm = entry.permission();

            //如果指令不要求权限或者sender拥有此权限，添加到列表里
            if (perm == null || sender.hasPermission(perm))
            {
                var msg = HelpStrings.commandEntryString()
                        .withLocale(locale)
                        .resolve("basename", entry.baseName())
                        .resolve("description", entry.description(), null)
                        .toComponent(null)
                        .decorate(TextDecoration.UNDERLINED)
                        .hoverEvent(HoverEvent.showText(HelpStrings.clickToCompleteString().toComponent(locale)))
                        .clickEvent(ClickEvent.suggestCommand(entry.suggestingCommand()));

                list.add(msg);
            }
        }

        if (section.getNotes() != null && section.getNotes().size() >= 1)
        {
            list.addAll(ObjectList.of(
                    Component.empty(),
                    HelpStrings.specialNoteString().toComponent(locale)
            ));

            for (var f : section.getNotes())
            {
                list.add(f.toComponent(locale)
                        .decorate(TextDecoration.ITALIC));
            }
        }

        list.add(Component.empty());

        return list;
    }

    /**
     * 从设置的Section中构建sender的帮助信息
     *
     * @param sender 要显示给谁
     * @return 构建的帮助信息
     */
    private List<Component> constructHelpMessage(CommandSender sender)
    {
        var list = new ObjectArrayList<Component>();
        var locale = MessageUtils.getLocale(sender);

        list.add(HelpStrings.avaliableCommandHeaderString().toComponent(locale));
        for (var section : commandSections)
        {
            var msg = HelpStrings.commandNamePatternString()
                    .withLocale(locale)
                    .resolve("basename", section.getCommandBaseName())
                    .resolve("description", section.getDescription(), null)
                    .toComponent(locale)
                    .decorate(TextDecoration.UNDERLINED)
                    .clickEvent(ClickEvent.runCommand("/fexp " + getCommandName() + " " + section.getCommandBaseName()))
                    .hoverEvent(HoverEvent.showText(HelpStrings.clickToViewString().toComponent(locale)));

            list.add(msg);
        }

        return list;
    }


    @Override
    public String getPermissionRequirement()
    {
        return null;
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return HelpStrings.helpDescription();
    }

    @Override
    public @Nullable List<String> onTabComplete(List<String> args, CommandSender source)
    {
        var baseName = args.size() >= 1 ? args.get(0) : "";
        var matchedSections = commandSections.stream()
                .filter(s -> s.getCommandBaseName().toLowerCase().startsWith(baseName.toLowerCase())).toList();

        var list = new ObjectArrayList<String>();

        for (var s : matchedSections)
            list.add(s.getCommandBaseName());

        return list;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args)
    {
        if (args.length >= 1)
        {
            var section = commandSections.stream()
                    .filter(s -> s.getCommandBaseName().equalsIgnoreCase(args[0])).findFirst().orElse(null);

            if (section != null)
            {
                for (var s : constructSectionMessage(sender, section))
                    sender.sendMessage(MessageUtils.prefixes(sender, s));
            }
            else
                sender.sendMessage(MessageUtils.prefixes(sender, HelpStrings.sectionNotFoundString().withLocale(MessageUtils.getLocale(sender))));

            return true;
        }

        for (var s : constructHelpMessage(sender))
            sender.sendMessage(MessageUtils.prefixes(sender, s));

        return true;
    }
}
