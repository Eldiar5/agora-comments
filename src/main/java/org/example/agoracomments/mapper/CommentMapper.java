package org.example.agoracomments.mapper;

import lombok.RequiredArgsConstructor;
import org.example.agoracomments.client.AgoraApiClient;
import org.example.agoracomments.dto.response.CommentResponseDto;
import org.example.agoracomments.dto.response.InfoResponseDto;
import org.example.agoracomments.entity.Comment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor // Добавили Lombok конструктор для инъекции
public class CommentMapper {

    private final AgoraApiClient agoraApiClient;

    // Добавляем аргумент token, так как FeignClient требует авторизацию
    public CommentResponseDto toDto(Comment comment, String token) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPostId());
        dto.setText(comment.getText());
        dto.setAuthorLogin(comment.getCommentAuthor());
        dto.setCreatedAt(comment.getCreatedAt());

        // Пытаемся получить аватарку
        try {
            if (token != null && !token.isEmpty()) {
                InfoResponseDto user = agoraApiClient.getUserByLogin(token, comment.getCommentAuthor());
                dto.setAuthorAvatarUrl(user.getAvatarUrl());
            }
        } catch (Exception e) {
            // Если сервис недоступен или юзер не найден - просто оставляем null
            // Фронтенд подставит DiceBear
            System.err.println("Не удалось загрузить аватар для комментария: " + e.getMessage());
        }

        return dto;
    }
}