package org.example.agoracomments.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.agoracomments.client.AgoraApiClient;
import org.example.agoracomments.dto.event.CommentCreatedEvent;
import org.example.agoracomments.dto.event.CommentDeletedEvent;
import org.example.agoracomments.dto.response.InfoResponseDto;
import org.example.agoracomments.entity.Comment;
import org.example.agoracomments.exception.handler.CommentNotFound;
import org.example.agoracomments.exception.handler.AgoraAccessException;
import org.example.agoracomments.repository.CommentRepository;
import org.example.agoracomments.service.CommentEventProducer;
import org.example.agoracomments.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AgoraApiClient agoraApiClient;
    private final CommentEventProducer eventProducer;

    @Override
    @Transactional
    public Comment addComment(Long postId, Long authorId, String text, String login, String token, String avatarUrl) {

        // 1. Просто вызываем. Если поста нет -> вылетит FeignException.NotFound -> поймает GlobalExceptionHandler
        agoraApiClient.getPostById(token, postId);

        // 2. Аналогично для юзера
        InfoResponseDto user = agoraApiClient.getUserById(token, authorId);

        // Доп. логика (опционально)
        if (!user.getLogin().equals(login)) {
             throw new AgoraAccessException("Подлог пользователя!"); // Это поймает handleRuntime
        }

        // 3. Сохраняем
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setCommentAuthor(login);
        comment.setText(text);
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        CommentCreatedEvent event = new CommentCreatedEvent();
        event.setCommentId(savedComment.getId());
        event.setPostId(postId);
        event.setAuthorLogin(login);
        event.setCommenterAvatarUrl(avatarUrl);

        // ОТПРАВЛЯЕМ СОБЫТИЕ
        eventProducer.sendCommentCreated(event);

        return savedComment;
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long postId, Long authorId, String authorLogin, String token) {

        agoraApiClient.getPostById(token, postId);

        InfoResponseDto user = agoraApiClient.getUserById(token, authorId);
        if (!user.getLogin().equals(authorLogin)) {
            throw new AgoraAccessException("Подлог пользователя!");
        }

        Comment comment = this.commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFound("Комментарий с ID не найден: " + commentId));

        if (!comment.getCommentAuthor().equals(authorLogin)) {
            throw new AgoraAccessException("Вы не можете удалить чужой комментарий");
        }

        this.commentRepository.delete(comment);

        CommentDeletedEvent event = new CommentDeletedEvent();
        event.setCommentId(commentId);
        event.setPostId(postId);

        // ОТПРАВЛЯЕМ СОБЫТИЕ
        eventProducer.sendCommentDeleted(event);
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFound("Не удалось найти комментарий"));
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
    }

    @Override
    public Map<Long, List<Comment>> getCommentsByPostIds(List<Long> postIds) {
        List<Comment> comments = commentRepository.findAllByPostIdIn(postIds);

        // Группируем список комментариев по postId
        return comments.stream()
                .collect(Collectors.groupingBy(Comment::getPostId));
    }
}
