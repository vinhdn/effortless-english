package effortlessenglish.estorm.vn.effortlessenglish.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vinh on 1/9/15.
 */
public class SubMenu extends Menu {

    public SubMenu(){
        super();
    }

    public SubMenu(Models models){
        super(models);
    }

    public SubMenu(JSONObject jsonObject, Models sub) throws JSONException {
        super(jsonObject,sub);
        setType(TYPE_MODEL.SUBMENU);
    }
}
