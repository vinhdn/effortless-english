package effortlessenglish.estorm.vn.effortlessenglish.volley;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import effortlessenglish.estorm.vn.effortlessenglish.EffortlessApplication;

public class VolleySingleton {
	private static VolleySingleton mInstance = null;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	public VolleySingleton() {
		mRequestQueue = Volley.newRequestQueue(EffortlessApplication
				.getAppContext());
		mImageLoader = new ImageLoader(this.mRequestQueue,
				new ImageLoader.ImageCache() {
					private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(
							10);

					public void putBitmap(String url, Bitmap bitmap) {
						mCache.put(url, bitmap);
					}

					public Bitmap getBitmap(String url) {
						return mCache.get(url);
					}
				});
	}

	public static VolleySingleton getInstance() {
		if (mInstance == null) {
			mInstance = new VolleySingleton();
		}
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		return this.mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		return this.mImageLoader;
	}

    public void LoadImage(String url, final ImageView view){
        ImageRequest request = new ImageRequest(url, new Response.Listener() {

            @Override
            public void onResponse(Object bitmap) {
                view.setImageBitmap((Bitmap) bitmap);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                view.setImageResource(android.R.drawable.stat_notify_error);
                view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
        });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance().getRequestQueue().add(request);
    }
}
