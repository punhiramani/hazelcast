package com.f1soft.hazelcast.controller;

import com.f1soft.hazelcast.model.BalanceCertificateRequest;
import com.f1soft.hazelcast.service.BalanceCertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/balance/certificates")
public class BalanceCertificateRequestController {

    @Autowired
    private BalanceCertificateRequestService service;

    @GetMapping("/{id}")
    public ResponseEntity<BalanceCertificateRequest> getById(@PathVariable Long id) {
        BalanceCertificateRequest request = service.getById(id);
        if (request != null) {
            return ResponseEntity.ok(request);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<BalanceCertificateRequest>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<BalanceCertificateRequest> create(@RequestBody BalanceCertificateRequest request) {
        return ResponseEntity.ok(service.save(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
