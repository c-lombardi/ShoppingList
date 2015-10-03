import java.util.List;

/**
 * Created by Christopher on 10/2/2015.
 */
public interface CRUD<T> {
    void create();
    void read();
    List<T> readAll();
    void update(boolean justFlipListActive);
    void delete(boolean deleteFromLibrary);
}
