package lab6;

import java.util.List;

public class ServerMessage {
    private List<String> serversData;

    ServerMessage(List<String> serversData){
        for (int i = 0; i < serversData.size(); i++){
            System.out.println(serversData.get(i));
        }
        this.serversData = serversData;
    }

    public List<String> getServersData() {
        return this.serversData;
    }
}

