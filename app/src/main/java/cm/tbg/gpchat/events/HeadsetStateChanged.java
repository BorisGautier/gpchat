package cm.tbg.gpchat.events;



public class HeadsetStateChanged {
    private int state;

    public HeadsetStateChanged(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
