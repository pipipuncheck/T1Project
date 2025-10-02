package org.example.repository;

import org.example.model.HttpLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HttpLogRepository extends JpaRepository<HttpLog, Long> {
}
