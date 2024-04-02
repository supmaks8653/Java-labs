import java.io.IOException;

public class Main {
    public static void main(String[] args){
        try {
            POP3Client client = new POP3Client(false, "cache.txt");
            //client.disconnect();
            client.connect("127.0.0.1");
            client.login("******@***.***","******");
            client.list();
            client.disconnect();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
