package effortlessenglish.estorm.vn.effortlessenglish.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import effortlessenglish.estorm.vn.effortlessenglish.Adapters.SubMenuItemAdapter;
import effortlessenglish.estorm.vn.effortlessenglish.Base.BaseActivity;
import effortlessenglish.estorm.vn.effortlessenglish.Models.Menu;
import effortlessenglish.estorm.vn.effortlessenglish.Models.SubMenu;
import effortlessenglish.estorm.vn.effortlessenglish.R;
import effortlessenglish.estorm.vn.effortlessenglish.Utils.Constants;
import effortlessenglish.estorm.vn.effortlessenglish.volley.VolleySingleton;

public class SubMenuActivity extends BaseActivity implements ListView.OnItemClickListener {

    private ListView listView;
    private ArrayList<SubMenu> listData;
    private Menu parMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressDialog(true);
        parMenu = new Menu();
        parMenu.setId(getIntent().getIntExtra(Constants.EXTRA_ID, 1));
        String name = getIntent().getStringExtra(Constants.EXTRA_NAME);
        if (name != null)
            parMenu.setName(name);
        setContentView(R.layout.activity_menu);
        getData();
    }

    @Override
    public void setActionView() {
        listView = (ListView) findViewById(R.id.menu_item);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onPlayerChange(String action) {

    }

    @Override
    public void onConnectServcie() {
        if( service != null && service.getCurrentLession() != null && service.getCurrentLession().getId() > 0){
            showController();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(SubMenuActivity.this, LessionActivity.class);
        i.putExtra(Constants.EXTRA_ID,listData.get(position).getId());
        i.putExtra(Constants.EXTRA_NAME,listData.get(position).getName());
        startActivity(i);
    }

    private void getData() {
        if (isNetwork()) {
            VolleySingleton
                    .getInstance()
                    .getRequestQueue()
                    .add(new JsonArrayRequest(
                            Constants.MENU + parMenu.getId(),
                            new Response.Listener<JSONArray>() {

                                @Override
                                public void onResponse(JSONArray data) {
                                    listData = new ArrayList<>();
                                    for (int i = 0; i < data.length(); i++) {
                                        try {
                                            SubMenu menu = new SubMenu(data.getJSONObject(i), parMenu);
                                            listData.add(menu);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    processData();
                                    showProgressDialog(false);
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            showProgressDialog(false);
                            arg0.printStackTrace();
                            showView(R.id.list_no_have_item, true);
                        }
                    }));
        }
    }

    private void processData() {
        if(listData.size() == 0)
            showView(R.id.list_no_have_item,true);
        listView.setAdapter(new SubMenuItemAdapter(this, listData,null));
    }
}
