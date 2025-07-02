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
import com.university.exam.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentByUserId(UUID userId) throws Exception {

       Student student = studentRepository.findByUser_UserId(userId)
               .orElseThrow(() -> new NoSuchObjectException("Student Not Found ["+ userId +"]"));

       Optional<StudentEnrollment> enrollment = this.studentEnrollmentRepository.findLatestByStudentId(student.getStudentId());

        return enrollment.map(studentEnrollment ->
                StudentResponseDTO.convertToStudentResponseDTO(student, studentEnrollment.getAcademicYearGroup()))
                .orElseGet(() -> StudentResponseDTO.convertToStudentResponseDTO(student));
    }


    public StudentResponseDTO getStudentByEmail(String email) throws NoSuchObjectException {
        Student student = studentRepository.findByUser_Email(email)
                .orElseThrow(() -> new NoSuchObjectException("Student Not Found ["+ email +"]"));

        Optional<StudentEnrollment> enrollment = this.studentEnrollmentRepository.findLatestByStudentId(student.getStudentId());

        return enrollment.map(studentEnrollment ->
                        StudentResponseDTO.convertToStudentResponseDTO(student, studentEnrollment.getAcademicYearGroup()))
                .orElseGet(() -> StudentResponseDTO.convertToStudentResponseDTO(student));
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();

        return students.stream().map(student -> {
            Optional<StudentEnrollment> enrollment = this.studentEnrollmentRepository.findLatestByStudentId(student.getStudentId());
            return enrollment.map(e ->
                            StudentResponseDTO.convertToStudentResponseDTO(student, e.getAcademicYearGroup()))
                    .orElseGet(() -> StudentResponseDTO.convertToStudentResponseDTO(student));
        }).toList();
    }


    @Transactional
    public StudentResponseDTO saveStudent(StudentRequestDTO studentRequestDTO) throws Exception {
        String userId = (studentRequestDTO.getUserRequestDTO().getId() == null) ? "" : studentRequestDTO.getUserRequestDTO().getId().toString();
        validateEmail(userId, studentRequestDTO.getUserRequestDTO().getEmail());

        Group group = findGroup(studentRequestDTO.getGroupId());
        AcademicYearGroup academicYearGroup = findAcademicYearGroup(group);
        AcademicTerm firstTerm = findFirstTerm(academicYearGroup);

        User user = saveUser(studentRequestDTO.getUserRequestDTO());
        Student student = saveStudent(user);
        saveEnrollment(student, academicYearGroup, firstTerm);

        return StudentResponseDTO.convertToStudentResponseDTO(student, academicYearGroup);
    }

    private void validateEmail(String userId, String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if(user.isEmpty()) return;

        boolean sameEmail = email.equals(user.get().getEmail());

        if ( (Utils.isEmpty(userId) && sameEmail) || (!user.get().getUserId().toString().equals(userId)) ) {
            throw new ValidationException("This Email Already exists!");
        }
    }

    private User saveUser(UserRequestDTO userRequestDTO) {
        User user = UserRequestDTO.convertToUserEntity(userRequestDTO, "STUDENT");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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

    private Student saveStudent(User user) {
        Student student = null;
        if(user.getUserId() == null || user.getUserId().toString().isBlank()) student = new Student();

        Optional<Student> savedStudent = this.studentRepository.findByUser_UserId(user.getUserId());
        if(savedStudent.isPresent()) student = savedStudent.get();

        if(student == null) student = new Student();
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
        boolean found = this.studentEnrollmentRepository.isStudentEnrolledInYearGroupAndTerm(student.getStudentId(),academicYearGroup.getId(),firstTerm.getId());
        if(found) return;

        StudentEnrollment enrollment = new StudentEnrollment();
        enrollment.setStudent(student);
        enrollment.setAcademicYearGroup(academicYearGroup);
        enrollment.setTerm(firstTerm);
        enrollment.setEnrollmentStatus(StudentEnrollment.EnrollmentStatus.ACTIVE);
        studentEnrollmentRepository.save(enrollment);
    }
}