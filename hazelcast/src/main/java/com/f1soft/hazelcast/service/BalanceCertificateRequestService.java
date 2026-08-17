package com.f1soft.hazelcast.service;

import com.f1soft.hazelcast.model.BalanceCertificateRequest;
import com.f1soft.hazelcast.repository.BalanceCertificateRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceCertificateRequestService {

    @Autowired
    private BalanceCertificateRequestRepository repository;

    @Cacheable(value = "balanceCertificates", key = "#id")
    public BalanceCertificateRequest getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<BalanceCertificateRequest> getAll() {
        return repository.findAll();
    }

    @CachePut(value = "balanceCertificates", key = "#result.id")
    public BalanceCertificateRequest save(BalanceCertificateRequest request) {
        return repository.save(request);
    }

    @CacheEvict(value = "balanceCertificates", key = "#id")
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
