package am.romanbalayan.chatapp.User;

import android.os.Parcel;
import android.os.Parcelable;

public class UserObject  {
    private String id;
    private String name;
    private String image;
    private String thumb;
    private boolean isPending;

    public UserObject() {

    }


    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
