package effortlessenglish.estorm.vn.effortlessenglish.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        ListView listView1 = (ListView) rootView.findViewById(R.id.player_fragment_lession_listview);

        String[] items = { "Milk", "Butter", "Yogurt", "Toothpaste", "Ice Cream" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_list_item_1, items);

        listView1.setAdapter(adapter);
        return rootView;
    }
}
