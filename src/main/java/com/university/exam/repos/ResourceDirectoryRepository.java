package com.university.exam.repos;

import com.university.exam.entities.ResourceDirectory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceDirectoryRepository extends JpaRepository<ResourceDirectory, UUID> {
    List<ResourceDirectory> findByBaseDirId(UUID baseDirId);
    void deleteAll(List<ResourceDirectory> directories);
}