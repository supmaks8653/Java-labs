import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    static String[] channels = new String[]{"Мелодрама \"Теорема Пифагора\"","Многосерийный фильм \"Неразрезанные страницы\"","Документальный проект. Тайны космоса","Новости","\"О самом главном\". Ток-шоу"};
    public static void main(String[] args){
        try {
            ServerSocket socket = new ServerSocket(1313);
            Socket client = socket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
            String line = reader.readLine();
            while(!line.equals("end")){
                String toWrite = "err";
                switch(line){
                    case "weather":
                        toWrite = "light snow -3C";
                        break;
                    case "datetime":
                        toWrite = "12:47 14.12.2023";
                        break;
                    case "money":
                        toWrite = "USD sell 3.151 buy 3.158, EUR sell 3.426 buy 3.436, RUB sell 3.506 buy 3.52";
                        break;
                    case "tele":
                        writer.println("ok send channel");
                        writer.flush();
                        String ch = reader.readLine();
                        try {
                            int channel = Integer.parseInt(ch);
                            if(channel-1 > channels.length || channel-1 < 0) throw new NumberFormatException();
                            toWrite = channels[channel-1];
                        } catch (NumberFormatException e){
                            toWrite = "bad channel";
                        }
                        break;
                }
                writer.println(toWrite);
                writer.flush();
                line = reader.readLine();
            }
            writer.println("goodbye");
            writer.flush();
            client.close();
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
