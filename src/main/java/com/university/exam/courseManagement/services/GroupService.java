package com.university.exam.courseManagement.services;

import com.university.exam.courseManagement.dtos.responseDTO.GroupResponseDTO;
import com.university.exam.courseManagement.entities.Group;
import com.university.exam.courseManagement.repos.GroupRepository;
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
}
