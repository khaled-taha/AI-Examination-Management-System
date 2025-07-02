package com.university.exam.courseManagement.controllers;

import com.university.exam.courseManagement.dtos.requestDTO.GroupRequestDTO;
import com.university.exam.courseManagement.dtos.responseDTO.GroupResponseDTO;
import com.university.exam.courseManagement.entities.Group;
import com.university.exam.courseManagement.services.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/groups")
@Tag(name = "Group Management", description = "APIs for managing groups")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER', 'STUDENT')")
    @Operation(
            summary = "Get groups by user ID",
            description = "Retrieves all groups associated with the specified user ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Groups retrieved successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupResponseDTO.class)))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Group not found")
            }
    )
    public ResponseEntity<List<GroupResponseDTO>> getGroupByUserId(
            @Parameter(description = "ID of the user", required = false)
            @PathVariable String userId) {
        return ResponseEntity.ok(groupService.getGroupsByUserId(userId));
    }


    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER', 'STUDENT')")
    @Operation(
            summary = "Get group by group ID",
            description = "Retrieves group associated with the specified group ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Group retrieved successfully",
                            content = @Content(schema = @Schema(implementation = GroupResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Group not found")
            }
    )
    public ResponseEntity<GroupResponseDTO> getGroupByGroupId(
            @Parameter(description = "ID of the group", required = true)
            @PathVariable UUID groupId) throws NoSuchObjectException {
        return ResponseEntity.ok(groupService.getGroupByGroupId(groupId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER', 'STUDENT')")
    public ResponseEntity<List<Group>> getAllGroups() {
        List<Group> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> createGroup(@Valid @RequestBody GroupRequestDTO groupRequestDTO) {
        groupService.createGroup(groupRequestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID id) {
        groupService.deleteGroup(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}