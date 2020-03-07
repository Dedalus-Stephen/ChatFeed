package am.romanbalayan.chatapp.Feed;

class FeedObject {
    private String image;
    private String text;
    private String posterId;



    public FeedObject() {
    }

    public FeedObject(String image, String text, String posterId) {
        this.image = image;
        this.text = text;
        this.posterId = posterId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

}