package org.example.agoracomments.repository;

import org.example.agoracomments.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long postId);

    List<Comment> findAllByPostIdIn(List<Long> postIds);

    Comment findCommentById(Long commentId);

}
