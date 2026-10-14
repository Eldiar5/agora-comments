package org.example.agoracomments.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.agoracomments.configuration.swagger.doc.*;
import org.example.agoracomments.dto.request.CommentRequestDto;
import org.example.agoracomments.dto.response.CommentResponseDto;
import org.example.agoracomments.entity.Comment;
import org.example.agoracomments.mapper.CommentMapper;
import org.example.agoracomments.service.impl.CommentServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comments API", description = "API для создания, получения и удаления комментариев (Требует JWT токен)")
@ApiErrorResponse400
@ApiErrorResponse401
@ApiErrorResponse404
@ApiErrorResponse500
public class CommentController {

    private final CommentServiceImpl commentService;
    private final CommentMapper commentMapper;

    @PostMapping
    @Operation(summary = "Оставить комментарий", description = "Оставить комментарий к посту (Нужен ID поста)")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentResponseDto> createComment(
            @RequestParam(name = "postId") Long postId,
            @RequestHeader("X-User-Id") Long userId,      // От Gateway (извлечено из токена)
            @RequestHeader("X-User-Name") String login, // От Gateway (извлечено из токена)
            @RequestHeader(value = "X-User-Avatar", required = false) String avatarUrl,
            @RequestHeader("Authorization") String token, // 👇 Сам сырой токен (Bearer ...)
            @RequestBody CommentRequestDto requestDto
    ) {
        // Передаем токен дальше по цепочке
        Comment comment = commentService.addComment(postId, userId, requestDto.getText(), login, token, avatarUrl);

        return ResponseEntity.ok(commentMapper.toDto(comment, token));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Удалить свой комментарий", description = "Удаляет комментарий по ID, если он принадлежит аутентифицированному пользователю")
    @ApiErrorResponse403
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<CommentResponseDto> deleteComment(
            @PathVariable Long commentId,
            @RequestParam(name = "postId") Long postId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String login,
            @RequestHeader("Authorization") String token
    ) {
        commentService.deleteComment(commentId, postId, userId, login, token);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    @Operation(summary = "Получить комментарии по списку постов", description = "Возвращает все комментарии по переданному списку из ID постов")
    @ApiResponse(
            responseCode = "200",
            description = "Успешный ответ",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CommentResponseDto.class)))
    )
    public ResponseEntity<Map<Long, List<CommentResponseDto>>> getCommentsBatch(
            @RequestBody List<Long> postIds,
            @RequestHeader("Authorization") String token
    ) {
        // 1. Получаем сгруппированные сущности из сервиса
        Map<Long, List<Comment>> commentsMap = commentService.getCommentsByPostIds(postIds);

        // 2. Преобразуем сущности в DTO
        Map<Long, List<CommentResponseDto>> responseMap = commentsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(comment ->  commentMapper.toDto(comment, token))
                                .toList()
                ));

        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Найти комментарий ID", description = "Возвращает комментарий, ID которого совпадает с переданным")
    @ApiResponse(
            responseCode = "200",
            description = "Успешный ответ",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CommentResponseDto.class))
    )
    public ResponseEntity<CommentResponseDto> getCommentById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok(commentMapper.toDto(comment, token));
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "Получить комментарии по ID поста", description = "Возвращает все комментарии по переданному ID поста")
    @ApiResponse(
            responseCode = "200",
            description = "Успешный ответ",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CommentResponseDto.class)))
    )
    public ResponseEntity<List<CommentResponseDto>> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token
    ) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);

        List<CommentResponseDto> dtos = comments.stream()
                .map(comment -> commentMapper.toDto(comment, token))
                .toList();

        return ResponseEntity.ok(dtos);
    }

}
