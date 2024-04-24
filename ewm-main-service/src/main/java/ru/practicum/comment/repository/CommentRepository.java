package ru.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.entity.Comment;
import ru.practicum.event.entity.Event;
import ru.practicum.user.entity.User;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findCommentsByEvent(Event event);

    List<Comment> findCommentsByAuthor(User author);
}
