package com.university.exam.examManagement.dtos.response;

import com.university.exam.examManagement.entities.ProgrammingLanguage;
import lombok.Data;

import java.util.UUID;

@Data
public class LanguagesResponseDTO {
    private UUID id;
    private String name;
    private String codeName;
    private String version;

    public static LanguagesResponseDTO fromEntity(ProgrammingLanguage language){
        LanguagesResponseDTO languagesResponseDTO = new LanguagesResponseDTO();
        languagesResponseDTO.setId(language.getId());
        languagesResponseDTO.setName(language.getName());
        languagesResponseDTO.setCodeName(language.getCodeName());
        languagesResponseDTO.setVersion(language.getVersion());
        return languagesResponseDTO;
    }
}
