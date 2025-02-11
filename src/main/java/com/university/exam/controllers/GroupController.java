package com.university.exam.controllers;

import com.university.exam.dtos.responseDTO.CourseResponseDTO;
import com.university.exam.dtos.responseDTO.GroupResponseDTO;
import com.university.exam.services.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{userId}")
    @Operation(
            summary = "Get groups by user ID",
            description = "Retrieves all groups associated with the specified user ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Groups retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CourseResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Group not found")
            }
    )
    public ResponseEntity<List<GroupResponseDTO>> getGroupByUserId(
            @Parameter(description = "ID of the user", required = false)
            @PathVariable UUID userId) {
        return ResponseEntity.ok(groupService.getGroupsByUserId(userId));
    }

    @GetMapping("/{groupId}")
    @Operation(
            summary = "Get group by group ID",
            description = "Retrieves group associated with the specified group ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Group retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CourseResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Group not found")
            }
    )
    public ResponseEntity<GroupResponseDTO> getGroupByGroupId(
            @Parameter(description = "ID of the group", required = true)
            @PathVariable UUID groupId) throws NoSuchObjectException {
        return ResponseEntity.ok(groupService.getGroupByGroupId(groupId));
    }
}
