import java.util.List;

/**
 * Created by Christopher on 2/21/2016.
 */
public class Message {
    private Session session;
    private ByteCommand command;
    private Item item;
    private List<Item> items;
    private ShoppingList shopping_list;

    public ShoppingList getShopping_list() {
        return shopping_list;
    }

    public void setShopping_list(ShoppingList sl) {
        shopping_list = sl;
    }

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

    public List<Item> getItems() {
        return items;
    }

    public void setItemIds(final List<Item> items) {
        this.items = items;
    }
}
