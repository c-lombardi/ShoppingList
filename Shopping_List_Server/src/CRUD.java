/**
 * Created by Christopher on 10/2/2015.
 */
public interface CRUD<T> {
    T create();

    T read();

    T update(final boolean justFlipListActive);

    T delete(final boolean deleteFromLibrary);
}
