package ece558.pdx.edu.project3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static ece558.pdx.edu.project3.LoginActivity.EXTRA_USERNAME_KEY;
import static ece558.pdx.edu.project3.R.layout.activity_location_update;


/**
 * LocationUpdate.java handles the UI appearing on Sign In
 *
 */
public class LocationUpdate extends AppCompatActivity{

    private EditText send_message,get_location;
    private Button send;
    int PLACE_PICKER_REQUEST = 1;
    Editable message;
    LoginDataBaseAdapter loginData;
    public double latitude,longitude,modalat,modalon;
    public String mPlaceString;
    private TextView mCurrentLocationTextView;
    private Button mCurrentLocationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_location_update);

        /**
         * TODO: Define onClick Listener for "get_location" text box to get user's current location
         * Hint: Need to define IntentBuilder for PlacePicker built-in UI widget
         * Reference : https://developers.google.com/places/android-api/placepicker
         */
        get_location = (EditText) findViewById(R.id.get_Location);
        get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(LocationUpdate.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d( "PlacesAPI Demo", "GooglePlayServicesRepairableException thrown" );
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d( "PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown" );
                }
            }
        });

        /**
         * TODO: Define TextView field to display the address of current location on the UI
         *
         */

        mCurrentLocationTextView = (TextView) findViewById(R.id.saveAddress);

        /**
         * TODO: Define onClick Listener for "Current_Location Button"
         * Hint: OnClick event should set the text for TextView field defined above
         */
        mCurrentLocationButton = (Button) findViewById(R.id.currentLocation);
        mCurrentLocationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCurrentLocationTextView.setText(mPlaceString);
            }
        });


        /**
         * Do not edit the code below as it is dependent on server just fill the required snippets
         *
         */
        send_message = (EditText) findViewById(R.id.Send_Message);
        send = (Button) findViewById(R.id.Send_Button);
        loginData = new LoginDataBaseAdapter(this);
        loginData = loginData.open();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * OnClick event for send button gets username and location details
                 */

   try {
       message = send_message.getText();
       Bundle extras = getIntent().getExtras();
       String rx_username = extras.getString(EXTRA_USERNAME_KEY); /// Edited

       /**
        * TODO: Enable the code below after defining getLat() and getLng()
        * TODO: methods in LoginDataBaseAdapter
        */
       String rx_lat = loginData.getLat(rx_username);
       String rx_lon = loginData.getLng(rx_username);

       /**
        * store in latitude , longitude variables to pass to json object
        */
       modalat = Double.parseDouble(rx_lat);
       modalon = Double.parseDouble(rx_lon);
   }

   catch (Exception e) {
       System.out.println ("Exception Caught");
       e.printStackTrace();
   }
       

                try {

                    /**
                     * Creates a JSON object and uses toSend.put to send home, current location along with message
                     *Pass data as name/value pair where you cannot edit name written
                     *in " " ex:"home_lat" as this are hard coded on server side.
                     *You can change the variable name carrying value ex:modalat
                     */
                    JSONObject toSend = new JSONObject();
                    toSend.put("home_lat", modalat);
                    toSend.put("home_lon",modalon);
                    toSend.put("c_lat", latitude);
                    toSend.put("c_lon",longitude);
                    toSend.put("message",message);

                    /**
                     * Creates transmitter object to send data to server
                     */
                    JSONTransmitter transmitter = new JSONTransmitter();
                    transmitter.execute(new JSONObject[] {toSend});

                    /**
                     * Receives a message from the server which is displayed as toast
                     */
                    JSONObject output=transmitter.get();
                    String op=output.getString("message");
                    Toast.makeText(LocationUpdate.this,op, Toast.LENGTH_LONG).show();

                }
                //To handle exceptions
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                catch (InterruptedException e)
                {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * TODO: Define onActivityResult() method which would take Place_Picker_request
     * and extract current Latitude, Longitude and address string
     * Hint : Set the address String to "get_location" text box
     * Reference : https://developers.google.com/places/android-api/placepicker
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getAddress());
                mPlaceString = toastMsg;
                get_location.setText(toastMsg);
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id==R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}



