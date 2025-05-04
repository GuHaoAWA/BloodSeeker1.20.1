package com.guhao.init;

import com.guhao.GuhaoMod;
import com.guhao.network.BloodBurstMessage;
import com.guhao.network.EnderMessage;
import com.guhao.network.RedFistMessage;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import static com.guhao.GuhaoMod.PACKET_HANDLER;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class Key {
    public static final KeyMapping ENDER = new KeyMapping("key.guhao.ender", GLFW.GLFW_KEY_KP_ADD, "key.categories.guhao") {
        private boolean isDownOld = false;

        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (isDownOld != isDown && isDown) {
                PACKET_HANDLER.sendToServer(new EnderMessage(0, 0));
                if (Minecraft.getInstance().player != null) {
                    EnderMessage.pressAction(Minecraft.getInstance().player, 0, 0);
                }
            }
            isDownOld = isDown;
        }

    };
    public static final KeyMapping BLOOD_BURST = new KeyMapping("key.guhao.blood_burst", GLFW.GLFW_KEY_KP_SUBTRACT, "key.categories.guhao"){
        private boolean isDownOld = false;

        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (isDownOld != isDown && isDown) {
                PACKET_HANDLER.sendToServer(new BloodBurstMessage(0, 0));
                if (Minecraft.getInstance().player != null) {
                    BloodBurstMessage.pressAction(Minecraft.getInstance().player, 0, 0);
                }
            }
            isDownOld = isDown;
        }

    };;
    public static final KeyMapping CTRL = new KeyMapping("key.guhao.ctrl", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.guhao") {
        private boolean isDownOld = false;
        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            isDownOld = isDown;
        }
    };
    public static final KeyMapping RIGHT = new KeyMapping("key.guhao.shift", GLFW.GLFW_MOUSE_BUTTON_2, "key.categories.guhao") {
        private boolean isDownOld = false;
        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            isDownOld = isDown;
        }
    };
    public static final KeyMapping RED_FIST = new KeyMapping("key.guhao.red_fist", GLFW.GLFW_KEY_ESCAPE, "key.categories.guhao") {
        private boolean isDownOld = false;

        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (isDownOld != isDown && isDown) {
                GuhaoMod.PACKET_HANDLER.sendToServer(new RedFistMessage(0, 0));
                if (Minecraft.getInstance().player != null) {
                    RedFistMessage.pressAction(Minecraft.getInstance().player, 0, 0);
                }
            }
            isDownOld = isDown;
        }
    };



    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(ENDER);
        event.register(BLOOD_BURST);
        event.register(CTRL);
        event.register(RIGHT);
        event.register(RED_FIST);
    }



    @Mod.EventBusSubscriber({Dist.CLIENT})
    public static class KeyEventListener {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (Minecraft.getInstance().screen == null) {
                RED_FIST.consumeClick();
                ENDER.consumeClick();
                BLOOD_BURST.consumeClick();
                CTRL.consumeClick();
                RIGHT.consumeClick();
            }
        }
    }
}
