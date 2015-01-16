package effortlessenglish.estorm.vn.effortlessenglish.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import effortlessenglish.estorm.vn.effortlessenglish.R;


public class AboutDialogFragment extends Dialog {

	private Context mContext;
    private String title;
    private String content;

    public AboutDialogFragment(Context context, int theme,String title, String content) {
        super(context, theme);
        this.mContext = context;
        this.content = content;
        this.title = title;
    }

    public static AboutDialogFragment newInstance(Context context, int theme,String title, String content) {
		AboutDialogFragment aboutDialog = new AboutDialogFragment(context,theme, title, content);
		aboutDialog.setContext(context);
		return aboutDialog;
	}

	public void setContext(Context ctx) {
		this.mContext = ctx;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);

        View view = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_about, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.title_text);
        TextView tvContent = (TextView) view.findViewById(R.id.content_text);
        tvContent.setText(Html.fromHtml(content));
        tvTitle.setText(title);
        setContentView(view);
    }
}

