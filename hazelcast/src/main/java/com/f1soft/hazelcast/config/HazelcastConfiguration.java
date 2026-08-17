package com.f1soft.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

    private static final Logger log = LoggerFactory.getLogger(HazelcastConfiguration.class);

    @Value("${hazelcast.cluster.name:bankxp-cluster}")
    private String clusterName;

    @Value("${hazelcast.local.dev:false}")
    private boolean localDev;

    @Value("${hazelcast.headless.service.dns:hazelcast-headless-service.delivery.svc.cluster.local}")
    private String headlessServiceDns;

    @Bean
    public Config hazelcastConfig() {
        Config config = new Config();
        config.setClusterName(clusterName);
        config.setInstanceName(clusterName + "-instance");

        NetworkConfig network = config.getNetworkConfig();
        JoinConfig join = network.getJoin();

        if (localDev) {
            log.info("Hazelcast running in Local Dev Mode (Multicast Discovery)");
            join.getMulticastConfig().setEnabled(true);
            join.getTcpIpConfig().setEnabled(false);
            join.getKubernetesConfig().setEnabled(false);
        } else {
            log.info("Hazelcast running in Cluster Mode (Kubernetes Headless Service-DNS: {})", headlessServiceDns);
            join.getMulticastConfig().setEnabled(false);
            join.getTcpIpConfig().setEnabled(false);
            
            // Hazelcast Official Kubernetes Service-DNS Discovery (No RBAC required)
            // Continuously polls the Headless Service DNS so newly launched pods automatically join the cluster
            join.getKubernetesConfig()
                    .setEnabled(true)
                    .setProperty("service-dns", headlessServiceDns)
                    .setProperty("service-dns-timeout", "10");
        }

        // Define a distributed map for the balance certificates cache
        MapConfig mapConfig = new MapConfig("balanceCertificates");
        mapConfig.setTimeToLiveSeconds(3600); // 1 hour TTL
        mapConfig.setMaxIdleSeconds(1800);   // 30 minutes idle timeout
        config.addMapConfig(mapConfig);

        return config;
    }

    @Bean
    public HazelcastInstance hazelcastInstance(Config config) {
        return Hazelcast.getOrCreateHazelcastInstance(config);
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }
}
