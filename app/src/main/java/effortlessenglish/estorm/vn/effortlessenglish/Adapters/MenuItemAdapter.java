package effortlessenglish.estorm.vn.effortlessenglish.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import effortlessenglish.estorm.vn.effortlessenglish.Models.Menu;
import effortlessenglish.estorm.vn.effortlessenglish.R;

/**
 * @author Vinh
 *
 */
public class MenuItemAdapter extends BaseAdapter{
	
	private Context mContext;
    private ArrayList<Menu> data;
	
	public MenuItemAdapter(Context context, ArrayList<Menu> data){
		this.mContext = context;
        this.data = data;
	}
	
	
	@Override
	public int getCount() {
        return data.size();
	}

	@Override
	public Object getItem(int arg0) {
        return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0).getId();
	}

	@Override
	public View getView(int p, View view, ViewGroup vg) {
		MenuHolder holder;
		if(view == null){
			holder = new MenuHolder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			view = inflater.inflate(R.layout.item_menu, null);
            holder.image = (ImageView)view.findViewById(R.id.menu_item_image);
            holder.title = (TextView)view.findViewById(R.id.menu_item_title);
			view.setTag(holder);
		}else{
			holder = (MenuHolder)view.getTag();
		}
		holder.title.setText(data.get(p).getName());
		return view;
	}
	
	public class MenuHolder{
        ImageView image;
        TextView title;
	}

}
