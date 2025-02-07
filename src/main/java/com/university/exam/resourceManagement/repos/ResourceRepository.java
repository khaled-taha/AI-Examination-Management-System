package com.university.exam.resourceManagement.repos;

import com.university.exam.resourceManagement.entities.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {
    List<Resource> findByResourceDirectoryIdIn(List<UUID> directoryIds);
}
