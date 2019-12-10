import java.util.List;

public class ServerMessage {
    private List<String> serversData;

    ServerMessage(List<String> serversData){
        this.serversData = serversData;
    }

    public List<String> getServersData() {
        return serversData;
    }
}

