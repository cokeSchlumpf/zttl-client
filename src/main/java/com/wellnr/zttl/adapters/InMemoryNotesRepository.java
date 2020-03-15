package com.wellnr.zttl.adapters;

import com.thedeanda.lorem.LoremIpsum;
import com.wellnr.zttl.core.model.Note;
import com.wellnr.zttl.core.model.NoteStatus;
import com.wellnr.zttl.core.ports.NotesRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InMemoryNotesRepository implements NotesRepository {

    private Note randomNote() {
        Random rand = new Random();
        String id = UUID.randomUUID().toString();
        String title = LoremIpsum.getInstance().getTitle(3, 10);
        String content = LoremIpsum.getInstance().getParagraphs(2, 5);
        NoteStatus status = NoteStatus.valueOf(NoteStatus.values()[rand.nextInt(2)].name());

        return new Note(id, status, LocalDate.now(), LocalDate.now(), title, content);
    }

    @Override
    public List<Note> getNotes() {
        return IntStream
                .range(1, 20)
                .mapToObj(i -> randomNote())
                .collect(Collectors.toList());
    }

    @Override
    public void saveNote(Note note) {

    }

}
