package com.neobyme.neoculler.client;

public final class Stats {
	/** Entities skipped since the game started (render thread only, so a plain long is fine). */
	public static long culled;
	private Stats() {}
}
