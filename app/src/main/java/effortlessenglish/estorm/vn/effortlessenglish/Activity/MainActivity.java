package effortlessenglish.estorm.vn.effortlessenglish.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import effortlessenglish.estorm.vn.effortlessenglish.Base.BaseActivity;
import effortlessenglish.estorm.vn.effortlessenglish.Models.Danhmuc;
import effortlessenglish.estorm.vn.effortlessenglish.R;
import effortlessenglish.estorm.vn.effortlessenglish.Services.PlayerService;
import effortlessenglish.estorm.vn.effortlessenglish.Utils.Constants;

public class MainActivity extends BaseActivity implements GridView.OnItemClickListener{

    private GridView gvDanhMuc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, PlayerService.class));
    }

    @Override
    public void setActionView() {
        gvDanhMuc = (GridView) findViewById(R.id.main_gridview_item);
        gvDanhMuc.setAdapter(new DanhMucAdapter(this));
        gvDanhMuc.setOnItemClickListener(this);
    }

    @Override
    public void onPlayerChange(String action) {

    }

    @Override
    public void onConnectServcie() {
        if( service != null && service.getCurrentLession() != null && service.getCurrentLession().getId() > 0){
            showController();
        }
        if(mApp.getLastPlay().equals(""))
            return;
        String[] listID = mApp.getLastPlay().split(",");
        if(listID.length != 2)
            return;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 5)
            return;
        Intent i = new Intent(MainActivity.this, MenuActivity.class);
        i.putExtra(Constants.EXTRA_ID, position + 1);
        startActivity(i);
    }
}
class DanhMucAdapter extends BaseAdapter{

    private Context mContext;

    public DanhMucAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DanhMucHolder holder;
        if(convertView == null){
            holder = new DanhMucHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_in_main, null);
            holder.image = (ImageView)convertView.findViewById(R.id.gridViewImage);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.gridViewTitleText);
            convertView.setTag(holder);
        }else
            holder = (DanhMucHolder)convertView.getTag();
        Danhmuc danhmuc = new Danhmuc(position + 1);
        holder.tvTitle.setText(danhmuc.getName());
        return convertView;
    }

    public class DanhMucHolder{
        public ImageView image;
        public TextView tvTitle;
    }
}
