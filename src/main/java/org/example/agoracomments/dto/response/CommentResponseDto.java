package org.example.agoracomments.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String text;
    private Long postId;
    private String authorLogin;

    private String authorAvatarUrl;

    private LocalDateTime createdAt;

}
