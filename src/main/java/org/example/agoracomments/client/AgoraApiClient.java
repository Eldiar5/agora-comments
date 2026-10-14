package org.example.agoracomments.client;

import org.example.agoracomments.dto.response.InfoResponseDto;
import org.example.agoracomments.dto.response.PostResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "AGORA-API", path = "/api/v1")
public interface AgoraApiClient {

    @GetMapping("/post/by-postId/{postId}")
    PostResponseDto getPostById(
            @RequestHeader("Authorization") String token,
            @PathVariable("postId") Long postId
    );

    @GetMapping("/info/by-userId/{userId}")
    InfoResponseDto getUserById(
            @RequestHeader("Authorization") String token,
            @PathVariable("userId") Long userId
    );

    @GetMapping("/info/by-login")
    InfoResponseDto getUserByLogin(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "login") String userLogin
    );

}
