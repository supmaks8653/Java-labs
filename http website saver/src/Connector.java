import java.io.*;
import java.net.Socket;

public class Connector {
    Socket socket;
    BufferedReader reader;
    PrintWriter writer;
    boolean debug;
    public Connector(String address, boolean debug) throws IOException {
        socket = new Socket(address,80);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.debug = debug;
    }
    public void disconnect() throws IOException{
        socket.close();
        reader = null;
        writer = null;
    }
    public void println(String message){
        if(debug) System.out.println("sent " + message);
        writer.println(message);
        writer.println("");
        writer.flush();
    }
    public String read() throws IOException{
        String result = reader.readLine();
        if(debug) System.out.println("received " + result);
        return result;
    }
}
