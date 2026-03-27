package com.myvisionboard.app.service;

import com.myvisionboard.app.model.Tag;
import com.myvisionboard.app.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = Tag.builder()
                .id("tag-1")
                .name("work")
                .build();
    }

    @Test
    void findAll_ReturnsAllTags() {
        Tag tag2 = Tag.builder().id("tag-2").name("personal").build();
        when(tagRepository.findAll()).thenReturn(List.of(tag, tag2));

        List<Tag> result = tagService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Tag::getName).containsExactly("work", "personal");
        verify(tagRepository).findAll();
    }

    @Test
    void findAll_WhenNoTagsExist_ReturnsEmptyList() {
        when(tagRepository.findAll()).thenReturn(List.of());

        List<Tag> result = tagService.findAll();

        assertThat(result).isEmpty();
        verify(tagRepository).findAll();
    }

    @Test
    void findById_WhenTagExists_ReturnsTag() {
        when(tagRepository.findById("tag-1")).thenReturn(Optional.of(tag));

        Optional<Tag> result = tagService.findById("tag-1");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("work");
        verify(tagRepository).findById("tag-1");
    }

    @Test
    void findById_WhenTagDoesNotExist_ReturnsEmpty() {
        when(tagRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<Tag> result = tagService.findById("nonexistent");

        assertThat(result).isEmpty();
        verify(tagRepository).findById("nonexistent");
    }

    @Test
    void findByName_WhenTagExists_ReturnsTag() {
        when(tagRepository.findByName("work")).thenReturn(Optional.of(tag));

        Optional<Tag> result = tagService.findByName("work");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("tag-1");
        verify(tagRepository).findByName("work");
    }

    @Test
    void findByName_WhenTagDoesNotExist_ReturnsEmpty() {
        when(tagRepository.findByName("unknown")).thenReturn(Optional.empty());

        Optional<Tag> result = tagService.findByName("unknown");

        assertThat(result).isEmpty();
        verify(tagRepository).findByName("unknown");
    }

    @Test
    void existsByName_WhenNameExists_ReturnsTrue() {
        when(tagRepository.existsByName("work")).thenReturn(true);

        boolean result = tagService.existsByName("work");

        assertThat(result).isTrue();
        verify(tagRepository).existsByName("work");
    }

    @Test
    void existsByName_WhenNameDoesNotExist_ReturnsFalse() {
        when(tagRepository.existsByName("new-tag")).thenReturn(false);

        boolean result = tagService.existsByName("new-tag");

        assertThat(result).isFalse();
        verify(tagRepository).existsByName("new-tag");
    }

    @Test
    void save_PersistsAndReturnsTag() {
        when(tagRepository.save(tag)).thenReturn(tag);

        Tag result = tagService.save(tag);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("tag-1");
        assertThat(result.getName()).isEqualTo("work");
        verify(tagRepository).save(tag);
    }

    @Test
    void deleteById_DelegatesDeleteToRepository() {
        doNothing().when(tagRepository).deleteById("tag-1");

        tagService.deleteById("tag-1");

        verify(tagRepository).deleteById("tag-1");
    }
}
