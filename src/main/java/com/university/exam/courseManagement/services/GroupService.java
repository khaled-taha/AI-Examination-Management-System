package com.university.exam.courseManagement.services;

import com.university.exam.courseManagement.dtos.requestDTO.GroupRequestDTO;
import com.university.exam.courseManagement.dtos.responseDTO.GroupResponseDTO;
import com.university.exam.courseManagement.entities.Group;
import com.university.exam.courseManagement.repos.GroupRepository;
import com.university.exam.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.NoSuchObjectException;
import java.util.*;

@Service
@AllArgsConstructor
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;


    @Transactional(readOnly = true)
    public List<GroupResponseDTO> getGroupsByUserId(String userId) {
        return fetchGroups(userId).stream()
                .map(GroupResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public GroupResponseDTO getGroupByGroupId(UUID groupId) throws NoSuchObjectException {
        return fetchGroup(groupId).map(GroupResponseDTO::fromEntity)
                .orElseThrow(() -> new NoSuchObjectException("Group Not Found [" + groupId + "]"));
    }
    private List<Group> fetchGroups(String userId) {
        return groupRepository.findAll();
    }

    private Optional<Group> fetchGroup(UUID groupId) {
        return groupRepository.findById(groupId);
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public void createGroup(GroupRequestDTO groupRequestDTO) {
        Group group = new Group();
        //group.setId(UUID.randomUUID()); // Generate UUID here
        group.setName(groupRequestDTO.getName());
        group.setDescription(groupRequestDTO.getDescription());
        groupRepository.save(group);
    }

    public void updateGroup(UUID id, GroupRequestDTO groupRequestDTO) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));

        group.setName(groupRequestDTO.getName());
        group.setDescription(groupRequestDTO.getDescription());
        groupRepository.save(group);
    }

    public void deleteGroup(UUID id) {
        groupRepository.deleteById(id);
    }
}
