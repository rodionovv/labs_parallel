import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;
import scala.Int;

import java.io.File;
import java.io.FileReader;
import java.util.HashSet;

public class RequestsThread extends Thread{


    private final CloseableHttpClient httpClient;
    private final File folder;

    private static final String URL = "http://localhost:8080/";
    private static final String PARAMETR = "?packageID=";

    RequestsThread(String path) {
        this.httpClient = HttpClients.createDefault();
        this.folder = new File(path);
    }

    @Override
    public void run() {
        try {
            sleep(5000);
        } catch (InterruptedException e){}
        for (final File file : folder.listFiles()){
            try {
                sleep(1000);
                sendPost(file.getName());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            HashSet<Integer> packageIdList = Functions.getPackageIdList();
            for (Integer packageId : packageIdList) {
                String result = sendGet(packageId);
                System.out.println(result);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void sendPost(String fileName) throws Exception {

        String result = "";
        HttpPost post = new HttpPost(URL);
        JSONParser jsonParser = new JSONParser();
        try(FileReader reader = new FileReader(folder.toString() + "/" + fileName)){
            Object obj = jsonParser.parse(reader);

            StringEntity requestEntity  = new StringEntity(
                    obj.toString(),
                    ContentType.APPLICATION_JSON
            );
            post.setEntity(new StringEntity(obj.toString()));
            post.setEntity(requestEntity);
            try (CloseableHttpResponse response = httpClient.execute(post)){
                result = EntityUtils.toString(response.getEntity());
                System.out.println(result);
            }
        }

    }

    private String sendGet(int packageID) throws Exception {
        String result = "";
        HttpGet request = new HttpGet(URL + PARAMETR + packageID);
        try (CloseableHttpResponse response = this.httpClient.execute(request)){
            HttpEntity entity = response.getEntity();
            if (entity != null){
                result = EntityUtils.toString(entity);
            }
        }
        return result;
    }

}
