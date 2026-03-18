package com.myvisionboard.app.service;

import com.myvisionboard.app.model.Note;
import com.myvisionboard.app.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public Page<Note> findByUserId(String userId, Pageable pageable) {
        return noteRepository.findByUserId(userId, pageable);
    }

    public Page<Note> findByUserIdAndTitle(String userId, String title, Pageable pageable) {
        return noteRepository.findByUserIdAndTitleContainingIgnoreCase(userId, title, pageable);
    }

    public Optional<Note> findById(String id) {
        return noteRepository.findById(id);
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public void deleteById(String id) {
        noteRepository.deleteById(id);
    }
}
