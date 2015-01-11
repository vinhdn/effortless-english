package effortlessenglish.estorm.vn.effortlessenglish.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import effortlessenglish.estorm.vn.effortlessenglish.R;
import effortlessenglish.estorm.vn.effortlessenglish.Services.PlayerService;

/**
 * Created by Vinh on 1/11/15.
 */
public class ContentLessionFragment extends Fragment {
    private Activity mActivity;
    private PlayerService service;

    public ContentLessionFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    public static ContentLessionFragment create(PlayerService service) {
        ContentLessionFragment fragment = new ContentLessionFragment();
        fragment.service = service;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.layout_content_lession, container, false);
        if (service != null && service.getCurrentLession() != null) {
            ((TextView) rootView.findViewById(R.id.layout_content_lession_tv))
                    .setText(Html.fromHtml(service.getCurrentLession()
                            .getDescription()));
        }
        return rootView;
    };
}
