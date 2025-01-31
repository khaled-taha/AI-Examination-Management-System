package com.university.exam.repos;

import com.university.exam.entities.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {
    List<Resource> findByResourceDirectoryId(UUID resourceDirectoryId);

    List<Resource> findByResourceDirectoryIdIn(List<UUID> directoryIds); // Fetch resources in multiple directories
}
