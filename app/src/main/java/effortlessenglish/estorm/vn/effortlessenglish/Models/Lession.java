package effortlessenglish.estorm.vn.effortlessenglish.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vinh on 1/9/15.
 */
public class Lession extends Menu {

    private String link;
    private int likes;

    public String getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(String largeImage) {
        this.largeImage = largeImage;
    }

    private String largeImage;
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public int getLikes() {
        return likes;
    }

    @Override
    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Lession(){
        super();
    }

    public Lession(JSONObject jsonObject, Models parent) throws JSONException {
        super(jsonObject,parent);
        if(!jsonObject.isNull("Link")){
            setLink(jsonObject.getString("Link"));
        }
        if(!jsonObject.isNull("Likes")){
            setLikes(jsonObject.getInt("Likes"));
        }
        if(!jsonObject.isNull("SmallImage")){
            setImage(jsonObject.getString("SmallImage"));
        }
        this.setType(TYPE_MODEL.LESSION);
    }
}
