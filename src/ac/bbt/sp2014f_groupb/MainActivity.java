package ac.bbt.sp2014f_groupb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.MapFragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity  {

	private static GoogleMap mGoogleMap = null;
    private static Location mMyLocation = null;
    private static boolean mMyLocationCentering = false;
    
    public static String posinfo = "";
    public static String info_A = "";
    public static String info_B = "";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

    	static private List<Place> list;
    	private AreaListAdapter adapter;
    	private int getcount = 10;
    	private int topPosition;
    	private int topPositionY;
    	private boolean setpos;
    	private double mLat;
    	private double mLong;
    	private int mMaxResultCount = 0;
    	private static MarkerOptions mMyMarkerOptions = null;
    	private boolean initFlg = true;
    	public String travelMode = "walking";
    	
    	private class Place {
    		private String _name;
    	    private String _address;
    		private String _lat;
    		private String _lng;
    		private String _genre;
    		
    	    public Place(String name, String address, String lat, String lng, String genre) {
    	        this._name = name;
    	        this._address = address;
    	        this._lat = lat;
    	        this._lng = lng;
    	        this._genre = genre;
    	    }
    	    public String getName() {
    	        return _name;
    	    }
    	    public String getAddress() {
    	        return _address;
    	    }
    	    public String getLat() {
    	        return _lat;
    	    }
    	    public String getLng() {
    	        return _lng;
    	    }
    	    public String getGenre() {
    	        return _genre;
    	    }
    	}
    	
    	private class AreaListAdapter extends BaseAdapter {
    	    private Context context;
    	    private List<Place> list;
    	    private LayoutInflater layoutInflater = null;

    	    public AreaListAdapter(Context context, List<Place> list) {
    	        super();
    	        this.context = context;
    	        this.list = list;
    	        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	    }
    	    @Override
    	    public int getCount() {
    	        return list.size();
    	    }
    	    @Override
    	    public Object getItem(int position) {
    	        return list.get(position);
    	    }
    	    @Override
    	    public long getItemId(int position) {
    	        return position;
    	    }
    	    @Override
    	    public View getView(int position, View convertView, ViewGroup parent) {
    	    	Place place = (Place) getItem(position);
    	        convertView = layoutInflater.inflate(R.layout.listitem, null);
    	        TextView tv = (TextView) convertView.findViewById(R.id.listitem);
    	        tv.setText(place.getName() + "\r\n" + place.getGenre() + "\r\n" + place.getAddress());
    	        
    	        return convertView;
    	    }
    	}
    	
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            
            // ListViewオブジェクト取得
        	ListView listview = (ListView)rootView.findViewById(R.id.lv_list);
        	// ListViewオブジェクトにクリックリスナー設定
        	listview.setOnItemClickListener(new ListItemClickListener());
            //　ListViewオブジェクトにスクロールリスナー設定
        	listview.setOnScrollListener(new onScrollListber());
        	//　スポット検索結果を格納するリストの初期化
        	list = new ArrayList<Place>();
        	//　Map関連処理
        	mGoogleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mGoogleMap != null) {
                // 現在地マーカーを表示
                mGoogleMap.setMyLocationEnabled(true);
                // 現在地が取得できたらマップ中央に表示する
                mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        mMyLocation = location;
                        if (mMyLocation != null && mMyLocationCentering == false) {    // 一度だけ現在地を画面中央に表示する
                            mMyLocationCentering = true;
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude()), 15.0f);
                            mGoogleMap.animateCamera(cameraUpdate);
                            //現在地緯度
                            mLat = mMyLocation.getLatitude();
                            //現在地経度
                            mLong = mMyLocation.getLongitude();
                            //初回のみスポット検索を実施
                            if (initFlg == true) {
                            	initFlg = false;
                            	AsyncHttpRequest task = new AsyncHttpRequest(getActivity());
                            	task.execute(createURL(0));
                            }
                        }
                    }
                });
            }
            
        	return rootView;
        }
        
        public void onStart() {
        	super.onStart();
        	
        	//
        	//list = new ArrayList<Place>();
        	// 非同期(スレッド)処理クラスの生成
        	//AsyncHttpRequest task = new AsyncHttpRequest(getActivity());
        	//task.execute(createURL(0));
        }

        private AsyncHttpRequest mTask ;
        
        //ListViewスクロール時のイベント
        class onScrollListber implements OnScrollListener{

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO 自動生成されたメソッド・スタブ
				if (totalItemCount == 0) {
					return;
				}
				//リストが最下行まで移動したら再読み込み
				if (totalItemCount == firstVisibleItem + visibleItemCount) {
	            	// 非同期(スレッド)処理クラスの生成
					if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
						return;
					}
					 //いま表示しているリストアイテムの順番を取得
					ListView listview = (ListView) getActivity().findViewById(R.id.lv_list);
			        if(listview.getChildAt(0)!=null){
			        	topPosition = listview.getFirstVisiblePosition();
			        	topPositionY = listview.getChildAt(0).getTop();
			        	setpos = true;
			        }else{
			        	setpos = false; 
			        }
					
					if (mMaxResultCount != totalItemCount) {
						mTask = new AsyncHttpRequest(getActivity());
						mTask.execute(createURL(totalItemCount));
					}
			        //mTask = new AsyncHttpRequest(getActivity());
			        //mTask.execute(createURL(totalItemCount));
	                //additionalReading();
	            }
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO 自動生成されたメソッド・スタブ
				
			}
        	
        }
        
        //ListViewのITEMクリック時のイベント
        class ListItemClickListener implements OnItemClickListener{
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        		// ListViewオブジェクト取得
        		ListView listview = (ListView)parent;
        		
        		// 選択された値取得
        		Place item = (Place) listview.getAdapter().getItem(position);
        		
        		mMyMarkerOptions = new MarkerOptions();
                mMyMarkerOptions.position(new LatLng(Double.parseDouble(item.getLat()), Double.parseDouble(item.getLng())));
                mMyMarkerOptions.snippet(item.getName());
                
                // 古いピンを消去する
                mGoogleMap.clear();
                // タップしたスポットの地点にピンを立てる
                mGoogleMap.addMarker(mMyMarkerOptions);
                
                //ルート検索
                routeSearch(item.getLat(), item.getLng());
                
        		// Toast確認
        		Toast.makeText(getActivity(), "お店 : " + item.getName() + ", 緯度 : " + item.getLat() + ", 経度 : " + item.getLng(), Toast.LENGTH_SHORT).show();
        		
        	}
        }
        
        private void routeSearch(String lat, String lng){
            //progressDialog.show();
            
            LatLng origin = new LatLng(mLat, mLong);
            LatLng dest = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        
            String url = getDirectionsUrl(origin, dest);
            DownloadTask downloadTask = new DownloadTask();
            
            downloadTask.execute(url);
        }

        private String getDirectionsUrl(LatLng origin, LatLng dest){

            String str_origin = "origin="+origin.latitude+","+origin.longitude;
            String str_dest = "destination="+dest.latitude+","+dest.longitude;
            String sensor = "sensor=false";

            //パラメータ
            String parameters = str_origin+"&"+str_dest+"&"+sensor + "&language=ja" + "&mode=" + travelMode;

            //JSON指定
            String output = "json";            
            String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

            return url;
        }
        
        private String downloadUrl(String strUrl) throws IOException{
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try{
                URL url = new URL(strUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while( ( line = br.readLine()) != null){
                    sb.append(line);
                }
                data = sb.toString();
                br.close();

            }catch(Exception e){
                //Log.d("Exception while downloading url", e.toString());
            }finally{
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        private class DownloadTask extends AsyncTask<String, Void, String>{
            //非同期で取得
            @Override
            protected String doInBackground(String... url) {
                 
                String data = "";
                try{
                    // Fetching the data from web service
                    data = downloadUrl(url[0]);
                }catch(Exception e){
                    //Log.d("Background Task",e.toString());
                }
                return data;
            }
              
            // doInBackground()
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                ParserTask parserTask = new ParserTask();
                parserTask.execute(result);
            }
        }

        /*parse the Google Places in JSON format */
        private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
            
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try{
                    jObject = new JSONObject(jsonData[0]);
                    ParseJsonpOfDirectionAPI parser = new ParseJsonpOfDirectionAPI();
                    
                    routes = parser.parse(jObject);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return routes;
            }

            //ルート検索で得た座標を使って経路表示
            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {
                
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();
                
                if(result.size() != 0){
                    
                    for(int i=0;i<result.size();i++){
                        points = new ArrayList<LatLng>();
                        lineOptions = new PolylineOptions();
                        
                        List<HashMap<String, String>> path = result.get(i);
                        
                        for(int j=0;j<path.size();j++){
                            HashMap<String,String> point = path.get(j);
        
                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
        
                            points.add(position);
                        }
        
                        //ポリライン
                        lineOptions.addAll(points);
                        lineOptions.width(10);
                        lineOptions.color(0x550000ff);
                        
                    }
                
                    //描画
                    mGoogleMap.addPolyline(lineOptions);
                }else{
                	mGoogleMap.clear();
                    Toast.makeText(getActivity(), "ルート情報を取得できませんでした", Toast.LENGTH_LONG).show();
                }
                //progressDialog.hide();
                
            }
        }
        
        //時間帯によってジャンルを決定する
        public String[] createGenre() {
        	String genre[] = {null,null,null};
        	
        	long currentTimeMillis = System.currentTimeMillis();
        	
        	Calendar calendar = Calendar.getInstance();
        	
        	calendar.setTimeInMillis(currentTimeMillis);
        	
        	Date date = new Date(currentTimeMillis);
        	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
        	
        	String time = simpleDateFormat.format(date);
        	if (Integer.parseInt(time) >= 6000 && Integer.parseInt(time) < 100000) {
        		genre[0] = "G014";
        		genre[1] = "G004";
        		genre[2] = "G005";
        		
        	} else if (Integer.parseInt(time) >= 100000 && Integer.parseInt(time) < 140000) {
        		genre[0] = "G005";
        		genre[1] = "G006";
        		genre[2] = "";

        	} else if (Integer.parseInt(time) >= 140000 && Integer.parseInt(time) < 180000) {
        		genre[0] = "G014";
        		genre[1] = "";
        		genre[2] = "";

        	} else if (Integer.parseInt(time) >= 180000 && Integer.parseInt(time) < 210000) {
        		genre[0] = "G001";
        		genre[1] = "";
        		genre[2] = "";

        	} else {
        		genre[0] = "G011";
        		genre[1] = "G012";
        		genre[2] = "G013";

        	}
        	
        	return genre;
        }
        //HTTPリクエストを送信する準備
        public String createURL(int listcount) {
        	
        	//yahoo
            //String apiURL = "http://shopping.yahooapis.jp/ShoppingWebService/V1/itemSearch?";
        	//String appid = "dj0zaiZpPU9XR2h5QldXQTh4VyZzPWNvbnN1bWVyc2VjcmV0Jng9NDk-";
        	//String name = "大前研一";
            
        	//google
        	//String apiURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/xml?";
        	//String appid = "AIzaSyDOg_XH2BY-gTUZ6WmgmdVToDkK-APXmC0";　//android key
            //String appid = "AIzaSyDFzQIcjM5hftXYdAukwDRi_QQieyKSXeA"; //browser
            //String location = "35.6814,139.7674";
            //String radius = "3000";
            //String types = "lodging";
            //String language = "ja";
            //String name = "harbour";
            //String sensor = "false";
            
        	//ぐるなび
        	//String apiURL = "http://api.gnavi.co.jp/ver1/RestSearchAPI/?";
        	//String appid = "0ad36a3ad657598864dd3139426f3c5b";
            //String area = "AREA110";
            //String pref = "PREF13";
            //String sort = "1";
            //String name = "harbour";
            
            //hotpepper
            String apiURL = "http://api.hotpepper.jp/GourmetSearch/V110/?";
        	String appid = "guest";
            String latitude = "35.658517";
            String longitude = "139.701334";
            String genre[] = createGenre();
            String range = "4";
            String name = "harbour";
            int count = getcount;
            int start = listcount + 1;
            
        	//
        	String mEncString = "";
            try {
            	//キーワードをエンコード
            	mEncString = URLEncoder.encode(name, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
            //google
            //return String.format("%slocation=%s&radius=%s&types=%s&language=%s&sensor=%s&key=%s", 
            //		apiURL, location, radius, types, language, sensor, appid);
            
            //yahoo
            //return String.format("%sappid=%s&query=%s", apiURL, appid, mEncString);
            
            //ぐるなび
            //return String.format("%skeyid=%s&area=%s&pref=%s&sort=%s", 
            //		apiURL, appid, area, pref, sort);
            
            //hotpepper
            return String.format("%skey=%s&Latitude=%s&Longitude=%s&Range=%s&GenreCD=%s&GenreCD=%s&GenreCD=%s&Start=%s&Count=%s", 
            		apiURL, appid, mLat, mLong, range, genre[0], genre[1], genre[2], start, count);
        }
        
        //XML解析
        public void readXML(InputStream stream) 
        		throws XmlPullParserException {
        	try {
                // (1)XmlPullParserの用意
                XmlPullParser myxmlPullParser = Xml.newPullParser();
                myxmlPullParser.setInput(stream, "UTF-8");

                String name = "";
                String address = "";
                String latitude = "";
                String longitude = "";
                String genre = "";
                for (int e = myxmlPullParser.getEventType(); 
                		e != XmlPullParser.END_DOCUMENT; e = myxmlPullParser.next()) {
                        
                	if (e == XmlPullParser.START_TAG) {
                		if (myxmlPullParser.getName().equals("NumberOfResults")) {
                			// (2)取得した記事の個数の取得
                			mMaxResultCount = Integer.parseInt(myxmlPullParser.nextText());
                			
                		} else if (myxmlPullParser.getName().equals("ShopName")) {
                			// (3)お店の名前の取得
                			name = myxmlPullParser.nextText();
                		} else if (myxmlPullParser.getName().equals("ShopAddress")) {
                			// (3)お店のアドレスの取得
                			address = myxmlPullParser.nextText();
                		} else if (myxmlPullParser.getName().equals("Latitude")) {
                			// (3)お店の 緯度の取得
                			latitude = myxmlPullParser.nextText();
                		} else if (myxmlPullParser.getName().equals("Longitude")) {
                			// (3)お店の経度の取得
                			longitude = myxmlPullParser.nextText();
                		} else if (myxmlPullParser.getName().equals("GenreName")) {
                			// (3)お店のジャンルの取得
                			genre = myxmlPullParser.nextText();
                		}
                	}
                	if (e == XmlPullParser.END_TAG) {
                		if (myxmlPullParser.getName().equals("Shop")) {
                			list.add(new Place(name, address, latitude, longitude, genre));
                		}
                	}
                }
        	} catch (XmlPullParserException e) {
        		e.printStackTrace();
        	} catch (IOException e) {
                e.printStackTrace();
        	}
        } 
        
        //リクエスト要求を実行
        public String httpGet(String strURL) {
            // (1)try-catchによるエラー処理
            try {
                // (2)URLクラスを使用して通信を行う
                URL url = new URL(strURL);
                URLConnection connection = url.openConnection();
                // 動作を入力に設定
                connection.setDoInput(true);
                InputStream stream = connection.getInputStream();
                
                readXML(stream);
                
                //不要なロジック
                String data = "";
                //for(int i=0; i<mArticleNum; i++){
                //        data += mArticleTitle[i];
                //}
                
                // (4)終了処理
                stream.close();
                //input.close();
                return data;
            } catch (Exception e) {
                // (5)エラー処理
                return e.toString();
            }
        }
        
        public class AsyncHttpRequest extends AsyncTask<String, Void, String> {
            
            private Activity mainActivity;

            public AsyncHttpRequest(Activity activity) {
                // 呼び出し元のアクティビティ
                this.mainActivity = activity;
            }
            
            // このメソッドは必ずオーバーライドする必要があるよ
            // ここが非同期で処理される部分みたいたぶん。
            @Override
            protected String doInBackground(String... params) {
            	
            	String url1 = params[0];
            	return httpGet(url1);
            }

            // このメソッドは非同期処理の終わった後に呼び出されます
            @Override
            protected void onPostExecute(String result) {
            	// 取得した結果をリストビューに格納
            	ListView listview = (ListView) getActivity().findViewById(R.id.lv_list);
            	adapter = new AreaListAdapter(getActivity(), list);
            	listview.setAdapter(adapter);
            	//リストを更新前の位置に戻す
            	if(setpos){
            		listview.setSelectionFromTop(topPosition, topPositionY);
            	}
                
            }
        }
        
    }
}
