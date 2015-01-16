package effortlessenglish.estorm.vn.effortlessenglish.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import effortlessenglish.estorm.vn.effortlessenglish.Models.Models;
import effortlessenglish.estorm.vn.effortlessenglish.R;
import effortlessenglish.estorm.vn.effortlessenglish.Services.PlayerService;

/**
 * Created by Vinh on 1/9/15.
 */
public class ListLessionFragment extends Fragment {

    private Activity mActivity;
    private PlayerService service;
    public  ListLessionFragment(){

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    public static ListLessionFragment create(PlayerService service){
        ListLessionFragment fragment = new ListLessionFragment();
        fragment.service = service;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.layout_list_lession, container, false);
        if(service == null)
            return rootView;
        Models model = service.getCurrentLession().getParent();
        String[] parentItem = {"","",""};
        int id = 2;
        while (model != null && model.getId() > 0 && id >= 0) {
            parentItem[id] = model.getName();
            model = model.getParent();
            id = id - 1;
        }
        ListView listView1 = (ListView) rootView.findViewById(R.id.player_fragment_lession_listview);
        ListView lvParent = (ListView) rootView.findViewById(R.id.player_fragment_lession_listview_parent);
        int size = service.getListLession().size();

        String[] items = new String[size];
        for(int i =0; i< size;i++){
            items[i] = service.getListLession().get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
                R.layout.item_list_string, items);
        ArrayAdapter<String> adapterPar = new ArrayAdapter<>(mActivity,
                R.layout.item_list_string, parentItem);
        lvParent.setAdapter(adapterPar);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                service.playSongIndex(position);
            }
        });
        return rootView;
    }
}
