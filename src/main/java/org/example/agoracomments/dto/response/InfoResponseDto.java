package org.example.agoracomments.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InfoResponseDto {

    private Long id;
    private String login;

    private String avatarUrl;

    // Эти поля могут быть null, если это Организация
    private String name;
    private String lastName;

    // Это поле может быть null, если это Человек
    private String title;

}
