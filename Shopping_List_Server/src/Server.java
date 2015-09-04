import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Christopher on 9/2/2015.
 */
public class Server implements Runnable {

    private int Port;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream streamIn = null;

    public Server(int port) {
        Port = port;
        try
        {  System.out.println("Binding to port " + port + ", please wait  ...");
            serverSocket = new ServerSocket(port);
            start();
        }
        catch(IOException ioe)
        {
            System.out.println(ioe);
        }
    }
    @Override
    public void run() {
        while(thread != null) {
            try
            {
                socket = serverSocket.accept();
                open();
                boolean done = false;
                while (!done)
                {
                    try (Database db = new Database())
                    {
                        String line = streamIn.readUTF();
                        System.out.println(line);
                        done = line.equals(".bye");
                    }
                    catch(Exception ioe)
                    {
                        done = true;
                    }
                }
                close();
            }
            catch(IOException ie)
            {
            }
        }
    }

    public void start()
    {
        if (thread == null)
        {  thread = new Thread(this);
            thread.start();
        }
    }
    public void stop()
    {
        if (thread != null)
        {
            thread.stop();
            thread = null;
        }
    }
    public void open() throws IOException
    {
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }
    public void close() throws IOException
    {
        if (socket != null)
            socket.close();
        if (streamIn != null)
            streamIn.close();
    }
}
