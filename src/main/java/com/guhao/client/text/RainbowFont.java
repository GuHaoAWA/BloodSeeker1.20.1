package com.guhao.client.text;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class RainbowFont extends Font {

    public static RainbowFont INSTANCE = new RainbowFont((p_95014_) -> Minecraft.getInstance().fontManager.fontSets.getOrDefault(Minecraft.getInstance().fontManager.renames.getOrDefault(p_95014_, p_95014_), Minecraft.getInstance().fontManager.missingFontSet));

    public RainbowFont(Function<ResourceLocation, FontSet> p_92717_) {
        super(p_92717_,true);
    }

    public long milliTime() {
        return System.nanoTime() / 1000000L;
    }

    public double rangeRemap(double value, double low1, double high1, double low2, double high2) {
        return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
    }

    public int drawShadow(PoseStack stack, String text, float x, float y, int color) {
        float colorr = (float) milliTime() / 700.0F % 1.0F;
        float colorrStep = (float) rangeRemap(Math.sin(((float) milliTime() / 1200.0F)) % 6.28318D, -0.9D, 2.5D, 0.025D, 0.15D);
        float posX = x;
        for (int i = 0; i < text.length(); i++) {
            int c = color & 0xFF000000 | Color.HSBtoRGB(colorr, 0.75F, 1.0F);
            colorr += colorrStep;
            colorr %= 1.0F;
        }
        return (int) posX;
    }
}
