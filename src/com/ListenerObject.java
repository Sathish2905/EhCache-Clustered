package com;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

public class ListenerObject implements CacheEventListener<Object, Object> {
	@Override
	public void onEvent(CacheEvent<? extends Object, ? extends Object> event) {
		System.out.println(event);
	}
}