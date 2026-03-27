package com.myvisionboard.app.service;

import com.myvisionboard.app.model.Note;
import com.myvisionboard.app.model.User;
import com.myvisionboard.app.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private Note note;
    private User user;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("user-1")
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .build();

        note = Note.builder()
                .id("note-1")
                .title("My First Note")
                .content("Some content here")
                .user(user)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void findByUserId_ReturnsPageOfNotes() {
        Page<Note> notePage = new PageImpl<>(List.of(note));
        when(noteRepository.findByUserId("user-1", pageable)).thenReturn(notePage);

        Page<Note> result = noteService.findByUserId("user-1", pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo("note-1");
        verify(noteRepository).findByUserId("user-1", pageable);
    }

    @Test
    void findByUserId_WhenNoNotesExist_ReturnsEmptyPage() {
        Page<Note> emptyPage = new PageImpl<>(List.of());
        when(noteRepository.findByUserId("user-1", pageable)).thenReturn(emptyPage);

        Page<Note> result = noteService.findByUserId("user-1", pageable);

        assertThat(result.getTotalElements()).isZero();
        verify(noteRepository).findByUserId("user-1", pageable);
    }

    @Test
    void findByUserIdAndTitle_ReturnsMatchingNotes() {
        Page<Note> notePage = new PageImpl<>(List.of(note));
        when(noteRepository.findByUserIdAndTitleContainingIgnoreCase("user-1", "first", pageable))
                .thenReturn(notePage);

        Page<Note> result = noteService.findByUserIdAndTitle("user-1", "first", pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("My First Note");
        verify(noteRepository).findByUserIdAndTitleContainingIgnoreCase("user-1", "first", pageable);
    }

    @Test
    void findByUserIdAndTitle_WhenNoMatchFound_ReturnsEmptyPage() {
        Page<Note> emptyPage = new PageImpl<>(List.of());
        when(noteRepository.findByUserIdAndTitleContainingIgnoreCase("user-1", "nonexistent", pageable))
                .thenReturn(emptyPage);

        Page<Note> result = noteService.findByUserIdAndTitle("user-1", "nonexistent", pageable);

        assertThat(result.getTotalElements()).isZero();
        verify(noteRepository).findByUserIdAndTitleContainingIgnoreCase("user-1", "nonexistent", pageable);
    }

    @Test
    void findById_WhenNoteExists_ReturnsNote() {
        when(noteRepository.findById("note-1")).thenReturn(Optional.of(note));

        Optional<Note> result = noteService.findById("note-1");

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("My First Note");
        verify(noteRepository).findById("note-1");
    }

    @Test
    void findById_WhenNoteDoesNotExist_ReturnsEmpty() {
        when(noteRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<Note> result = noteService.findById("nonexistent");

        assertThat(result).isEmpty();
        verify(noteRepository).findById("nonexistent");
    }

    @Test
    void save_PersistsAndReturnsNote() {
        when(noteRepository.save(note)).thenReturn(note);

        Note result = noteService.save(note);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("note-1");
        assertThat(result.getTitle()).isEqualTo("My First Note");
        verify(noteRepository).save(note);
    }

    @Test
    void deleteById_DelegatesDeleteToRepository() {
        doNothing().when(noteRepository).deleteById("note-1");

        noteService.deleteById("note-1");

        verify(noteRepository).deleteById("note-1");
    }
}
