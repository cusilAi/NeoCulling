package com.neobyme.neoculler.client;

import com.neobyme.neoculler.NeoCulling;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class NeoCullingClient implements ClientModInitializer {
private static KeyMapping openSettings;

@Override
public void onInitializeClient() {
Cfg.load();
KeyMapping.Category category = KeyMapping.Category.register(NeoCulling.id("main"));
openSettings = KeyMappingHelper.registerKeyMapping(
new KeyMapping("key.neoculling.settings", GLFW.GLFW_KEY_I, category));
ClientTickEvents.END_CLIENT_TICK.register(mc -> {
while (openSettings.consumeClick()) {
if (ctrlDown()) mc.gui.setScreen(new NeoSettingsScreen(null));
}
});
}

private static boolean ctrlDown() {
long w = GLFW.glfwGetCurrentContext();
return w != 0L && (GLFW.glfwGetKey(w, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
|| GLFW.glfwGetKey(w, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS);
}
}
