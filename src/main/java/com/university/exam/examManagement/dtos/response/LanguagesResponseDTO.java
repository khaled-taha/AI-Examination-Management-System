package com.university.exam.examManagement.dtos.response;

import com.university.exam.examManagement.entities.ProgrammingLanguage;
import lombok.Data;

@Data
public class LanguagesResponseDTO {
    private String name;
    private String codeName;
    private String version;

    public static LanguagesResponseDTO fromEntity(ProgrammingLanguage language){
        LanguagesResponseDTO languagesResponseDTO = new LanguagesResponseDTO();
        languagesResponseDTO.setName(language.getName());
        languagesResponseDTO.setCodeName(language.getCodeName());
        languagesResponseDTO.setVersion(language.getVersion());
        return languagesResponseDTO;
    }
}
