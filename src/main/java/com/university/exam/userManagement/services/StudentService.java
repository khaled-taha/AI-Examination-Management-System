package com.university.exam.userManagement.services;

import com.university.exam.userManagement.dtos.requestDTO.StudentRequestDTO;
import com.university.exam.userManagement.dtos.requestDTO.UserRequestDTO;
import com.university.exam.userManagement.dtos.responseDTO.StudentResponseDTO;
import com.university.exam.userManagement.entities.ClassStudy;
import com.university.exam.userManagement.entities.Student;
import com.university.exam.userManagement.entities.User;
import com.university.exam.userManagement.repos.ClassStudyRepository;
import com.university.exam.userManagement.repos.StudentRepository;
import com.university.exam.userManagement.repos.UserRepository;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.NoSuchObjectException;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassStudyRepository classStudyRepository;

    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentByUserId(UUID userId) throws Exception {
        return studentRepository.findByUser_UserId(userId)
                .map(StudentResponseDTO::convertToStudentResponseDTO)
                .orElseThrow(() -> new NoSuchObjectException("Student Not Found ["+ userId +"]"));
    }

    @Transactional
    public StudentResponseDTO saveStudent(StudentRequestDTO studentRequestDTO) throws Exception {
        User user = UserRequestDTO.convertToUserEntity(studentRequestDTO.getUserRequestDTO(), "STUDENT");
        user = userRepository.save(user);

        ClassStudy classStudy = classStudyRepository.findById(studentRequestDTO.getClassStudyId())
                .orElseThrow(() -> new NoSuchObjectException("Class Study Not Found ["+ studentRequestDTO.getClassStudyId() +"]"));

        Student student = new Student();
        student.setUser(user);
        student.setClassStudy(classStudy);
        student = studentRepository.save(student);

        return StudentResponseDTO.convertToStudentResponseDTO(student);
    }
}