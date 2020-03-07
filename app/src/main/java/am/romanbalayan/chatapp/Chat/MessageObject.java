package am.romanbalayan.chatapp.Chat;

public class MessageObject {
    private String message;
    private boolean seen;
    private String type;
    private String time;
    private String sender;
    private String date;
    private String receiver;
    private String id;
    private String media;

    public MessageObject(String message, boolean seen, String type, String time, String sender, String date, String receiver, String id, String media) {
        this.message = message;
        this.seen = seen;
        this.type = type;
        this.time = time;
        this.sender = sender;
        this.date = date;
        this.receiver = receiver;
        this.id = id;
        this.media = media;
    }

    public MessageObject() {
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }
}

