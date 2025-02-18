package com.university.exam.repos;


import com.university.exam.entities.SuperResource;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SuperResourceRepository extends JpaRepository<SuperResource, UUID> {
    Optional<SuperResource> findByResourceId(UUID resourceId);

    @Modifying
    @Query(value = "DELETE FROM super_resource WHERE resource_id IN (:resourceIds)", nativeQuery = true)
    void deleteByResourceIdIn(@Param("resourceIds") List<UUID> resourceIds);

    List<SuperResource> findByResourceIdIn(List<UUID> resourceIds);
}