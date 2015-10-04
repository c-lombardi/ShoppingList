import java.util.List;

/**
 * Created by Christopher on 10/2/2015.
 */
public interface CRUD<T> {
    T create();
    T read();
    List<T> readAll();
    T update(boolean justFlipListActive);
    T delete(boolean deleteFromLibrary);
}
