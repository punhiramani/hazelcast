package com.f1soft.hazelcast.controller;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cache-info")
public class CacheDebugController {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCacheInfo() {
        Map<String, Object> info = new HashMap<>();

        try {
            // Local Pod Host IP
            info.put("currentPodIp", InetAddress.getLocalHost().getHostAddress());
            
            // Hazelcast Cluster Members
            info.put("clusterMembersCount", hazelcastInstance.getCluster().getMembers().size());
            info.put("clusterMembers", hazelcastInstance.getCluster().getMembers().stream()
                    .map(member -> member.getSocketAddress().toString())
                    .collect(Collectors.toList()));

            // Balance Certificates Distributed Map Details
            IMap<Object, Object> cacheMap = hazelcastInstance.getMap("balanceCertificates");
            info.put("cacheMapName", cacheMap.getName());
            info.put("cacheSize", cacheMap.size());
            info.put("cachedKeys", cacheMap.keySet());

        } catch (Exception e) {
            info.put("error", e.getMessage());
        }

        return ResponseEntity.ok(info);
    }
}
