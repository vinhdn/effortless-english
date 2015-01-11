package effortlessenglish.estorm.vn.effortlessenglish.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vinh on 1/9/15.
 */
public class Danhmuc extends Models {
    public Danhmuc(){
        super();
    }

    public Danhmuc(JSONObject jsonObject) throws JSONException {
        if(!jsonObject.isNull("Id")){
            setId(jsonObject.getInt("Id"));
        }else
            return;
        if(!jsonObject.isNull("Description")){
            setDescription(jsonObject.getString("Description"));
        }
        if(!jsonObject.isNull("Name")){
            setDescription(jsonObject.getString("Name"));
        }
        if(!jsonObject.isNull("Image")){
            setImage(jsonObject.getString("Image"));
        }

        setType(TYPE_MODEL.DANHMUC);
    }

    public Danhmuc(int id){
        setId(id);
        switch (id){
            case 1:
                setName("Original Effortless English");
                break;
            case 2:
                setName("Learn Real English");
                break;
            case 3:
                setName("Flow English Lessen");
                break;
            case 4:
                setName("Business Effortless English");
                break;
            case 5:
                setName("Power English Now");
                break;
            case 6:
                setName("Instruction Effortless English");
                break;
        }
    }
}
