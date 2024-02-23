package xyz.nifeather.fexp.features.bossbar;

import net.kyori.adventure.text.format.TextColor;

public class BossbarColor
{
    private final int r;
    private final int g;
    private final int b;

    public int red()
    {
        return r;
    }

    public int green()
    {
        return g;
    }

    public int blue()
    {
        return b;
    }

    public BossbarColor(int r, int g, int b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static BossbarColor fromTextColor(TextColor textColor)
    {
        return new BossbarColor(textColor.red(), textColor.green(), textColor.blue());
    }

    public TextColor toTextColor()
    {
        return TextColor.color(r, g, b);
    }
}
