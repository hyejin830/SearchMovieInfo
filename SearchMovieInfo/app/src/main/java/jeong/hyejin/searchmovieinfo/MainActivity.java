package jeong.hyejin.searchmovieinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MovieInfoAdapter.OnItemClickListener{
    public static final int MAX_VALUE = 100;
    public static final String EXTRA_LINK = "link";

    private Button searchBtn;
    private EditText searchText;
    private RecyclerView mRecyclerView;

    private parseJSONTask task;

    private MovieInfoAdapter mMovieInfoAdapter;
    private ArrayList<MovieInfoItem> mMovieInfoList;
    private RecyclerView.LayoutManager mLayoutManager;

    private String clientId = "924069i8FysiNZyJjjme";//애플리케이션 클라이언트 아이디값";
    private String clientSecret = "r5yr7G_5d0";//애플리케이션 클라이언트 시크릿값";
    static String keyword = "";

    private RequestQueue mRequestQueue;

    private InputMethodManager imm;

    private ConnectivityManager manager;
    private NetworkInfo mobile,wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(!wifi.isConnected() && !mobile.isConnected()){
            Toast.makeText(MainActivity.this, R.string.internet_error,Toast.LENGTH_LONG).show();
        }

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        searchBtn = (Button)findViewById(R.id.searchBtn);
        searchText = (EditText)findViewById(R.id.search_text);

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        mMovieInfoList = new ArrayList<>();

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getApplicationContext(),new LinearLayoutManager(this).getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRequestQueue = Volley.newRequestQueue(this);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMovieInfoList.clear();

                keyword = searchText.getText().toString();
                if(keyword.equals("")){
                    Toast.makeText(MainActivity.this,R.string.nokeyword,Toast.LENGTH_SHORT).show();
                }else if((wifi.isConnected()||mobile.isConnected())&&!keyword.equals("")){
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(),0);
                    task = new parseJSONTask(MainActivity.this);
                    task.execute(keyword);
                }

            }
        });
    }

    class parseJSONTask extends AsyncTask<String,Void,String>{
        private ProgressDialog mProgressBar;
        String returnResult = "";

        public parseJSONTask(Context context){
            mProgressBar = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setMessage("잠시만요...");
            mProgressBar.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String text = URLEncoder.encode(strings[0], "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/search/movie.json?query="+text+"&display="+MAX_VALUE; // json 결과

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("items");
                                    returnResult = "success";
                                    if(jsonArray.length()==0){
                                        Toast.makeText(MainActivity.this,"'"+keyword+"'검색결과는 없습니다..",Toast.LENGTH_SHORT).show();
                                    }
                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject item = jsonArray.getJSONObject(i);
                                        String image = item.getString("image");
                                        String title = item.getString("title");
                                        double user_rating = item.getDouble("userRating");
                                        String pub_date = item.getString("pubDate");
                                        String director = item.getString("director");
                                        String actors = item.getString("actor");
                                        String link = item.getString("link");

                                        mMovieInfoList.add(new MovieInfoItem(image,title,user_rating,pub_date,director,actors,link));

                                    }

                                    mMovieInfoAdapter = new MovieInfoAdapter(MainActivity.this,mMovieInfoList);
                                    mRecyclerView.setAdapter(mMovieInfoAdapter);
                                    mMovieInfoAdapter.setOnItemClickListener(MainActivity.this);
                                    mMovieInfoAdapter.notifyDataSetChanged();
                                    mRecyclerView.invalidate();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    returnResult="fail";
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        returnResult="fail";
                    }
                }
                ){
                    @Override
                    public Map<String,String> getHeaders() throws AuthFailureError{
                        Map<String,String> params = new HashMap<>();
                        params.put("X-Naver-Client-Id",clientId);
                        params.put("X-Naver-Client-Secret",clientSecret);
                        Log.d("getHeader => ",""+params);
                        return params;
                    }
                };
                queue.add(request);
            }catch (Exception e) {
                System.out.println(e);
                returnResult="fail";
            }

            return returnResult;
        }

        @Override
       protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressBar.dismiss();
            if(s.equals("fail")){
                Toast.makeText(MainActivity.this,"네트워크 오류...",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent linkIntent = new Intent(this,DetailActivity.class);
        MovieInfoItem clickedItem = mMovieInfoList.get(position);
        linkIntent.putExtra(EXTRA_LINK,clickedItem.getLink());
        startActivity(linkIntent);
    }
}
