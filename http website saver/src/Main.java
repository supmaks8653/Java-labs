import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Main {
    public static void main(String[] args){
        try {
            Connector c = new Connector(args[0], true);
            System.out.println("Connecting to "+args[0]);
            c.println("GET / HTTP/1.1\nHost: "+args[0]);
            String result = c.read();
            if(result.charAt(9) != '2'){
                System.out.println("The site returned an error - " + result.substring(9));
                c.disconnect();
                return;
            }
            while(!result.isEmpty()){
                result = c.read();
            }
            result = c.read();
            File f = new File("index.html");
            if(!f.exists()) f.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter("index.html"));
            while(!result.isEmpty()) {
                bw.write(result+'\n');
                result = c.read();
            }
            bw.close();
            c.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
