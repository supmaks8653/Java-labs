import java.io.*;
import java.util.Arrays;
import java.util.HashSet;

public class CacheManager {
    String cacheFileName;
    HashSet<String> alreadyReadMessages = new HashSet<>();
    public CacheManager(String cfn) throws IOException{
        cacheFileName = cfn;
        load();
    }
    private void load() throws IOException {
        File f = new File(cacheFileName);
        if(!f.exists()) f.createNewFile();
        BufferedReader br = new BufferedReader(new FileReader(cacheFileName));
        String res = br.readLine();
        br.close();
        if(res == null) return;
        String[] ids = res.split(" ");
        alreadyReadMessages.addAll(Arrays.asList(ids));
    }
    public void addId(String id){
        alreadyReadMessages.add(id);
    }
    public void save() throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(cacheFileName));
        for(String i : alreadyReadMessages) bw.write(i+" ");
        bw.close();
    }
    public boolean alreadyRead(String id){
        return alreadyReadMessages.contains(id);
    }
}
