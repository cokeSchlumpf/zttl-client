package com.wellnr.zttl.adapters;

import com.thedeanda.lorem.LoremIpsum;
import com.wellnr.zttl.core.model.Note;
import com.wellnr.zttl.core.model.NoteStatus;
import com.wellnr.zttl.core.ports.NotesRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InMemoryNotesRepository implements NotesRepository {

   private Map<String, Note> notes = IntStream
      .range(1, 20)
      .mapToObj(i -> randomNote())
      .collect(Collectors.toMap(Note::getId, note -> note));

   private Note randomNote() {
      Random rand = new Random();
      String id = UUID.randomUUID().toString();
      String title = LoremIpsum.getInstance().getTitle(3, 10);
      String content = LoremIpsum.getInstance().getParagraphs(2, 5);
      NoteStatus status = NoteStatus.valueOf(NoteStatus.values()[rand.nextInt(2) + 1].name());
      Set<String> tags = new HashSet<>(Arrays.asList(LoremIpsum.getInstance().getWords(1, 5).split(" ")));

      return new Note(id, status, LocalDate.now().atStartOfDay(), LocalDate.now().atStartOfDay(), title, tags, content);
   }

   @Override
   public Note createNewNote() {
      String id = UUID.randomUUID().toString();
      String title = "New Note";
      String content = "";
      NoteStatus status = NoteStatus.NEW;
      Set<String> tags = new HashSet<>();

      return new Note(id, status, LocalDateTime.now(), LocalDateTime.now(), title, tags, content);
   }

   @Override
   public List<Note> findNotesByTitle(String query) {
      return notes
         .values()
         .stream()
         .filter(n -> n.getTitle().contains(query))
         .collect(Collectors.toList());
   }

   @Override
   public Optional<Note> getNoteById(String id) {
      return notes
         .values()
         .stream()
         .filter(n -> n.getId().equals(id))
         .findFirst();
   }

   @Override
   public List<Note> getNotes() {
      return new ArrayList<>(notes.values());
   }

   @Override
   public Set<String> getTags() {
      return notes
         .values()
         .stream()
         .map(Note::getTags)
         .flatMap(Collection::stream)
         .collect(Collectors.toSet());
   }

   @Override
   public Note saveNote(Note note) {
      notes.put(note.getId(), note);
      return note;
   }

}
