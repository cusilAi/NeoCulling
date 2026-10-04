package com.neobyme.neoculler.client;

import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Decides, per entity, whether rendering can be skipped. Cheapest tests run first. */
public final class Culler {
	/** entity id -> (gameTick << 1 | occludedBit); -1 = unknown */
	private static final Int2LongOpenHashMap CACHE = new Int2LongOpenHashMap();

	static {
		CACHE.defaultReturnValue(-1L);
	}

	public static boolean shouldCull(Entity e, double camX, double camY, double camZ) {
		if (Opts.ENABLED.value == 0) return false;

		Minecraft mc = Minecraft.getInstance();
		Entity camEntity = mc.getCameraEntity();
		if (e == camEntity || e == mc.player) return false;
		if (Opts.KEEP_GLOWING.value != 0 && e.isCurrentlyGlowing()) return false;
		if (Opts.KEEP_NAMED.value != 0 && e.hasCustomName()) return false;
		if (camEntity != null && !e.getPassengers().isEmpty() && e.getPassengers().contains(camEntity)) return false;

		// 1) distance (cheapest)
		double dx = e.getX() - camX, dy = e.getY() - camY, dz = e.getZ() - camZ;
		double d2 = dx * dx + dy * dy + dz * dz;
		int limit = limitFor(e);
		int global = Opts.MAX_DISTANCE.value;
		if (global > 0 && (limit == 0 || global < limit)) limit = global;
		if (limit > 0 && d2 > (double) limit * limit) {
			Stats.culled++;
			return true;
		}

		// 2) occlusion (expensive, cached per entity)
		if (Opts.OCCLUSION.value != 0) {
			double min = Opts.OCC_MIN.value;
			if (d2 >= min * min && occluded(e, camX, camY, camZ)) {
				Stats.culled++;
				return true;
			}
		}
		return false;
	}

	private static int limitFor(Entity e) {
		if (e instanceof ItemEntity) return Opts.ITEM_DIST.value;
		if (e instanceof ExperienceOrb) return Opts.XP_DIST.value;
		if (e instanceof ArmorStand) return Opts.ARMOR_DIST.value;
		if (e instanceof ItemFrame) return Opts.FRAME_DIST.value;
		if (e instanceof Projectile) return Opts.PROJ_DIST.value;
		if (e instanceof Player) return Opts.PLAYER_DIST.value;
		if (e instanceof LivingEntity) return Opts.MOB_DIST.value;
		return Opts.OTHER_DIST.value;
	}

	private static boolean occluded(Entity e, double cx, double cy, double cz) {
		Level level = e.level();
		long now = level.getGameTime();
		int id = e.getId();
		int interval = Opts.OCC_INTERVAL.value;

		long cached = CACHE.get(id);
		if (cached >= 0) {
			long t = cached >>> 1;
			if (now >= t && now - t < interval) return (cached & 1L) != 0;
		}

		if (CACHE.size() > 4096) CACHE.clear();

		Vec3 from = new Vec3(cx, cy, cz);
		AABB box = e.getBoundingBox();
		double mx = (box.minX + box.maxX) * 0.5, mz = (box.minZ + box.maxZ) * 0.5;
		double midY = (box.minY + box.maxY) * 0.5;

		// visible if ANY of three sample points (middle, top, bottom) has a clear line of sight
		boolean visible = !blocked(level, e, from, mx, midY, mz)
				|| !blocked(level, e, from, mx, box.maxY - 0.1, mz)
				|| !blocked(level, e, from, mx, box.minY + 0.1, mz);

		CACHE.put(id, (now << 1) | (visible ? 0L : 1L));
		return !visible;
	}

	private static boolean blocked(Level level, Entity e, Vec3 from, double x, double y, double z) {
		ClipContext ctx = new ClipContext(from, new Vec3(x, y, z), ClipContext.Block.VISIBILITY, ClipContext.Fluid.NONE, e);
		return level.clip(ctx).getType() != HitResult.Type.MISS;
	}

	private Culler() {}
}
