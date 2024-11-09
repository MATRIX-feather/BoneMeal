package xyz.nifeather.fexp.features.bossbar;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Warden;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;
import xyz.nifeather.fexp.features.bossbar.easings.Easing;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class BossbarHolder extends FPluginObject
{
    private final LivingEntity bindingEntity;

    public BossbarHolder(LivingEntity entity)
    {
        this.bindingEntity = entity;
    }

    private final AtomicReference<BossBar> bindingBossbar = new AtomicReference<>(null);

    @Initializer
    private void load(FConfigManager config)
    {
        this.scheduleOn(bindingEntity, () ->
        {
            this.bindingBossbar.set(BossBar.bossBar(
                    bindingEntity.name(),
                    (float) (bindingEntity.getHealth() / bindingEntity.getMaxHealth()),
                    BossBar.Color.BLUE,
                    BossBar.Overlay.PROGRESS
            ));
        });

        config.bind(enableBossbar, FConfigOptions.WARDEN_BOSSBAR);
        config.bind(wardenBossbarShowAnger, FConfigOptions.WARDEN_BOSSBAR_SHOW_ANGER);

        enableBossbar.onValueChanged((o, n) ->
        {
            var binding = bindingBossbar.get();
            if (!n && binding != null)
                bindingEntity.getWorld().getPlayers().forEach(p -> p.hideBossBar(binding));
        });

        this.scheduleOnRegion(bindingEntity, this::update);
    }

    private final Bindable<Boolean> enableBossbar = new Bindable<>(false);
    private final Bindable<Boolean> wardenBossbarShowAnger = new Bindable<>(false);

    private synchronized void update()
    {
        if (this.disposed.get()) return;

        var bindingBossbar = this.bindingBossbar.get();

        this.scheduleOnRegion(bindingEntity, this::update);

        if (!enableBossbar.get() || bindingBossbar == null) return;

        var percent = (float) (bindingEntity.getHealth() / bindingEntity.getMaxHealth());
        bindingBossbar.progress(percent);

        if (bindingEntity.isDead())
        {
            logger.warn("%s is dead but it's binding bossbar is alive?!".formatted(bindingEntity));
            dispose();

            return;
        }

        if (plugin.getCurrentTick() % 10 == 0)
        {
            var nameComponent = Component.text();
            nameComponent.append(bindingEntity.name());

            if (bindingEntity instanceof Warden warden && wardenBossbarShowAnger.get())
            {
                var progress = warden.getAnger() / 80f;
                if (progress >= 1f) progress = 1f;

                var angerAt = warden.getEntityAngryAt() == null
                            ? Component.text("???")
                            : warden.getEntityAngryAt().name();

                angerAt = angerAt.color(TextColor.fromHexString("#cccccc")).decorate(TextDecoration.ITALIC);

                var angerText = Component.text(" \uD83D\uDD25 ").append(angerAt);

                var colorSafe = BossbarColor.fromTextColor(TextColor.fromHexString("#43A047"));
                var colorDanger = BossbarColor.fromTextColor(TextColor.fromHexString("#E53935"));

                var finalColor = TransformUtils.valueAt(progress, colorSafe, colorDanger, Easing.Plain);
                angerText = angerText.color(finalColor.toTextColor());

                nameComponent.append(angerText);
            }

            bindingBossbar.name(nameComponent);

            var loc = bindingEntity.getLocation();
            var playersRemaining = new ObjectArrayList<>(bindingEntity.getWorld().getPlayers());
            var playersInView = playersRemaining.stream()
                    .filter(p -> p.getLocation().distance(loc) < 80)
                    .toList();

            playersRemaining.removeAll(playersInView);
            playersInView.forEach(p -> p.showBossBar(bindingBossbar));
            playersRemaining.forEach(p -> p.hideBossBar(bindingBossbar));
        }
    }

    private final AtomicBoolean disposed = new AtomicBoolean(false);

    public synchronized void dispose()
    {
        wardenBossbarShowAnger.unBindAll();
        enableBossbar.unBindAll();

        var bindingBossbar = this.bindingBossbar.get();
        if (bindingBossbar != null)
        {
            for (var viewer : bindingBossbar.viewers())
            {
                if (viewer instanceof Audience audience)
                    audience.hideBossBar(bindingBossbar);
            }
        }

        //Bukkit.getOnlinePlayers().forEach(p -> p.hideBossBar(this.bindingBossbar));
        this.disposed.set(true);
    }
}
