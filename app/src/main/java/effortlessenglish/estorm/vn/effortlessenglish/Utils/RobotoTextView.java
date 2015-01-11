package effortlessenglish.estorm.vn.effortlessenglish.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoTextView extends TextView {

	public RobotoTextView(Context context) {
		super(context);
	}

	public RobotoTextView(Context context, AttributeSet attributeset) {
		super(context, attributeset);
	}

	public RobotoTextView(Context context, AttributeSet attributeset, int i) {
		super(context, attributeset, i);
	}

	@Override
	public void setTypeface(Typeface tf, int style) {
		if (!isInEditMode()) {
			super.setTypeface(Typeface.createFromAsset(
					getContext().getAssets(), "fonts/RobotoCondensed-Regular.ttf"));
		}
	}

}