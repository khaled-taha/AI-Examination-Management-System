package com.university.exam.userManagement.services;

import com.university.exam.userManagement.dtos.requestDTO.AdminRequestDTO;
import com.university.exam.userManagement.dtos.requestDTO.UserRequestDTO;
import com.university.exam.userManagement.dtos.responseDTO.AdminResponseDTO;
import com.university.exam.userManagement.entities.Admin;
import com.university.exam.userManagement.entities.Specialization;
import com.university.exam.userManagement.entities.User;
import com.university.exam.userManagement.repos.AdminRepository;
import com.university.exam.userManagement.repos.SpecializationRepository;
import com.university.exam.userManagement.repos.UserRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.NoSuchObjectException;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {

    @Autowired
    private SpecializationRepository specializationRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public AdminResponseDTO getAdminByUserId(UUID userId) throws Exception {
        return adminRepository.findByUser_UserId(userId)
                .map(AdminResponseDTO::convertToAdminResponseDTO)
                .orElseThrow(() -> new NoSuchObjectException("Admin Not Found ["+ userId +"]"));
    }

    @Transactional
    public AdminResponseDTO saveAdmin(AdminRequestDTO adminRequestDTO) throws Exception {
        Specialization specialization = this.specializationRepository.findById(adminRequestDTO.getSpecializationId())
                .orElseThrow(() -> new NoSuchObjectException("Specialization Not Found ["+ adminRequestDTO.getSpecializationId() +"]"));

        User user = UserRequestDTO.convertToUserEntity(adminRequestDTO.getUserRequestDTO(), "ADMIN");
        user = userRepository.save(user);

        Admin admin = new Admin();
        admin.setUser(user);
        admin.setSpecialization(specialization);
        admin = adminRepository.save(admin);

        return AdminResponseDTO.convertToAdminResponseDTO(admin);
    }
}