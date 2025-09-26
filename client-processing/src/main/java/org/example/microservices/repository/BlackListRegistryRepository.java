package org.example.microservices.repository;

import org.example.microservices.model.BlackListRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlackListRegistryRepository extends JpaRepository<BlackListRegistry, Long> {

    Optional<BlackListRegistry> findByDocumentId(String documentId);
}
