package com.myvisionboard.app.service;

import com.myvisionboard.app.model.Tag;
import com.myvisionboard.app.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Optional<Tag> findById(String id) {
        return tagRepository.findById(id);
    }

    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

    public boolean existsByName(String name) {
        return tagRepository.existsByName(name);
    }

    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    public void deleteById(String id) {
        tagRepository.deleteById(id);
    }
}
