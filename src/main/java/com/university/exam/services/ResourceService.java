package com.university.exam.services;

import com.university.exam.dtos.requestDTO.ResourceRequestDTO;
import com.university.exam.dtos.requestDTO.ResourceDirectoryRequestDTO;
import com.university.exam.dtos.responseDTO.ResourceDirectoryResponseDTO;
import com.university.exam.dtos.responseDTO.ResourceResponseDTO;
import com.university.exam.entities.Resource;
import com.university.exam.entities.ResourceDirectory;
import com.university.exam.entities.SuperResource;
import com.university.exam.repos.ResourceDirectoryRepository;
import com.university.exam.repos.ResourceRepository;
import com.university.exam.repos.SuperResourceRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private SuperResourceRepository superResourceRepository;

    @Autowired
    private ResourceDirectoryRepository resourceDirectoryRepository;

    @Transactional
    public ResourceResponseDTO uploadResource(ResourceRequestDTO resourceRequestDTO, byte[] data) throws NoSuchObjectException {
        ResourceDirectory directory = fetchResourceDirectory(resourceRequestDTO.getResourceDirId());
        Resource resource = createResource(resourceRequestDTO, directory);
        createSuperResource(data, resource);
        return ResourceResponseDTO.fromEntity(resource);
    }

    @Transactional
    public void deleteResource(UUID resourceId) throws NoSuchObjectException {
        Resource resource = fetchResource(resourceId);
        SuperResource superResource = fetchSuperResource(resourceId);
        deleteSuperResource(superResource);
        deleteResource(resource);
    }

    @Transactional(readOnly = true)
    public byte[] downloadResource(UUID resourceId) throws NoSuchObjectException {
        SuperResource superResource = fetchSuperResource(resourceId);
        return superResource.getData();
    }

    @Transactional
    public ResourceDirectoryResponseDTO createDirectory(ResourceDirectoryRequestDTO directoryDTO) {
        ResourceDirectory directory = buildDirectory(directoryDTO);
        directory = saveDirectory(directory);
        return ResourceDirectoryResponseDTO.fromEntity(directory);
    }

    @Transactional
    public ResourceDirectoryResponseDTO updateDirectory(UUID directoryId, ResourceDirectoryRequestDTO directoryDTO) throws NoSuchObjectException {
        ResourceDirectory directory = fetchDirectory(directoryId);
        updateDirectoryDetails(directory, directoryDTO);
        directory = saveDirectory(directory);
        return ResourceDirectoryResponseDTO.fromEntity(directory);
    }

    @Transactional
    public void deleteDirectory(UUID directoryId) throws NoSuchObjectException {
        fetchDirectory(directoryId);
        resourceDirectoryRepository.deleteSubDirectoryWithResources(directoryId);
    }

    private ResourceDirectory fetchResourceDirectory(UUID directoryId) throws NoSuchObjectException {
        return resourceDirectoryRepository.findById(directoryId)
                .orElseThrow(() -> new NoSuchObjectException("Resource directory not found"));
    }

    private Resource createResource(ResourceRequestDTO resourceRequestDTO, ResourceDirectory directory) {
        Resource resource = new Resource();
        resource.setName(resourceRequestDTO.getName());
        resource.setType(resourceRequestDTO.getType());
        resource.setResourceDirectory(directory);
        return resourceRepository.save(resource);
    }

    private SuperResource createSuperResource(byte[] data, Resource resource) {
        SuperResource superResource = new SuperResource();
        superResource.setData(data);
        superResource.setResource(resource);
        return superResourceRepository.save(superResource);
    }

    private Resource fetchResource(UUID resourceId) throws NoSuchObjectException {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NoSuchObjectException("Resource not found"));
    }

    private SuperResource fetchSuperResource(UUID resourceId) throws NoSuchObjectException {
        return superResourceRepository.findByResourceId(resourceId)
                .orElseThrow(() -> new NoSuchObjectException("Super resource not found"));
    }

    private void deleteSuperResource(SuperResource superResource) {
        superResourceRepository.delete(superResource);
    }

    private void deleteResource(Resource resource) {
        resourceRepository.delete(resource);
    }

    private ResourceDirectory buildDirectory(ResourceDirectoryRequestDTO directoryDTO) {
        ResourceDirectory directory = new ResourceDirectory();
        directory.setName(directoryDTO.getName());
        directory.setCreator(directoryDTO.getCreator());
        directory.setBaseDirId(directoryDTO.getBaseDirId());
        return directory;
    }

    private ResourceDirectory saveDirectory(ResourceDirectory directory) {
        return resourceDirectoryRepository.save(directory);
    }

    private ResourceDirectory fetchDirectory(UUID directoryId) throws NoSuchObjectException {
        return resourceDirectoryRepository.findById(directoryId)
                .orElseThrow(() -> new NoSuchObjectException("Directory not found"));
    }

    private void updateDirectoryDetails(ResourceDirectory directory, ResourceDirectoryRequestDTO directoryDTO) {
        directory.setName(directoryDTO.getName());
        directory.setCreator(directoryDTO.getCreator());
        directory.setBaseDirId(directoryDTO.getBaseDirId());
    }
}