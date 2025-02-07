package com.university.exam.resourceManagement.repos;


import com.university.exam.resourceManagement.entities.SuperResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SuperResourceRepository extends JpaRepository<SuperResource, UUID> {
    Optional<SuperResource> findByResourceId(UUID resourceId);
    void deleteByResourceIdIn(List<UUID> resourceIds);
    List<SuperResource> findByResourceIdIn(List<UUID> resourceIds);
}