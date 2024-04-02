import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args){
        try {
            Socket s = new Socket("127.0.0.1", 1313);
            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            String input = console.readLine();
            writer.println(input);
            writer.flush();
            String response = reader.readLine();
            System.out.println(response);
            while(!response.equals("goodbye")){
                input = console.readLine();
                writer.println(input);
                writer.flush();
                response = reader.readLine();
                System.out.println(response);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
