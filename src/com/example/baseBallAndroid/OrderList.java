package com.example.baseBallAndroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Randall on 6/1/2014.
 */
public class OrderList extends Activity {

    private ArrayAdapter adapter;
    private ListView listView;
    private JSONArray restResponseJSONArray;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.order_list);

        getOrders();

    }

    public void getOrders() {
        String urlString = "http://10.0.2.2:8080/api/orders";

        RequestParams params = new RequestParams();


        BaseBallRestClient.get(urlString, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                String x = "Something";
            }

            @Override
            public
            void onSuccess(int statusCode, Header[] headers, JSONArray orderResponse) {

                //Store this to allow access to the full response Object and all of its information
                //for click events the array should display a 1:1 and same order with the simple String arrayList
                //that is created for ListView purposes only
                restResponseJSONArray = orderResponse;
                ArrayList<String> displayOrderNameArray = new ArrayList<String>();

                for(int i = 0; i<orderResponse.length(); i++) {

                    try {
                        JSONObject order = orderResponse.getJSONObject(i);

                        /*
                          JSONObject returns a series of key/value pairs, there are methods
                          available to return a requested value
                        */
                        //get the order name and place it into the arrayList
                        displayOrderNameArray.add(order.getString("name"));
                        //displayOrderNameArray.add(order);

                    }
                    catch(JSONException e) {
                        Log.e("JSONObjectError:", e.getMessage());
                    }
                }

                setListView(displayOrderNameArray);
            }

        });
    }

    public void setListView(ArrayList displayOrderNameArray) {

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, displayOrderNameArray);

        listView = (ListView) findViewById(R.id.orderListView);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(mMessageClickedHandler);
    }

    // Create a message handling object as an anonymous class.
    private AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            //id gives the list number that was selected
            String orderName = (String)listView.getItemAtPosition(position);
        }
    };


}