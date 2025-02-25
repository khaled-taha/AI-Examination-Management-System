package com.university.exam.userManagement.dtos.responseDTO;

import com.university.exam.courseManagement.dtos.responseDTO.GroupResponseDTO;
import com.university.exam.userManagement.entities.Student;
import lombok.Data;

import java.util.UUID;


@Data
public class StudentResponseDTO {
    private UserResponseDTO userResponseDTO;
    private GroupResponseDTO groupResponseDTO;

    public static StudentResponseDTO convertToStudentResponseDTO(Student student) {
        StudentResponseDTO responseDTO = new StudentResponseDTO();
        responseDTO.setUserResponseDTO(UserResponseDTO.convertToUserResponseDTO(student.getUser()));
        responseDTO.setGroupResponseDTO(GroupResponseDTO.fromEntity(student.getGroup()));
        return responseDTO;
    }
}
