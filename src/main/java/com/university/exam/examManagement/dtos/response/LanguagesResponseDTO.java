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
        language.setName(language.getName());
        language.setCodeName(language.getCodeName());
        language.setVersion(language.getVersion());
        return languagesResponseDTO;
    }
}
