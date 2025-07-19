package com.guhao.client.text;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColorPutter {
    public static final ChatFormatting[] Rainbow = new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.AQUA, ChatFormatting.BLUE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.BLACK,ChatFormatting.WHITE,ChatFormatting.DARK_RED};
    public static ChatFormatting[] StaticRainbow = new ChatFormatting[]{ChatFormatting.RED,ChatFormatting.DARK_RED};
    public static ChatFormatting[] GB = new ChatFormatting[]{ChatFormatting.BLACK,ChatFormatting.DARK_GRAY,ChatFormatting.GRAY,ChatFormatting.DARK_GRAY};
    public static String Format(String Input, ChatFormatting[] Style, double Delay, int Step) {
        StringBuilder Builder = new StringBuilder(Input.length() * 3);
        int Offset = (int) Math.floor(Util.getMillis() / Delay) % Style.length;
        for (int i = 0; i < Input.length(); ++i) {
            char c = Input.charAt(i);
            int col = (i * Step + Style.length - Offset) % Style.length;
            Builder.append(Style[col]);
            Builder.append(c);
        }
        return Builder.toString();
    }
    public static String Format2(String Input, ChatFormatting[] Style, double Delay, int Step) {
        StringBuilder Builder = new StringBuilder(Input.length() * 3);
        int Offset = (int) Math.floor(Util.getMillis() / Delay) % Style.length;
        for (int i = 0; i < Input.length(); ++i) {
            char c = Input.charAt(i);
            int col = (i * Step + Style.length - Offset) % Style.length;
            Builder.append(Style[col]);
            Builder.append(c);
        }
        return Builder.toString();
    }
    public static String Format3(String Input, ChatFormatting[] Style, double Delay, int Step) {
        StringBuilder Builder = new StringBuilder(Input.length() * 3);
        int Offset = (int) Math.floor(Util.getMillis() / Delay) % Style.length;
        for (int i = 0; i < Input.length(); ++i) {
            char c = Input.charAt(i);
            int col = (i * Step + Style.length - Offset) % Style.length;
            Builder.append(Style[col]);
            Builder.append(c);
        }
        return Builder.toString();
    }

    /*
        public String getName() {
            return ColorPutter.rainbow("name");
        }
     */
    public static String rainbow(String Input) {
        return Format(Input, Rainbow, 20.0D, 1);
    }
    public static String rainbow2(String Input) {
        return Format(Input, StaticRainbow, 20.0D, 1);
    }
    public static String rainbow3(String Input) {
        return Format2(Input, StaticRainbow, 40.0D, 1);
    }
    public static String rainbow4(String Input) {
        return Format3(Input, GB, 400.0D, 1);
    }
}
