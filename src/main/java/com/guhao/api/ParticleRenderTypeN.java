package com.guhao.api;

import com.dfdyz.epicacg.EpicACG;
import com.dfdyz.epicacg.client.render.custom.BloomParticleRenderType;
import com.dfdyz.epicacg.client.render.custom.SpaceBrokenRenderType;
import com.dfdyz.epicacg.utils.RenderUtils;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface ParticleRenderTypeN {

    ParticleRenderType PARTICLE_SHEET_LIT_NO_CULL = new ParticleRenderType() {
        public void begin(BufferBuilder p_107462_, @NotNull TextureManager p_107463_) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.disableCull();
            p_107462_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }


        public void end(Tesselator p_107465_) {
            p_107465_.end();
        }

        public String toString() {
            return "PARTICLE_SHEET_LIT_NO_CULL";
        }
    };
}

