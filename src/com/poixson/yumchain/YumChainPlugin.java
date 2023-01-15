package com.poixson.yumchain;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.poixson.commonmc.tools.plugin.xJavaPlugin;
import com.poixson.yumchain.commands.Commands;


public class YumChainPlugin extends xJavaPlugin {
	protected static final String LOG_PREFIX  = "[YUM] ";
	protected static final int SPIGOT_PLUGIN_ID = 107050;
	protected static final int BSTATS_PLUGIN_ID = 17233;

	protected static final AtomicReference<YumChainPlugin> instance = new AtomicReference<YumChainPlugin>(null);

	// listeners
	protected final AtomicReference<Commands>     commandListener = new AtomicReference<Commands>(null);
	protected final AtomicReference<YumChainHandler> chainHandler = new AtomicReference<YumChainHandler>(null);

	protected static final String[] DEFAULT_CHAIN_FOODS = new String[] {
		"APPLE",
		"MELON_SLICE",
		"SWEET_BERRIES",
		"GLOW_BERRIES",
		"CARROT",
		"BAKED_POTATO",
		"BEETROOT",
		"DRIED_KELP",
		"COOKED_BEEF",
		"COOKED_PORKCHOP",
		"COOKED_MUTTON",
		"COOKED_CHICKEN",
		"COOKED_RABBIT",
		"COOKED_COD",
		"COOKED_SALMON",
		"BREAD",
		"COOKIE",
		"PUMPKIN_PIE",
		"MUSHROOM_STEW",
		"BEETROOT_SOUP",
		"RABBIT_STEW",
		"MILK_BUCKET",
		"HONEY_BOTTLE",
	};



	public YumChainPlugin() {
		super(YumChainPlugin.class);
	}



	@Override
	public void onEnable() {
		super.onEnable();
		if (!instance.compareAndSet(null, this))
			throw new RuntimeException("Plugin instance already enabled?");
		// commands listener
		{
			final Commands listener = new Commands(this);
			final Commands previous = this.commandListener.getAndSet(listener);
			if (previous != null)
				previous.unregister();
			listener.register();
		}
		// yum chain handler
		{
			final YumChainHandler listener = new YumChainHandler(this);
			final YumChainHandler previous = this.chainHandler.getAndSet(listener);
			if (previous != null)
				previous.unregister();
			listener.register();
		}
		// custom stats
		{
			final Metrics metrics = this.metrics.get();
			if (metrics != null) {
				metrics.addCustomChart(YumChainFullChart.GetChart(this));
			}
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();
		// commands listener
		{
			final Commands listener = this.commandListener.getAndSet(null);
			if (listener != null)
				listener.unregister();
		}
		// yum chain listener
		{
			final YumChainHandler listener = this.chainHandler.getAndSet(null);
			if (listener != null)
				listener.unregister();
		}
		if (!instance.compareAndSet(this, null))
			(new RuntimeException("Disable wrong instance of plugin?")).printStackTrace();
	}



	// -------------------------------------------------------------------------------
	// yum chain handdler



	public YumChainHandler getYumChainHandler() {
		return this.chainHandler.get();
	}
	public YumChainDAO getYumChain(final Player player) {
		return this.getYumChainHandler().getYumChain(player);
	}
	public YumChainDAO getYumChain(final UUID uuid) {
		return this.getYumChainHandler().getYumChain(uuid);
	}



	// -------------------------------------------------------------------------------
	// configs



	@Override
	protected void loadConfigs() {
		// plugin dir
		{
			final File path = this.getDataFolder();
			if (!path.isDirectory()) {
				if (!path.mkdir())
					throw new RuntimeException("Failed to create directory: " + path.toString());
				log.info(LOG_PREFIX + "Created directory: " + path.toString());
			}
		}
		// config.yml
		{
			final FileConfiguration cfg = this.getConfig();
			this.config.set(cfg);
			this.configDefaults(cfg);
			cfg.options().copyDefaults(true);
			super.saveConfig();
		}
	}
	@Override
	protected void saveConfigs() {
		// config.yml
		super.saveConfig();
	}
	@Override
	protected void configDefaults(final FileConfiguration cfg) {
		cfg.addDefault("Foods", DEFAULT_CHAIN_FOODS);
	}



	public String[] getChainFoodsStr() {
		final List<String> foods = this.config.get().getStringList("Foods");
		return foods.toArray(new String[0]);
	}
	public Material[] getChainFoodsMat() {
		final LinkedList<Material> list = new LinkedList<Material>();
		final String[] foods = this.getChainFoodsStr();
		for (final String food : foods) {
			list.add(Material.getMaterial(food));
		}
		return list.toArray(new Material[0]);
	}



	// -------------------------------------------------------------------------------



	@Override
	protected int getSpigotPluginID() {
		return SPIGOT_PLUGIN_ID;
	}
	@Override
	protected int getBStatsID() {
		return BSTATS_PLUGIN_ID;
	}



}
