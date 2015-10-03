import java.util.ArrayList;

/**
 * Created by Christopher on 10/2/2015.
 */
public interface CRUD<T> {
    void Create();
    void Read();
    ArrayList<T> ReadAll();
    void Update(boolean justFlipListActive);
    void Delete(boolean deleteFromLibrary);
}
