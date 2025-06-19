package com.university.exam.userManagement.services;

import com.university.exam.userManagement.dtos.responseDTO.SpecializationResponseDTO;
import com.university.exam.userManagement.entities.Specialization;
import com.university.exam.userManagement.repos.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    @Autowired
    public SpecializationService(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
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
        specializationRepository.deleteById(specializationId);
    }
}