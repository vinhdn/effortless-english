package effortlessenglish.estorm.vn.effortlessenglish.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vinh on 1/9/15.
 */
public class Menu extends Models {
    public Menu(){
        super();
    }

    public Menu(JSONObject jsonObject, Models parent) throws JSONException {
        if(!jsonObject.isNull("Id")){
            setId(jsonObject.getInt("Id"));
        }else
            return;
        if(!jsonObject.isNull("Description")){
            setDescription(jsonObject.getString("Description"));
        }
        if(!jsonObject.isNull("Name")){
            setName(jsonObject.getString("Name"));
        }
        if(parent != null){
            setParent(parent);
            setIdParMenu(parent.getId());
            setNameParMenu(parent.getName());
        }
        setType(TYPE_MODEL.MENU);
    }
    public Menu(Models models){
        super(models);
    }
}
