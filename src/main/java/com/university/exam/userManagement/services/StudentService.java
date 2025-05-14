package com.university.exam.userManagement.services;

import com.university.exam.academicManagement.entities.AcademicTerm;
import com.university.exam.academicManagement.entities.AcademicYearGroup;
import com.university.exam.academicManagement.entities.StudentEnrollment;
import com.university.exam.academicManagement.repos.AcademicTermRepository;
import com.university.exam.academicManagement.repos.AcademicYearGroupRepository;
import com.university.exam.academicManagement.repos.StudentEnrollmentRepository;
import com.university.exam.courseManagement.entities.Group;
import com.university.exam.courseManagement.repos.GroupRepository;
import com.university.exam.exceptions.ValidationException;
import com.university.exam.userManagement.dtos.requestDTO.StudentRequestDTO;
import com.university.exam.userManagement.dtos.requestDTO.UserRequestDTO;
import com.university.exam.userManagement.dtos.responseDTO.StudentResponseDTO;
import com.university.exam.userManagement.entities.Student;
import com.university.exam.userManagement.entities.User;
import com.university.exam.userManagement.repos.StudentRepository;
import com.university.exam.userManagement.repos.UserRepository;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private AcademicYearGroupRepository academicYearGroupRepository;
    
    @Autowired
    private StudentEnrollmentRepository studentEnrollmentRepository;
    
    @Autowired
    private AcademicTermRepository academicTermRepository;

    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentByUserId(UUID userId) throws Exception {
        return studentRepository.findByUser_UserId(userId)
                .map(StudentResponseDTO::convertToStudentResponseDTO)
                .orElseThrow(() -> new NoSuchObjectException("Student Not Found ["+ userId +"]"));
    }

    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO) throws Exception {
        validateEmail(studentRequestDTO.getUserRequestDTO().getEmail());

        User user = saveUser(studentRequestDTO.getUserRequestDTO());
        Group group = findGroup(studentRequestDTO.getGroupId());
        AcademicYearGroup academicYearGroup = findAcademicYearGroup(group);
        Student student = createStudent(user);
        AcademicTerm firstTerm = findFirstTerm(academicYearGroup);

        saveEnrollment(student, academicYearGroup, firstTerm);

        return StudentResponseDTO.convertToStudentResponseDTO(student);
    }

    private void validateEmail(String email) {
        if (this.userRepository.existsByEmail(email)) {
            throw new ValidationException("This Email Already exists!");
        }
    }

    private User saveUser(UserRequestDTO userRequestDTO) {
        User user = UserRequestDTO.convertToUserEntity(userRequestDTO, "STUDENT");
        return userRepository.save(user);
    }

    private Group findGroup(UUID groupId) throws NoSuchObjectException {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchObjectException("Group Not Found [" + groupId + "]"));
    }

    private AcademicYearGroup findAcademicYearGroup(Group group) throws NoSuchObjectException {
        return academicYearGroupRepository.findLatestActiveAcademicYearByGroupId(group.getId())
                .orElseThrow(() -> new NoSuchObjectException(
                        "No Academic Year Found for Group [" + group.getId() + "] In the Current Year. " +
                                "Please create an Academic Year for this Group and try again."));
    }

    private Student createStudent(User user) {
        Student student = new Student();
        student.setUser(user);
        return studentRepository.saveAndFlush(student);
    }

    private AcademicTerm findFirstTerm(AcademicYearGroup academicYearGroup) throws NoSuchObjectException {
        return academicTermRepository
                .findByAcademicYearIdAndTermOrder(academicYearGroup.getAcademicYear().getId(), 1)
                .orElseThrow(() -> new NoSuchObjectException(
                        "No Academic Term Found for Academic Year Group [" + academicYearGroup.getId() + "] In the Current Year. " +
                                "Please create an Academic Term for this Academic Year Group and try again."));
    }

    private void saveEnrollment(Student student, AcademicYearGroup academicYearGroup, AcademicTerm firstTerm) {
        StudentEnrollment enrollment = new StudentEnrollment();
        enrollment.setStudent(student);
        enrollment.setAcademicYearGroup(academicYearGroup);
        enrollment.setTerm(firstTerm);
        enrollment.setEnrollmentStatus(StudentEnrollment.EnrollmentStatus.ACTIVE);
        studentEnrollmentRepository.save(enrollment);
    }
    
    
    
    
}