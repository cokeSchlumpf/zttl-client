package com.wellnr.zttl.adapters;

import com.thedeanda.lorem.LoremIpsum;
import com.wellnr.zttl.core.model.Note;
import com.wellnr.zttl.core.model.NoteStatus;
import com.wellnr.zttl.core.ports.NotesRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InMemoryNotesRepository implements NotesRepository {

    private List<Note> notes = IntStream
            .range(1, 20)
            .mapToObj(i -> randomNote())
            .collect(Collectors.toList());

    private Note randomNote() {
        Random rand = new Random();
        String id = UUID.randomUUID().toString();
        String title = LoremIpsum.getInstance().getTitle(3, 10);
        String content = LoremIpsum.getInstance().getParagraphs(2, 5);
        NoteStatus status = NoteStatus.valueOf(NoteStatus.values()[rand.nextInt(2)].name());
        Set<String> tags = new HashSet<>(Arrays.asList(LoremIpsum.getInstance().getWords(1, 5).split(" ")));

        return new Note(id, status, LocalDate.now(), LocalDate.now(), title, tags, content);
    }

    @Override
    public List<Note> getNotes() {
        return notes;
    }

    @Override
    public Set<String> getTags() {
        return notes
                .stream()
                .map(Note::getTags)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public void saveNote(Note note) {

    }

}
