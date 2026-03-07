
import java.io.*;
import java.util.UUID;

public class LibraryService {

    private static final String FILE = "library.dat";

    private Library library;

    public LibraryService() {
        library = loadLibrary();
    }

    public Library getLibrary() {
        return library;
    }

    public void addSong(Song song) {
        library.addSong(song);
        saveLibrary();
    }

    private void saveLibrary() {
        try (ObjectOutputStream out
                = new ObjectOutputStream(new FileOutputStream(FILE))) {

            out.writeObject(library);
            System.out.println(new File("library.dat").getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Library loadLibrary() {

        try (ObjectInputStream in
                = new ObjectInputStream(new FileInputStream(FILE))) {

            return (Library) in.readObject();

        } catch (Exception e) {
            return new Library();
        }
    }

    public void deleteSong(UUID id) {

        library.removeSong(id);

        saveLibrary();
    }
}
