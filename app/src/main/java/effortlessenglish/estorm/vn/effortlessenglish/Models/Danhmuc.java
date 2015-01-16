package effortlessenglish.estorm.vn.effortlessenglish.Models;

import org.json.JSONException;
import org.json.JSONObject;

import effortlessenglish.estorm.vn.effortlessenglish.R;

/**
 * Created by Vinh on 1/9/15.
 */
public class Danhmuc extends Models {

    public int getIntroduce() {
        return introduce;
    }

    public void setIntroduce(int introduce) {
        this.introduce = introduce;
    }

    private int introduce;

    public Danhmuc(){
        super();
    }

    public Danhmuc(Models models){
        super(models);
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

        setType(TYPE_MODEL.DANHMUC);
    }

    public Danhmuc(int id){
        super();
        setType(TYPE_MODEL.DANHMUC);
        setId(id);
        switch (id){
            case 1:
                setName("Original Effortless English");
                setImage(R.drawable.originalenglish);
                setIntroduce(R.string.gioithieuoriginal);
                break;
            case 2:
                setName("Learn Real English");
                setImage(R.drawable.learnicon);
                setIntroduce(R.string.gioithieulearn);
                break;
            case 3:
                setName("Flow English Lessen");
                setImage(R.drawable.flowicon);
                setIntroduce(R.string.gioithieuflow);
                break;
            case 4:
                setName("Business Effortless English");
                setImage(R.drawable.bussinesicon);
                setIntroduce(R.string.gioithieubussiness);
                break;
            case 5:
                setName("Power English Now");
                setImage(R.drawable.powericon);
                setIntroduce(R.string.gioithieupower);
                break;
            case 6:
                setName("Instruction Effortless English");
                setImage(R.drawable.introduceicon);
                setIntroduce(R.string.gioithieuchungngan);
                break;
        }
    }
}
