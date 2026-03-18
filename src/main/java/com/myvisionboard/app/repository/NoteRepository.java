package com.myvisionboard.app.repository;

import com.myvisionboard.app.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface NoteRepository extends JpaRepository<Note, String> {

    Page<Note> findByUserId(String userId, Pageable pageable);
    Page<Note> findByUserIdAndTitleContainingIgnoreCase(String userId, String title, Pageable pageable);
}
