package com;

import java.net.URI;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.event.EventType;
import org.slf4j.Logger;

import static java.net.URI.create;
import static org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder.clusteredDedicated;
import static org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder.cluster;
import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManagerBuilder;
import static org.ehcache.config.builders.ResourcePoolsBuilder.heap;
import static org.ehcache.config.units.MemoryUnit.MB;
import static org.slf4j.LoggerFactory.getLogger;

public class RemoteClusteredCache {
	private static final Logger LOGGER = getLogger(RemoteClusteredCache.class);

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		URI uri = create("terracotta://localhost:9410/default-server");
		CacheEventListenerConfigurationBuilder cacheEventListenerConfiguration = CacheEventListenerConfigurationBuilder
				.newEventListenerConfiguration(new ListenerObject(),
						EventType.CREATED, EventType.UPDATED, EventType.REMOVED)
				.unordered().asynchronous();

		try (CacheManager cacheManager = newCacheManagerBuilder()
				.with(cluster(uri).autoCreate().defaultServerResource("main"))
				.withCache(
						"basicCache",
						newCacheConfigurationBuilder(
								Long.class,
								String.class,
								heap(100).offheap(1, MB).with(
										clusteredDedicated(5, MB))).add(
								cacheEventListenerConfiguration)).build(true)) {
			Cache<Long, String> basicCache = cacheManager.getCache(
					"basicCache", Long.class, String.class);
			basicCache.put(1L, "ONE");
		} finally {

		}
	}
}