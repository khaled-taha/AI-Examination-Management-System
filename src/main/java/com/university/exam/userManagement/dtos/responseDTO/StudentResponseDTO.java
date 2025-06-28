package com.university.exam.userManagement.dtos.responseDTO;

import com.university.exam.academicManagement.dtos.responseDTO.AcademicYearGroupResponseDTO;
import com.university.exam.academicManagement.entities.AcademicYearGroup;
import com.university.exam.courseManagement.dtos.responseDTO.GroupResponseDTO;
import com.university.exam.userManagement.entities.Student;
import lombok.Data;

import java.util.UUID;


@Data
public class StudentResponseDTO {
    private UserResponseDTO userResponseDTO;
    private UUID studentId;
    private AcademicYearGroupResponseDTO academicYearGroupResponseDTO;

    public static StudentResponseDTO convertToStudentResponseDTO(Student student) {
        StudentResponseDTO responseDTO = new StudentResponseDTO();
        responseDTO.setUserResponseDTO(UserResponseDTO.convertToUserResponseDTO(student.getUser()));
        responseDTO.setStudentId(student.getStudentId());
        return responseDTO;
    }

    public static StudentResponseDTO convertToStudentResponseDTO(Student student, AcademicYearGroup group) {
        StudentResponseDTO responseDTO = new StudentResponseDTO();
        responseDTO.setUserResponseDTO(UserResponseDTO.convertToUserResponseDTO(student.getUser()));
        responseDTO.setAcademicYearGroupResponseDTO(AcademicYearGroupResponseDTO.fromEntity(group));
        responseDTO.setStudentId(student.getStudentId());
        return responseDTO;
    }
}
