package com.poixson.yumchain;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bstats.charts.SingleLineChart;


public class YumChainFullChart implements Callable<Integer> {

	protected final YumChainPlugin plugin;



	public static SingleLineChart GetChart(final YumChainPlugin plugin) {
		return new SingleLineChart(
			"full_yum_chain",
			new YumChainFullChart(plugin)
		);
	}
	public YumChainFullChart(final YumChainPlugin plugin) {
		this.plugin = plugin;
	}



	@Override
	public Integer call() throws Exception {
		final YumChainHandler handler = this.plugin.getYumChainHandler();
		final HashMap<UUID, Double> percents = handler.getChainPercents();
		int full = 0;
		for (final Double percent : percents.values()) {
			final double val = percent.doubleValue();
			if (val >= 0.99)
				full++;
		}
		return Integer.valueOf(full);
	}



}
