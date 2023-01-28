package com.poixson.yumchain;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class FoodFullerTask extends BukkitRunnable {

	protected final YumChainPlugin plugin;
	protected final Player player;



	public FoodFullerTask(final YumChainPlugin plugin, final Player player) {
		this.plugin = plugin;
		this.player = player;
	}



	public void start() {
		this.runTaskTimer(this.plugin, 40L, 40L);
	}



	@Override
	public void run() {
		final int food = this.player.getFoodLevel();
		if (food >= 20) {
			this.cancel();
			return;
		}
		this.player.setFoodLevel(food + 1);
	}



}
