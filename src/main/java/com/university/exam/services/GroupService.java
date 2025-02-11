package com.university.exam.services;

import com.university.exam.dtos.responseDTO.GroupResponseDTO;
import com.university.exam.entities.*;
import com.university.exam.repos.*;
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
    public List<GroupResponseDTO> getGroupsByUserId(UUID userId) {
        return fetchGroups(userId).stream()
                .map(GroupResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public GroupResponseDTO getGroupByGroupId(UUID groupId) throws NoSuchObjectException {
        return fetchGroup(groupId).map(GroupResponseDTO::fromEntity)
                .orElseThrow(() -> new NoSuchObjectException("Group Not Found [" + groupId + "]"));
    }
    private List<Group> fetchGroups(UUID userId) {
        return groupRepository.findAll();
    }

    private Optional<Group> fetchGroup(UUID groupId) {
        return groupRepository.findById(groupId);
    }
}