package com.wellnr.zttl.adapters;

import com.wellnr.zttl.core.model.Note;
import com.wellnr.zttl.core.model.NoteStatus;
import com.wellnr.zttl.core.ports.NotesRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilesystemNotesRepository implements NotesRepository {

   private final Path archiveDir;

   private final Path inboxDir;

   private final Pattern fileNamePattern;

   private FilesystemNotesRepository(Path archiveDir, Path inboxDir) {
      this.archiveDir = archiveDir;
      this.inboxDir = inboxDir;
      this.fileNamePattern = Pattern.compile("(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)-(\\d\\d)(\\d\\d)\\.md");
   }

   public static FilesystemNotesRepository apply(Path workDirectory) {
      try {
         Path archive = workDirectory.resolve("archive");
         Path inbox = workDirectory.resolve("inbox");
         Files.createDirectories(archive);
         Files.createDirectories(inbox);

         return new FilesystemNotesRepository(archive, inbox);
      } catch (IOException e) {
         throw new RuntimeException(e); // TODO mw: Improve error handling
      }
   }

   @Override
   public Note createNewNote() {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmm");
      String id = LocalDateTime.now().format(formatter);
      String title = "New Note";
      String content = "Enter some great thoughts here";
      NoteStatus status = NoteStatus.NEW;
      Set<String> tags = new HashSet<>();

      return new Note(id, status, LocalDateTime.now(), LocalDateTime.now(), title, tags, content);
   }

   @Override
   public List<Note> findNotesByTitle(String query) {
      // TODO
      return List.of();
   }

   @Override
   public Optional<Note> getNoteById(String id) {
      String name = id + ".md";
      return fromFile(archiveDir.resolve(name)).or(() -> fromFile(inboxDir.resolve(name)));
   }

   @Override
   public List<Note> getNotes() {
      try (
         Stream<Path> archive = Files.walk(archiveDir);
         Stream<Path> inbox = Files.walk(inboxDir)) {

         return Stream
            .concat(archive, inbox)
            .map(this::fromFile)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Set<String> getTags() {
      return getNotes()
         .stream()
         .map(Note::getTags)
         .flatMap(Collection::stream)
         .collect(Collectors.toSet());
   }

   @Override
   public void deleteNote(Note note) {
      try {
         String fileName = note.getId() + ".md";

         switch (note.getStatus()) {
            case NEW:
               break;
            case INBOX:
               Files.deleteIfExists(inboxDir.resolve(fileName));
               break;
            case ARCHIVED:
               Files.deleteIfExists(archiveDir.resolve(fileName));
               break;
         }
      } catch (Exception e) {
         // TODO mw: Better Exception Handling
         throw new RuntimeException(e);
      }
   }

   @Override
   public Note saveNote(Note note) {
      try {
         Path parent = inboxDir;
         String fileName = note.getId() + ".md";

         switch (note.getStatus()) {
            case NEW:
               note = note.withStatus(NoteStatus.INBOX);
            case INBOX:
               Files.deleteIfExists(archiveDir.resolve(fileName));
               break;
            case ARCHIVED:
               parent = archiveDir;
               Files.deleteIfExists(inboxDir.resolve(fileName));
               break;
         }

         Files.writeString(parent.resolve(fileName), toMarkdown(note));
         return fromFile(parent.resolve(fileName)).orElse(note);
      } catch (Exception e) {
         // TODO mw: Better Exception Handling
         throw new RuntimeException(e);
      }
   }

   private String toMarkdown(Note note) {
      return "# " + note.getId() + " " + note.getTitle() + "\n" +
         "\n" +
         note.getTags().stream().map(s -> "§" + s).collect(Collectors.joining(" ")) +
         "\n\n" +
         "---" +
         "\n\n" +
         note.getContent();
   }

   private Optional<Note> fromFile(Path file) {
      Matcher matcher = fileNamePattern.matcher(file.getFileName().toString());
      if (!matcher.matches()) {
         return Optional.empty();
      }

      if (!Files.exists(file)) {
         return Optional.empty();
      }

      try {
         String id = matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3) + "-" + matcher.group(4) + matcher.group(5);

         LocalDateTime created = LocalDateTime.of(
            Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)),
            Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5)));
         LocalDateTime updated = LocalDateTime.ofInstant(
            Files.getLastModifiedTime(file).toInstant(), ZoneId.systemDefault());

         List<String> fileContent = Files.readAllLines(file);
         String title = fileContent.get(0).substring(18);

         String content = "";
         if (fileContent.size() >= 7) {
            content = String.join("\n", fileContent.subList(6, fileContent.size())).trim();
         }

         NoteStatus status = NoteStatus.NEW;
         if (file.toAbsolutePath().toString().startsWith(archiveDir.toAbsolutePath().toString())) {
            status = NoteStatus.ARCHIVED;
         } else if (file.toAbsolutePath().toString().startsWith(inboxDir.toAbsolutePath().toString())) {
            status = NoteStatus.INBOX;
         }

         Set<String> tags = new HashSet<>(Arrays.asList(fileContent.get(2).replaceAll("§", "").split(" ")));

         return Optional.of(new Note(id, status, created, updated, title, tags, content));
      } catch (Exception e) {
         e.printStackTrace();
         return Optional.empty();
      }
   }

}
