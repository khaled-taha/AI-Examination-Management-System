package com.university.exam.services;

import com.university.exam.dtos.requestDTO.ResourceRequestDTO;
import com.university.exam.dtos.requestDTO.ResourceDirectoryRequestDTO;
import com.university.exam.dtos.responseDTO.DirectoryWithResourcesDTO;
import com.university.exam.dtos.responseDTO.ResourceDirectoryResponseDTO;
import com.university.exam.dtos.responseDTO.ResourceResponseDTO;
import com.university.exam.entities.Course;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public ResourceResponseDTO uploadResource(UUID resourceDirId, MultipartFile file) throws IOException {
        if(file != null && !file.isEmpty() && file.getContentType() != null && !file.getContentType().isEmpty()) {
            ResourceDirectory directory = fetchDirectory(resourceDirId);
            Resource resource = createResource(file, directory);
            createSuperResource(file.getBytes(), resource);
            return ResourceResponseDTO.fromEntity(resource);
        }
        throw new IOException("File is empty or has no content type");
    }

    @Transactional
    public void deleteResource(UUID resourceId) throws NoSuchObjectException {
        Resource resource = fetchResource(resourceId);
        SuperResource superResource = fetchSuperResource(resourceId);
        deleteSuperResource(superResource);
        deleteResource(resource);
    }

    public record FileDownloading(byte[] data, String name, String type, long size) {}
    @Transactional(readOnly = true)
    public FileDownloading downloadResource(UUID resourceId) throws NoSuchObjectException {
        SuperResource superResource = fetchSuperResource(resourceId);
        Resource resource = fetchResource(resourceId);
        return new FileDownloading(superResource.getData(), resource.getName(), resource.getType(), resource.getSize());
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

    @Transactional(readOnly = true)
    public List<DirectoryWithResourcesDTO> getSubDirectoriesById(UUID baseDirectoryId) throws NoSuchObjectException {
        ResourceDirectory baseDirectory = fetchDirectory(baseDirectoryId);

        List<ResourceDirectory> directories = fetchAllDirectories(baseDirectory);
        List<Resource> resources = fetchResourcesForDirectories(directories);
        Map<UUID, List<Resource>> resourcesByDirectoryId = groupResourcesByDirectoryId(resources);

        return mapDirectoriesToDTOs(directories, resourcesByDirectoryId);
    }

    private Resource createResource(MultipartFile file, ResourceDirectory directory) {
        Resource resource = new Resource();
        resource.setName(file.getName());
        resource.setType(file.getContentType());
        resource.setSize(file.getSize());
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
                .orElseThrow(() -> new NoSuchObjectException("Resource directory not found"));
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

    private List<ResourceDirectory> fetchAllDirectories(ResourceDirectory baseDirectory) {
        List<ResourceDirectory> subDirectories = resourceDirectoryRepository.findByBaseDirId(baseDirectory.getId());
        subDirectories.add(baseDirectory);
        return subDirectories;
    }

    private List<Resource> fetchResourcesForDirectories(List<ResourceDirectory> directories) {
        List<UUID> directoryIds = directories.stream()
                .map(ResourceDirectory::getId)
                .collect(Collectors.toList());

        return resourceRepository.findByResourceDirectoryIdIn(directoryIds);
    }

    private Map<UUID, List<Resource>> groupResourcesByDirectoryId(List<Resource> resources) {
        return resources.stream()
                .collect(Collectors.groupingBy(resource -> resource.getResourceDirectory().getId()));
    }

    private List<DirectoryWithResourcesDTO> mapDirectoriesToDTOs(
            List<ResourceDirectory> directories,
            Map<UUID, List<Resource>> resourcesByDirectoryId) {
        return directories.stream()
                .map(directory -> {
                    List<ResourceResponseDTO> resourceDTOs = resourcesByDirectoryId.getOrDefault(directory.getId(), List.of())
                            .stream()
                            .map(ResourceResponseDTO::fromEntity)
                            .collect(Collectors.toList());
                    return DirectoryWithResourcesDTO.fromEntity(directory, resourceDTOs);
                })
                .collect(Collectors.toList());
    }

    private void updateDirectoryDetails(ResourceDirectory directory, ResourceDirectoryRequestDTO directoryDTO) {
        directory.setName(directoryDTO.getName());
        directory.setCreator(directoryDTO.getCreator());
        directory.setBaseDirId(directoryDTO.getBaseDirId());
    }
}