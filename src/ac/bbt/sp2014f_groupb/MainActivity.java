package ac.bbt.sp2014f_groupb;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.os.Build;



public class MainActivity extends Activity {

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
            
            // ListView�I�u�W�F�N�g�擾
        	ListView listview = (ListView)rootView.findViewById(R.id.lv_list);
        	// ListView�I�u�W�F�N�g�ɃN���b�N���X�i�[�ݒ�
        	listview.setOnItemClickListener(new ListItemClickListener());
            
            return rootView;
        }
        
        public void onStart() {
        	super.onStart();
        	// �񓯊�(�X���b�h)�����N���X�̐���
        	AsyncHttpRequest task = new AsyncHttpRequest(getActivity());
        	task.execute(createURL());
        }

        class ListItemClickListener implements OnItemClickListener{
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        		// ListView�I�u�W�F�N�g�擾
        		ListView listview = (ListView)parent;
        		// �I�����ꂽ�l�擾
        		Place item = (Place) listview.getAdapter().getItem(position);
        		// Toast�m�F
        		Toast.makeText(getActivity(), "���X : " + item.getName() + ", �ܓx : " + item.getLat() + ", �o�x : " + item.getLng(), Toast.LENGTH_SHORT).show();
        		
        	}
        }
        
        //HTTP���N�G�X�g�𑗐M���鏀��
        public String createURL() {
        	//yahoo
            //String apiURL = "http://shopping.yahooapis.jp/ShoppingWebService/V1/itemSearch?";
        	//String appid = "dj0zaiZpPU9XR2h5QldXQTh4VyZzPWNvbnN1bWVyc2VjcmV0Jng9NDk-";
        	//String name = "��O����";
            
        	//google
        	//String apiURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/xml?";
        	//String appid = "AIzaSyDOg_XH2BY-gTUZ6WmgmdVToDkK-APXmC0";�@//android key
            //String appid = "AIzaSyDFzQIcjM5hftXYdAukwDRi_QQieyKSXeA"; //browser
            //String location = "35.6814,139.7674";
            //String radius = "3000";
            //String types = "lodging";
            //String language = "ja";
            //String name = "harbour";
            //String sensor = "false";
            
        	//����Ȃ�
        	//String apiURL = "http://api.gnavi.co.jp/ver1/RestSearchAPI/?";
        	//String appid = "0ad36a3ad657598864dd3139426f3c5b";
            //String area = "AREA110";
            //String pref = "PREF13";
            //String sort = "1";
            //String name = "harbour";
            
            //hotpepper
            String apiURL = "http://api.hotpepper.jp/GourmetSearch/V110/?";
        	String appid = "guest";
            String latitude = "35.681382";
            String longitude = "139.766084";
            String range = "3";
            String name = "harbour";
            
        	//
        	String mEncString = "";
            try {
            	//�L�[���[�h���G���R�[�h
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
            
            //����Ȃ�
            //return String.format("%skeyid=%s&area=%s&pref=%s&sort=%s", 
            //		apiURL, appid, area, pref, sort);
            
            //hotpepper
            return String.format("%skey=%s&Latitude=%s&Longitude=%s&Range=%s", 
            		apiURL, appid, latitude, longitude, range);
        }
        
        //XML���
        public void readXML(InputStream stream) 
        		throws XmlPullParserException {
        	try {
                // (1)XmlPullParser�̗p��
                XmlPullParser myxmlPullParser = Xml.newPullParser();
                myxmlPullParser.setInput(stream, "UTF-8");

                list = new ArrayList<Place>();
                String name = "";
                String address = "";
                String latitude = "";
                String longitude = "";
                String genre = "";
                for (int e = myxmlPullParser.getEventType(); 
                		e != XmlPullParser.END_DOCUMENT; e = myxmlPullParser.next()) {
                        
                	if (e == XmlPullParser.START_TAG) {
                		if (myxmlPullParser.getName().equals("NumberOfResults")) {
                			// (2)�擾�����L���̌��̎擾
                			String status = myxmlPullParser.nextText();
                			
                		} else if (myxmlPullParser.getName().equals("ShopName")) {
                			// (3)���X�̖��O�̎擾
                			name = myxmlPullParser.nextText();
                		} else if (myxmlPullParser.getName().equals("ShopAddress")) {
                			// (3)���X�̃A�h���X�̎擾
                			address = myxmlPullParser.nextText();
                		} else if (myxmlPullParser.getName().equals("Latitude")) {
                			// (3)���X�� �ܓx�̎擾
                			latitude = myxmlPullParser.nextText();
                		} else if (myxmlPullParser.getName().equals("Longitude")) {
                			// (3)���X�̌o�x�̎擾
                			longitude = myxmlPullParser.nextText();
                		} else if (myxmlPullParser.getName().equals("GenreName")) {
                			// (3)���X�̃W�������̎擾
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
        
        public String httpGet(String strURL) {
            // (1)try-catch�ɂ��G���[����
            try {
                // (2)URL�N���X���g�p���ĒʐM���s��
                URL url = new URL(strURL);
                URLConnection connection = url.openConnection();
                // �������͂ɐݒ�
                connection.setDoInput(true);
                InputStream stream = connection.getInputStream();
                
                readXML(stream);
                
                //�s�v�ȃ��W�b�N
                String data = "";
                //for(int i=0; i<mArticleNum; i++){
                //        data += mArticleTitle[i];
                //}
                
                // (4)�I������
                stream.close();
                //input.close();
                return data;
            } catch (Exception e) {
                // (5)�G���[����
                return e.toString();
            }
        }
        
        public class AsyncHttpRequest extends AsyncTask<String, Void, String> {
            
            private Activity mainActivity;

            public AsyncHttpRequest(Activity activity) {
                // �Ăяo�����̃A�N�e�B�r�e�B
                this.mainActivity = activity;
            }
            
            // ���̃��\�b�h�͕K���I�[�o�[���C�h����K�v�������
            // �������񓯊��ŏ�������镔���݂������Ԃ�B
            @Override
            protected String doInBackground(String... params) {
            	
            	String url1 = params[0];
            	return httpGet(url1);
            }

            // ���̃��\�b�h�͔񓯊������̏I�������ɌĂяo����܂�
            @Override
            protected void onPostExecute(String result) {
                // �擾�������ʂ����X�g�r���[�Ɋi�[
            	ListView listview = (ListView) getActivity().findViewById(R.id.lv_list);
            	listview.setAdapter(new AreaListAdapter(getActivity(), list));
            }
        }
        
    }
}
