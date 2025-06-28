package com.university.exam.userManagement.services;

import com.university.exam.exceptions.ValidationException;
import com.university.exam.userManagement.dtos.responseDTO.SpecializationResponseDTO;
import com.university.exam.userManagement.entities.Admin;
import com.university.exam.userManagement.entities.Specialization;
import com.university.exam.userManagement.repos.AdminRepository;
import com.university.exam.userManagement.repos.SpecializationRepository;
import com.university.exam.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public SpecializationService(SpecializationRepository specializationRepository, AdminRepository adminRepository) {
        this.specializationRepository = specializationRepository;
        this.adminRepository = adminRepository;
    }

    public SpecializationResponseDTO createSpecialization(String specializationName) {
        Specialization specialization = new Specialization();
        specialization.setSpecializationName(specializationName);
        Specialization savedSpecialization = specializationRepository.save(specialization);
        return SpecializationResponseDTO.convertToSpecializationResponseDTO(savedSpecialization);
    }

    public List<SpecializationResponseDTO> getAllSpecializations() {
        List<Specialization> specializations = specializationRepository.findAll();
        return specializations.stream()
                .map(SpecializationResponseDTO::convertToSpecializationResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteSpecialization(UUID specializationId) {
        List<Admin> admins = this.adminRepository.findBySpecialization_SpecializationId(specializationId);
        if(!Utils.isEmpty(admins)) throw new ValidationException("Cannot Remove Referenced Specialization!");
        specializationRepository.deleteById(specializationId);
    }
}