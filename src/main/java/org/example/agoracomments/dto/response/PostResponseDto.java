package org.example.agoracomments.dto.response;

import lombok.Data;

@Data
public class PostResponseDto {

    private String authorLogin;
    private Integer postId;

    private String topic;

}
