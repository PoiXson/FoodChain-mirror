package com.poixson.yumchain;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.poixson.tools.xJavaPlugin;
import com.poixson.yumchain.commands.Commands;


public class YumChainPlugin extends xJavaPlugin {
	@Override public int getSpigotPluginID() { return 107050; }
	@Override public int getBStatsID() {       return 17233;  }
	public static final String CHAT_PREFIX = ChatColor.AQUA+"[YUM] "+ChatColor.WHITE;

	// listeners
	protected final AtomicReference<YumChainHandler> chainHandler = new AtomicReference<YumChainHandler>(null);

	protected final AtomicReference<Commands> commands = new AtomicReference<Commands>(null);

	protected final AtomicReference<Material[]> cacheChainFoods  = new AtomicReference<Material[]>(null);
	protected final AtomicReference<Material[]> cacheBypassFoods = new AtomicReference<Material[]>(null);
	protected final AtomicReference<String[]> cacheMessagesYum   = new AtomicReference<String[]>(null);
	protected final AtomicReference<String[]> cacheMessagesYuck  = new AtomicReference<String[]>(null);

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
	protected static final String[] DEFAULT_BYPASS_FOODS = new String[] {
		"GOLDEN_APPLE",
		"ENCHANTED_GOLDEN_APPLE",
		"GLISTERING_MELON_SLICE",
		"GOLDEN_CARROT",
	};
	protected static final String[] DEFAULT_MESSAGES_YUM = new String[] {
		"Yum!",
		"Burp",
		"Mmmm",
	};
	protected static final String[] DEFAULT_MESSAGES_YUCK = new String[] {
		"Yuck",
		"Blah",
		"Bluh",
		"Ugh..",
		"Eh..",
		"Ew..",
	};



	public YumChainPlugin() {
		super(YumChainPlugin.class);
	}



	@Override
	public void onEnable() {
		super.onEnable();
		// yum chain handler
		{
			final YumChainHandler listener = new YumChainHandler(this);
			final YumChainHandler previous = this.chainHandler.getAndSet(listener);
			if (previous != null)
				previous.unregister();
			listener.register();
		}
		// commands
		{
			final Commands commands = new Commands(this);
			final Commands previous = this.commands.getAndSet(commands);
			if (previous != null)
				previous.close();
		}
		// custom stats
		{
			final Metrics metrics = this.metrics.get();
			if (metrics != null) {
				metrics.addCustomChart(YumChainFullChart.GetChart(this));
			}
		}
		// save
		this.setConfigChanged();
		this.saveConfigs();
	}

	@Override
	public void onDisable() {
		super.onDisable();
		// commands
		{
			final Commands commands = this.commands.getAndSet(null);
			if (commands != null)
				commands.close();
		}
		// yum chain handler
		{
			final YumChainHandler listener = this.chainHandler.getAndSet(null);
			if (listener != null)
				listener.unregister();
		}
		// clear config caches
		this.cacheChainFoods.set(null);
		this.cacheBypassFoods.set(null);
		this.cacheMessagesYum.set(null);
		this.cacheMessagesYuck.set(null);
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
		super.loadConfigs();
		// config.yml
		final FileConfiguration cfg = this.getConfig();
		this.config.set(cfg);
		this.configDefaults(cfg);
		cfg.options().copyDefaults(true);
	}
	@Override
	protected void saveConfigs() {
		super.saveConfig();
	}
	@Override
	protected void configDefaults(final FileConfiguration cfg) {
		super.configDefaults(cfg);
		cfg.addDefault("Foods",         DEFAULT_CHAIN_FOODS  );
		cfg.addDefault("Bypass",        DEFAULT_BYPASS_FOODS );
		cfg.addDefault("Yum Messages",  DEFAULT_MESSAGES_YUM );
		cfg.addDefault("Yuck Messages", DEFAULT_MESSAGES_YUCK);
	}



	public String[] getChainFoodsStr() {
		final List<String> foods = this.config.get().getStringList("Foods");
		return foods.toArray(new String[0]);
	}
	public Material[] getChainFoodsMat() {
		// cached
		{
			final Material[] foods = this.cacheChainFoods.get();
			if (foods != null)
				return foods;
		}
		// get chain foods
		{
			final LinkedList<Material> list = new LinkedList<Material>();
			final String[] foodStrs = this.getChainFoodsStr();
			for (final String food : foodStrs) {
				list.add(Material.getMaterial(food));
			}
			final Material[] foods = list.toArray(new Material[0]);
			this.cacheChainFoods.set(foods);
			return foods;
		}
	}



	public String[] getBypassFoodsStr() {
		final List<String> foods = this.config.get().getStringList("Bypass");
		return foods.toArray(new String[0]);
	}
	public Material[] getBypassFoodsMat() {
		// cached
		{
			final Material[] foods = this.cacheBypassFoods.get();
			if (foods != null)
				return foods;
		}
		// get bypass foods
		{
			final LinkedList<Material> list = new LinkedList<Material>();
			final String[] foodStrs = this.getBypassFoodsStr();
			for (final String food : foodStrs) {
				list.add(Material.getMaterial(food));
			}
			final Material[] foods = list.toArray(new Material[0]);
			this.cacheBypassFoods.set(foods);
			return foods;
		}
	}
	public boolean isBypassFood(final Material food) {
		final Material[] foods = this.getBypassFoodsMat();
		for (final Material f : foods) {
			if (food.equals(f))
				return true;
		}
		return false;
	}



	public String[] getYumMessages() {
		// cached
		{
			final String[] msgs = this.cacheMessagesYum.get();
			if (msgs != null)
				return msgs;
		}
		// get messages
		{
			final List<String> list = this.config.get().getStringList("Yum Messages");
			final List<String> msgs = new LinkedList<String>();
			for (String msg : list) {
				msg = msg.trim();
				if (!msg.isEmpty())
					msgs.add(msg);
			}
			final String[] array = msgs.toArray(new String[0]);
			this.cacheMessagesYum.set(array);
			return array;
		}
	}
	public String[] getYuckMessages() {
		// cached
		{
			final String[] msgs = this.cacheMessagesYuck.get();
			if (msgs != null)
				return msgs;
		}
		// get messages
		{
			final List<String> list = this.config.get().getStringList("Yuck Messages");
			final List<String> msgs = new LinkedList<String>();
			for (String msg : list) {
				msg = msg.trim();
				if (!msg.isEmpty())
					msgs.add(msg);
			}
			final String[] array = msgs.toArray(new String[0]);
			this.cacheMessagesYuck.set(array);
			return array;
		}
	}



}
