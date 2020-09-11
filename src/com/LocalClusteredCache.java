package com;

import java.net.URL;
import java.util.EnumSet;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;

import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManager;
import static org.slf4j.LoggerFactory.getLogger;

public class LocalClusteredCache {
  private static final Logger LOGGER = getLogger(LocalClusteredCache.class);

  public static void main(String[] args) {
    URL myUrl = LocalClusteredCache.class.getResource("/ehcache.xml");
    Configuration xmlConfig = new XmlConfiguration(myUrl);
    try (CacheManager cacheManager = newCacheManager(xmlConfig)) {
      cacheManager.init();
      Cache<Long, String> basicCache = cacheManager.getCache("basicCache", Long.class, String.class);
      ListenerObject listener = new ListenerObject(); 
      basicCache.getRuntimeConfiguration().registerCacheEventListener(listener, EventOrdering.ORDERED,
          EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.UPDATED, EventType.REMOVED));
      basicCache.put(2L, "two");
      basicCache.put(3L, "Three");
      basicCache.remove(3L);
    }
  }
}