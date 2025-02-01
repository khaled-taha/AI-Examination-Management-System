package com.university.exam.repos;

import com.university.exam.entities.ResourceDirectory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceDirectoryRepository extends JpaRepository<ResourceDirectory, UUID> {
    List<ResourceDirectory> findByBaseDirId(UUID baseDirId);

    @Modifying
    @Query(value = """
        DELETE FROM super_resource sr
        WHERE sr.resource_id IN (
            SELECT r.id FROM resource r
            WHERE r.resource_dir_id = :directoryId
        );
        DELETE FROM resource r
        WHERE r.resource_dir_id = :directoryId;
        DELETE FROM resource_directory d
        WHERE d.id = :directoryId;
        """, nativeQuery = true)
    void deleteSubDirectoryWithResources(@Param("directoryId") UUID directoryId);
}