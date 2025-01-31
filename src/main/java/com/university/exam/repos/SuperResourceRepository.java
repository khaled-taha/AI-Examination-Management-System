package com.university.exam.repos;


import com.university.exam.entities.SuperResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SuperResourceRepository extends JpaRepository<SuperResource, UUID> {
    Optional<SuperResource> findByResourceId(UUID resourceId);

    void deleteByResourceId(UUID resourceId);

    void deleteByResourceIdIn(List<UUID> resourceIds); // Batch delete super resources
}