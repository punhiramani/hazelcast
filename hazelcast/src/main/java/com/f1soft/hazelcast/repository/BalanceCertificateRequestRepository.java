package com.f1soft.hazelcast.repository;

import com.f1soft.hazelcast.model.BalanceCertificateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceCertificateRequestRepository extends JpaRepository<BalanceCertificateRequest, Long> {
}
