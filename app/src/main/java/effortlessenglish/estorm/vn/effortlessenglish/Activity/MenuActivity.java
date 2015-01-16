package effortlessenglish.estorm.vn.effortlessenglish.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import effortlessenglish.estorm.vn.effortlessenglish.Adapters.MenuItemAdapter;
import effortlessenglish.estorm.vn.effortlessenglish.Base.BaseActivity;
import effortlessenglish.estorm.vn.effortlessenglish.Models.Danhmuc;
import effortlessenglish.estorm.vn.effortlessenglish.Models.Menu;
import effortlessenglish.estorm.vn.effortlessenglish.Models.Models;
import effortlessenglish.estorm.vn.effortlessenglish.R;
import effortlessenglish.estorm.vn.effortlessenglish.Utils.Constants;
import effortlessenglish.estorm.vn.effortlessenglish.volley.VolleySingleton;

public class MenuActivity extends BaseActivity implements ListView.OnItemClickListener {

    private ListView listView;
    private ArrayList<Models> listData;
    private Danhmuc danhmuc;
    private MenuItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressDialog(true);
        if (Constants.selectedModel != null && Constants.selectedModel.getId() == getIntent().getIntExtra(Constants.EXTRA_ID, 0)) {
            danhmuc = new Danhmuc(Constants.selectedModel);
        } else
            danhmuc = new Danhmuc();
        setContentView(R.layout.activity_menu);
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.selectedModel = danhmuc;
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
        if (service != null && service.getCurrentLession() != null && service.getCurrentLession().getId() > 0) {
            showController();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(MenuActivity.this, SubMenuActivity.class);
        Constants.selectedModel = listData.get(position);
        i.putExtra(Constants.EXTRA_ID, listData.get(position).getId());
        i.putExtra(Constants.EXTRA_NAME, listData.get(position).getName());
        startActivity(i);
    }

    private void getData() {
        if (isNetwork()) {
            VolleySingleton
                    .getInstance()
                    .getRequestQueue()
                    .add(new JsonArrayRequest(
                            Constants.DANHMUC + danhmuc.getId(),
                            new Response.Listener<JSONArray>() {

                                @Override
                                public void onResponse(JSONArray data) {
                                    listData = new ArrayList<>();
                                    for (int i = 0; i < data.length(); i++) {
                                        try {
                                            Menu menu = new Menu(data.getJSONObject(i), danhmuc);
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
        } else {
            getOffline();
        }
    }

    private void getOffline() {
        try {
            listData = mApp.getLocalStorage().getListsModelsOfParent(danhmuc, Models.TYPE_MODEL.MENU.getValue());
            processData();
            showProgressDialog(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            showProgressDialog(false);
            showView(R.id.list_no_have_item, true);
        }
    }

    private void processData() {
        if (listData.size() == 0)
            showView(R.id.list_no_have_item, true);
        adapter = new MenuItemAdapter(this, listData);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
        swingBottomInAnimationAdapter.setAbsListView(listView);
        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        listView.setAdapter(swingBottomInAnimationAdapter);
    }

}
