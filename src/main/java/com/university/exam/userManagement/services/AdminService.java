package com.university.exam.userManagement.services;

import com.university.exam.exceptions.ValidationException;
import com.university.exam.userManagement.dtos.requestDTO.AdminRequestDTO;
import com.university.exam.userManagement.dtos.requestDTO.UserRequestDTO;
import com.university.exam.userManagement.dtos.responseDTO.AdminResponseDTO;
import com.university.exam.userManagement.entities.Admin;
import com.university.exam.userManagement.entities.Specialization;
import com.university.exam.userManagement.entities.Student;
import com.university.exam.userManagement.entities.User;
import com.university.exam.userManagement.repos.AdminRepository;
import com.university.exam.userManagement.repos.SpecializationRepository;
import com.university.exam.userManagement.repos.UserRepository;
import com.university.exam.utils.Utils;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {

    private final SpecializationRepository specializationRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository, SpecializationRepository specializationRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.specializationRepository = specializationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public AdminResponseDTO getAdminByUserId(UUID userId) throws Exception {
        return adminRepository.findByUser_UserId(userId)
                .map(AdminResponseDTO::fromEntity)
                .orElseThrow(() -> new NoSuchObjectException("Admin Not Found ["+ userId +"]"));
    }

    @Transactional(readOnly = true)
    public List<AdminResponseDTO> getAdmins() {
        return adminRepository.findAll().stream().map(AdminResponseDTO::fromEntity).toList();
    }

    @Transactional
    public AdminResponseDTO createAdmin(AdminRequestDTO adminRequestDTO) throws Exception {
        Specialization specialization = this.specializationRepository.findById(adminRequestDTO.getSpecializationId())
                .orElseThrow(() -> new NoSuchObjectException("Specialization Not Found ["+ adminRequestDTO.getSpecializationId() +"]"));

        String userId = (adminRequestDTO.getUserRequestDTO().getId() == null) ? "" : adminRequestDTO.getUserRequestDTO().getId().toString();
        validateEmail(userId, adminRequestDTO.getUserRequestDTO().getEmail());

        User user = UserRequestDTO.convertToUserEntity(adminRequestDTO.getUserRequestDTO(), "ADMIN");
        user = userRepository.save(user);

        Admin admin = null;
        if(user.getUserId() == null || user.getUserId().toString().isBlank()) admin = new Admin();

        Optional<Admin> savedAdmin = this.adminRepository.findByUser_UserId(user.getUserId());
        if(savedAdmin.isPresent()) admin = savedAdmin.get();

        if(admin == null) admin = new Admin();
        admin.setUser(user);
        admin.setSpecialization(specialization);
        admin = adminRepository.save(admin);

        return AdminResponseDTO.fromEntity(admin);
    }

    private void validateEmail(String userId, String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if(user.isEmpty()) return;

        boolean sameEmail = email.equals(user.get().getEmail());

        if ( (Utils.isEmpty(userId) && sameEmail) || (!user.get().getUserId().toString().equals(userId)) ) {
            throw new ValidationException("This Email Already exists!");
        }
    }
}