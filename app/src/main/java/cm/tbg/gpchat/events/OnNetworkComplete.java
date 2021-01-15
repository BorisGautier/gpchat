package cm.tbg.gpchat.events;


public class OnNetworkComplete {
    private String id;

    public OnNetworkComplete(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
