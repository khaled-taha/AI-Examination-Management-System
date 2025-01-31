package com.university.exam.services;

import com.university.exam.dtos.CourseDTO;
import com.university.exam.entities.*;
import com.university.exam.exceptions.ValidationException;
import com.university.exam.repos.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ResourceDirectoryRepository resourceDirectoryRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private SuperResourceRepository superResourceRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) throws NoSuchObjectException {
        validateCourseDTO(courseDTO);

        Group group = fetchGroup(courseDTO.getGroupId());
        ResourceDirectory baseDirectory = createBaseDirectory(courseDTO.getName());
        Resource avatarResource = createAvatarResource(courseDTO, baseDirectory);
        SuperResource avatarSuperResource = createAvatarSuperResource(courseDTO, avatarResource);

        Course course = buildCourse(courseDTO, group, baseDirectory, avatarResource);
        course = courseRepository.save(course);

        return CourseDTO.fromEntity(course, avatarSuperResource.getData(), courseDTO.getAvatarType());
    }

    @Transactional
    public CourseDTO updateCourse(String code, CourseDTO courseDTO) throws NoSuchObjectException {
        validateCourseDTO(courseDTO);

        Group group = fetchGroup(courseDTO.getGroupId());
        Course course = fetchCourse(code);

        updateCourseDetails(course, courseDTO, group);
        updateAvatarIfProvided(course, courseDTO);

        course = courseRepository.save(course);
        return CourseDTO.fromEntity(
                course,
                course.getAvatarId() != null ? fetchSuperResourceData(course.getAvatarId()) : null,
                course.getAvatarId() != null ? fetchResourceType(course.getAvatarId()) : null
        );
    }

    @Transactional
    public void deleteCourse(String code) throws NoSuchObjectException {
        Course course = fetchCourse(code);
        ResourceDirectory baseDirectory = course.getBaseDirectory();

        List<ResourceDirectory> subDirectories = resourceDirectoryRepository.findByBaseDirId(baseDirectory.getId());
        List<UUID> directoryIds = getDirectoryIds(baseDirectory, subDirectories);
        List<Resource> allResources = resourceRepository.findByResourceDirectoryIdIn(directoryIds);
        List<UUID> resourceIds = getResourceIds(allResources);

        deleteSuperResources(resourceIds);
        deleteResources(resourceIds);
        deleteSubDirectories(subDirectories);
        deleteBaseDirectory(baseDirectory);
        deleteCourse(course);
    }

    // Helper Methods

    private void validateCourseDTO(CourseDTO courseDTO) throws ValidationException {
        if (courseDTO.getName() == null || courseDTO.getName().isEmpty()) {
            throw new ValidationException("Course name cannot be empty");
        }
    }

    private Group fetchGroup(UUID groupId) throws NoSuchObjectException {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchObjectException("Group not found with ID: " + groupId));
    }

    private ResourceDirectory createBaseDirectory(String courseName) {
        ResourceDirectory baseDirectory = new ResourceDirectory();
        baseDirectory.setName("Course_" + courseName + "_BaseDir");
        baseDirectory.setCreator("Admin"); // Replace with actual creator logic
        return resourceDirectoryRepository.save(baseDirectory);
    }

    private Resource createAvatarResource(CourseDTO courseDTO, ResourceDirectory baseDirectory) {
        Resource avatarResource = new Resource();
        avatarResource.setName("Course_" + courseDTO.getName() + "_Avatar");
        avatarResource.setType(courseDTO.getAvatarType());
        avatarResource.setResourceDirectory(baseDirectory);
        return resourceRepository.save(avatarResource);
    }

    private SuperResource createAvatarSuperResource(CourseDTO courseDTO, Resource avatarResource) {
        SuperResource avatarSuperResource = new SuperResource();
        avatarSuperResource.setData(courseDTO.getAvatar());
        avatarSuperResource.setResource(avatarResource);
        return superResourceRepository.save(avatarSuperResource);
    }

    private Course buildCourse(CourseDTO courseDTO, Group group, ResourceDirectory baseDirectory, Resource avatarResource) {
        return Course.builder()
                .code(courseDTO.getCode())
                .name(courseDTO.getName())
                .avatarId(avatarResource.getId())
                .active(courseDTO.isActive())
                .group(group)
                .baseDirectory(baseDirectory)
                .build();
    }

    private Course fetchCourse(String code) throws NoSuchObjectException {
        return courseRepository.findById(code)
                .orElseThrow(() -> new NoSuchObjectException("Course not found"));
    }

    private void updateCourseDetails(Course course, CourseDTO courseDTO, Group group) {
        course.setName(courseDTO.getName());
        course.setActive(courseDTO.isActive());
        course.setGroup(group);
    }

    private void updateAvatarIfProvided(Course course, CourseDTO courseDTO) throws NoSuchObjectException {
        if (courseDTO.getAvatar() != null) {
            Resource avatarResource = resourceRepository.findById(course.getAvatarId())
                    .orElseThrow(() -> new NoSuchObjectException("Avatar resource not found"));

            SuperResource avatarSuperResource = superResourceRepository.findByResourceId(avatarResource.getId())
                    .orElseThrow(() -> new NoSuchObjectException("Avatar super resource not found"));
            avatarSuperResource.setData(courseDTO.getAvatar());
            superResourceRepository.save(avatarSuperResource);
        }
    }

    private byte[] fetchSuperResourceData(UUID resourceId) throws NoSuchObjectException {
        return superResourceRepository.findByResourceId(resourceId)
                .orElseThrow(() -> new NoSuchObjectException("Super resource not found"))
                .getData();
    }

    private String fetchResourceType(UUID resourceId) throws NoSuchObjectException {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NoSuchObjectException("Resource not found"))
                .getType();
    }

    private List<UUID> getDirectoryIds(ResourceDirectory baseDirectory, List<ResourceDirectory> subDirectories) {
        List<UUID> directoryIds = subDirectories.stream()
                .map(ResourceDirectory::getId)
                .collect(Collectors.toList());
        directoryIds.add(baseDirectory.getId());
        return directoryIds;
    }

    private List<UUID> getResourceIds(List<Resource> resources) {
        return resources.stream()
                .map(Resource::getId)
                .collect(Collectors.toList());
    }

    private void deleteSuperResources(List<UUID> resourceIds) {
        superResourceRepository.deleteByResourceIdIn(resourceIds);
    }

    private void deleteResources(List<UUID> resourceIds) {
        resourceRepository.deleteAllByIdInBatch(resourceIds);
    }

    private void deleteSubDirectories(List<ResourceDirectory> subDirectories) {
        resourceDirectoryRepository.deleteAllByIdInBatch(
                subDirectories.stream()
                        .map(ResourceDirectory::getId)
                        .collect(Collectors.toList())
        );
    }

    private void deleteBaseDirectory(ResourceDirectory baseDirectory) {
        resourceDirectoryRepository.delete(baseDirectory);
    }

    private void deleteCourse(Course course) {
        courseRepository.delete(course);
    }
}