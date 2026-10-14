package org.example.agoracomments.exception.handler;

import feign.FeignException;
import org.example.agoracomments.dto.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Ловим 404 от Feign (Пост или Юзер не найдены в Agora-API)
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorResponseDto> handleFeignNotFound(FeignException.NotFound ex) {
        // Можно попытаться распарсить тело ответа от API, но для простоты вернем свое сообщение
        String message = "Запрашиваемый ресурс не найден во внешнем сервисе (Agora API)";
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(message));
    }

    // 2. Ловим 403/401 от Feign (Если токен протух или невалиден)
    @ExceptionHandler({FeignException.Forbidden.class, FeignException.Unauthorized.class})
    public ResponseEntity<ErrorResponseDto> handleFeignAuth(FeignException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDto("Ошибка доступа к внешнему сервису (Невалидный токен)"));
    }

    // 3. Ловим остальные ошибки Feign (например, 500 от API)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponseDto> handleGenericFeign(FeignException ex) {
        return ResponseEntity
                .status(ex.status() > 0 ? ex.status() : 500) // Feign иногда возвращает -1
                .body(new ErrorResponseDto("Ошибка взаимодействия с внешним сервисом: " + ex.getMessage()));
    }

    @ExceptionHandler(AgoraAccessException.class)
    public  ResponseEntity<ErrorResponseDto> handleAgoraAccess(AgoraAccessException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(CommentNotFound.class)
    public ResponseEntity<ErrorResponseDto> handleCommentNotFound(CommentNotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    // 4. Ловим обычные RuntimeException (твоя страховка)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

}
