import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class POP3Client {
    private Socket socket;

    private BufferedReader reader;
    private BufferedWriter writer;

    public final boolean debug;
    private final CacheManager cacheManager;

    private static final int DEFAULT_PORT = 110;

    public POP3Client(boolean _debug, String cacheFileName) throws IOException{
        debug =  _debug;
        cacheManager = new CacheManager(cacheFileName);
    }

    public void connect(String host, int port) throws IOException {
        if(debug) System.out.println("Going to connect to " + host +":" + port);
        socket = new Socket();
        socket.connect(new InetSocketAddress(host,port));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        readResponseLine();
        System.out.println("Connected");
    }
    public void connect(String host) throws IOException{
        if(debug) System.out.println("Using default port");
        connect(host, DEFAULT_PORT);
    }
    public boolean isConnected(){
        return socket!=null && socket.isConnected();
    }
    public void disconnect() throws IOException{
        if(!isConnected()) throw new IllegalStateException("Not connected, so cannot disconnect");
        logout();
        socket.close();
        writer = null;
        reader = null;
        System.out.println("Disconnected");
        cacheManager.save();
    }
    protected String readResponseLine() throws IOException{
        String response = reader.readLine();
        if(response !=null) {
            if (debug) System.out.println("DEBUG [in] : " + response);
            if (response.startsWith("-ERR")) throw new RuntimeException("Server has returned an error: " + response.replaceFirst("-ERR ", ""));
        }
        return response;
    }
    protected boolean readUntilCant(int messageNumber) throws IOException{
        String response = readResponseLine();
        String toPrint = "";
        while (!response.equals(".")){
            if(response.startsWith("Subject: ") || response.startsWith("From: ")) toPrint += response + "\n";
            response = readResponseLine();
        }
        String messageId = getId(messageNumber);
        boolean result = !cacheManager.alreadyRead(messageId);
        if(result){
            System.out.println(toPrint);
            cacheManager.addId(messageId);
        }
        return result;
    }
    protected String sendCommand(String command) throws IOException {
        if (debug) System.out.println("DEBUG [out]: " + command)    ;
        writer.write(command + "\n");
        writer.flush();
        return readResponseLine();
    }
    public void login(String username, String password) throws IOException{
        sendCommand("USER "+username);
        sendCommand("PASS "+password);
        System.out.println("Logged in");
    }
    public void logout() throws IOException{
        sendCommand("QUIT");
    }
    public void stat() throws IOException{
        sendCommand("STAT");
    }
    public void list() throws IOException{
        String response = sendCommand("LIST");
        int amountOfMessages = Integer.parseInt(response.split(" ")[1]);
        for(int i = 0;i<amountOfMessages;i++) readResponseLine();
        boolean readAny = false;
        for(int i = 0;i<amountOfMessages;i++){
            sendCommand("TOP "+ (i+1) + " 0");
            if(readUntilCant(i+1)) readAny = true;
        }
        if(!readAny) System.out.println("No new messages");
    }
    protected String getId(int messageNumber) throws IOException{
        return sendCommand("UIDL " + (messageNumber)).split(" ")[2];
    }
}
