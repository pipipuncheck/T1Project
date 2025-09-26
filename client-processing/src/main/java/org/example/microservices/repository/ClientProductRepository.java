package org.example.microservices.repository;

import org.example.microservices.model.ClientProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientProductRepository extends JpaRepository<ClientProduct, Long> {
}
