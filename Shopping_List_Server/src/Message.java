import java.util.List;

/**
 * Created by Christopher on 2/21/2016.
 */
public class Message {
    private Session session;
    private ByteCommand command;
    private Item item;
    private List<Integer> itemIds;

    public Session getSession() {
        return session;
    }

    public void setSession(final Session session) {
        this.session = session;
    }

    public ByteCommand getCommand() {
        return command;
    }

    public void setCommand(final ByteCommand command) {
        this.command = command;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(final Item item) {
        this.item = item;
    }

    public List<Integer> getItemIds() {
        return itemIds;
    }

    public void setItemIds(final List<Integer> itemIds) {
        this.itemIds = itemIds;
    }
}
