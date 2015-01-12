package effortlessenglish.estorm.vn.effortlessenglish.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vinh on 1/9/15.
 */
public class Lession extends Menu {

    public String getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(String largeImage) {
        this.largeImage = largeImage;
    }

    private String largeImage;


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
