package effortlessenglish.estorm.vn.effortlessenglish.Activity;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import effortlessenglish.estorm.vn.effortlessenglish.Base.BaseActivity;
import effortlessenglish.estorm.vn.effortlessenglish.R;

public class IntroActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        TextView textView = (TextView) findViewById(R.id.tv_intro);
        textView.setText(Html.fromHtml(getString(R.string.gioithieuchung)));
    }

    @Override
    public void setActionView() {

    }

    @Override
    public void onPlayerChange(String action) {

    }

    @Override
    public void onConnectServcie() {

    }

}
