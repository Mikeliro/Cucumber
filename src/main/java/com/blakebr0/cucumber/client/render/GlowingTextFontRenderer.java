package com.blakebr0.cucumber.client.render;

import com.blakebr0.cucumber.helper.ColorHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class GlowingTextFontRenderer extends FontRenderer {
    private final GlowingTextRenderer.ColorInfo info;

    public GlowingTextFontRenderer(FontRenderer parent, GlowingTextRenderer.ColorInfo info) {
        super(parent.font);
        this.info = info;
    }

    @Override
    public int drawString(MatrixStack stack, String text, float x, float y, int color) {
        float sine = 0.5F * ((float) Math.sin(Math.toRadians(4.0F * ((float) GlowingTextRenderer.getTicks() + Minecraft.getInstance().getRenderPartialTicks()))) + 1.0F);
        return super.drawString(stack, text, x, y, ColorHelper.intColor(this.info.r + (int) (this.info.rl * sine), this.info.g + (int) (this.info.gl * sine), this.info.b + (int) (this.info.bl * sine)));
    }

    @Override
    public int drawStringWithShadow(MatrixStack stack, String text, float x, float y, int color) {
        float sine = 0.5F * ((float) Math.sin(Math.toRadians(4.0F * ((float) GlowingTextRenderer.getTicks() + Minecraft.getInstance().getRenderPartialTicks()))) + 1.0F);
        return super.drawStringWithShadow(stack, text, x, y, ColorHelper.intColor(this.info.r + (int) (this.info.rl * sine), this.info.g + (int) (this.info.gl * sine), this.info.b + (int) (this.info.bl * sine)));
    }
}
