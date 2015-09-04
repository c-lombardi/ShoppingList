/**
 * Created by Christopher on 9/1/2015.
 */
public class Program {
    private static Server server;
    public static void main(String argv[])
    {
        int port = Integer.getInteger(argv[0]);
        server = new Server(port); //starts server
    }
}
