package com.guhao.utils;


import com.dfdyz.epicacg.client.render.custom.BloomParticleRenderType;
import com.guhao.GuhaoMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec2f;
import yesman.epicfight.api.utils.math.Vec3f;

public class RenderUtils {
    public static final int DefaultLightColor = 15728880;
    public static final BloomParticleRenderType BLOOD_FIRE_FLAME = new BloomParticleRenderType(new ResourceLocation("guhao", "blood_fire_flame"), GetTexture("particle/blood_fire_flame"));
    public static int EmissiveLightPos = 15728880;

    public RenderUtils() {
    }

    public static void GLSetTexture(ResourceLocation texture) {
        TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
        AbstractTexture abstracttexture = texturemanager.getTexture(texture);
        RenderSystem.bindTexture(abstracttexture.getId());
        RenderSystem.texParameter(3553, 10242, 33071);
        RenderSystem.texParameter(3553, 10243, 33071);
        RenderSystem.setShaderTexture(0, abstracttexture.getId());
    }

    public static ResourceLocation GetTexture(String path) {
        return new ResourceLocation("guhao", "textures/" + path + ".png");
    }

    @OnlyIn(Dist.CLIENT)
    public static void AddParticle(ClientLevel level, Particle particle) {
        try {
            Minecraft mc = Minecraft.getInstance();
            Camera camera = mc.gameRenderer.getMainCamera();
            if (mc.level != level) {
                GuhaoMod.LOGGER.info("[ParticleEngine]Different Level!");
            }

            if (camera.isInitialized() && mc.particleEngine != null && camera.getPosition().distanceToSqr(particle.x, particle.y, particle.z) < 1024.0) {
                mc.particleEngine.add(particle);
            }
        } catch (Exception var4) {
        }

    }

    public static class Quad {
        private final Vec3f[] vertexs = new Vec3f[4];
        public final Vec2f[] uvs = new Vec2f[4];

        public Quad(float sizeX, float sizeY) {
            float x = sizeX / 2.0F;
            float z = -sizeY / 2.0F;
            this.vertexs[0] = new Vec3f(x, 0.0F, z);
            this.vertexs[1] = new Vec3f(-x, 0.0F, z);
            this.vertexs[2] = new Vec3f(-x, 0.0F, -z);
            this.vertexs[3] = new Vec3f(x, 0.0F, -z);
            this.uvs[0] = new Vec2f(1.0F, 1.0F);
            this.uvs[1] = new Vec2f(0.0F, 1.0F);
            this.uvs[2] = new Vec2f(0.0F, 0.0F);
            this.uvs[3] = new Vec2f(1.0F, 0.0F);
        }

        public Quad rotate(Vec3f rotAxis, float angle) {
            for(int i = 0; i < this.vertexs.length; ++i) {
                this.vertexs[i].rotate(angle, rotAxis);
            }

            return this;
        }

        public Quad move(float x, float y, float z) {
            for(int i = 0; i < this.vertexs.length; ++i) {
                this.vertexs[i] = this.vertexs[i].add(x, y, z);
            }

            return this;
        }

        public Vec3f GetVertex(int idx, OpenMatrix4f matrix4f) {
            return OpenMatrix4f.transform3v(matrix4f, this.vertexs[idx], new Vec3f());
        }

        public void PushVertex(VertexConsumer buffer, Vec3f camPos, OpenMatrix4f tf, float rCol, float gCol, float bCol, float alpha, int lightCol) {
            for(int i = 0; i < this.vertexs.length; ++i) {
                Vec3f pos = this.GetVertex(i, tf).sub(camPos);
                buffer.vertex((double)pos.x, (double)pos.y, (double)pos.z).color(rCol, gCol, bCol, alpha).uv(this.uvs[i].x, this.uvs[i].y).uv2(lightCol).endVertex();
            }

        }
    }
}
