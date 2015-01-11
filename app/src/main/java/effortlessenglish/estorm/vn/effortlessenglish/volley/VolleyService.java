package effortlessenglish.estorm.vn.effortlessenglish.volley;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class VolleyService {

	public static JSONObject jsonObjectResult = new JSONObject();

	public VolleyService() {

	}

	private static JSONObject postInfo(String url, JSONObject paramsOb) {
		VolleySingleton
				.getInstance()
				.getRequestQueue()
				.add(new JsonObjectRequest(url, paramsOb,
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								jsonObjectResult = response;
							}

						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								VolleyLog.e("Volley ErrorResponse: ",
										error.getMessage());
								jsonObjectResult = new JSONObject();
							}

						}));
		return jsonObjectResult;
	}

}
