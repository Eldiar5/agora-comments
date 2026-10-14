package org.example.agoracomments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.agoracomments.dto.event.CommentCreatedEvent;
import org.example.agoracomments.dto.event.CommentDeletedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentEventProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.comments:comments.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing.key.comment-created:comment.created.key}")
    private String createdKey;

    @Value("${rabbitmq.routing.key.comment-deleted:comment.deleted.key}")
    private String deletedKey;

    public void sendCommentCreated(CommentCreatedEvent event) {
        // Отправляем просто ID поста, где нужно увеличить счетчик
        rabbitTemplate.convertAndSend(exchange, createdKey, event);
    }

    public void sendCommentDeleted(CommentDeletedEvent event) {
        rabbitTemplate.convertAndSend(exchange, deletedKey, event);
    }

}
