package cm.tbg.gpchat.model.realms;

public class LogCall {
    private long timeUpdated;
    private int timeDurationSeconds;
    private String status, myId, userId, userName, userImage, userStatus;
    private boolean isVideo;

    public LogCall() {
    }

    public LogCall(User user, long timeUpdated, int timeDurationSeconds, boolean isVideo, String status, String myId) {
        this.timeUpdated = timeUpdated;
        this.timeDurationSeconds = timeDurationSeconds;
        this.isVideo = isVideo;
        this.status = status;
        this.myId = myId;
        this.userId = user.getUid();
        this.userName = user.getUserName();
        this.userImage = user.getPhoto();
        this.userStatus = user.getStatus();
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public long getTimeUpdated() {
        return timeUpdated;
    }

    public int getTimeDuration() {
        return timeDurationSeconds;
    }

    public int getTimeDurationSeconds() {
        return timeDurationSeconds;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMyId() {
        return myId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public String getStatus() {
        return status;
    }
}
