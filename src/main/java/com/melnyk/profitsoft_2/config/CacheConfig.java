package com.melnyk.profitsoft_2.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

/**
 * Central application cache configuration.
 *
 * <p>Enables Spring caching and creates three Ehcache-backed JCache regions:
 * <ul>
 *     <li>{@link #GENRE_CACHE_NAME} – for cached Genre entities</li>
 *     <li>{@link #AUTHOR_CACHE_NAME} – for cached Author entities</li>
 *     <li>{@link #BOOK_CACHE_NAME} – for cached BookDetailsDto objects</li>
 * </ul>
 *
 * <p>Caches are configured with heap storage (1000 entries) and a TTL of 10 minutes.
 * Ehcache is used as the underlying provider via the JCache (JSR-107) API.</p>
 */
@Configuration
public class CacheConfig {

    public static final String GENRE_CACHE_NAME = "GENRE_ENTITY";
    public static final String AUTHOR_CACHE_NAME = "AUTHOR_ENTITY";
    public static final String BOOK_CACHE_NAME = "BOOK_DTO";

    @Bean
    public JCacheCacheManager cacheManager() {
        return new JCacheCacheManager(jCacheManager());
    }

    @Bean
    public javax.cache.CacheManager jCacheManager() {
        CachingProvider provider = Caching.getCachingProvider();
        javax.cache.CacheManager cacheManager = provider.getCacheManager();

        createCacheIfAbsent(cacheManager, GENRE_CACHE_NAME, Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    Object.class, Object.class,
                    ResourcePoolsBuilder.heap(1000)
                ).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10)))
                .build()
        ));

        createCacheIfAbsent(cacheManager, AUTHOR_CACHE_NAME, Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    Object.class, Object.class,
                    ResourcePoolsBuilder.heap(1000)
                ).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10)))
                .build()
        ));

        createCacheIfAbsent(cacheManager, BOOK_CACHE_NAME, Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    Object.class, Object.class,
                    ResourcePoolsBuilder.heap(1000)
                ).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10)))
                .build()
        ));

        return cacheManager;
    }

    private <K, V, C extends javax.cache.configuration.Configuration<K, V>> javax.cache.Cache<K, V> createCacheIfAbsent(javax.cache.CacheManager cacheManager, String cacheName, C config) {
        javax.cache.Cache<K, V> cache = cacheManager.getCache(cacheName);
        return cache != null ? cache : cacheManager.createCache(cacheName, config);
    }

}