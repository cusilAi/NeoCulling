package com.neobyme.neoculling.client.mixin;

import com.neobyme.neoculling.client.Culler;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
	@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
	private void neoculling$cull(Entity entity, Frustum frustum, double camX, double camY, double camZ,
								 CallbackInfoReturnable<Boolean> cir) {
		if (Culler.shouldCull(entity, camX, camY, camZ)) {
			cir.setReturnValue(false);
		}
	}
}
