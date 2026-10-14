package org.example.agoracomments.service;

import org.example.agoracomments.entity.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService {

    Comment addComment(Long postId, Long authorId, String text, String username, String token, String avatarUrl);

    void deleteComment(Long commentId, Long postId, Long authorId, String authorLogin, String token);

    Comment getCommentById(Long commentId);

    List<Comment> getCommentsByPostId(Long postId);

    Map<Long, List<Comment>> getCommentsByPostIds(List<Long> postIds);

}
