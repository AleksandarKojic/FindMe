package com.alexandar.android.findme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Alexandar on 1/23/2016.
 */


//     TODO : Iskoristi chapter 15 iz Android knjige i dodaj da kada se odabere lokacija, salje se odabranim korisnicima tako sto otvori imenik ili jos bolje sms u kojem je unapred definisam tex sa lokacijom za slanje. Ovako ti vec radi projekat Locatr kad ga pokrenes, verovatno jer nema nijedna druga app na virtuelnoj masini osim messaginga. Iskoristi to!
//     TODO : Obavezno da pored opcije da te locira GPS-om i nju posalje prijatelju, dodaj i mogucnost da kliknes prstom na mesto gde hoces na mapi pa salje tu lokaciju. Tako mozes da mu posaljes sms tipa, "nadjemo se ovde" i  ubodes prstom gde...
//     TODO : Dodaj da pamti poslednju lokaciju i zumira na nju. ili barem da zumira na nivo tog grada, da ne pocinje uvek od mape sveta.
//     TODO : Sredi da zoom ne bude preveliki vec na nivo gradskog bloka, ne same ulice.
//     TODO : Dodaj da app ima opciju snimanja tvog kretanja po gradu i nacrtati waypath tih tacaka. Verovano je najbolje preko SQLite baze u koju snimas sa razmakom od 10s npr...
//     TODO : Prepravi da ne pocinje aplikacija tako sto ce pamtiti poziciju iz prethodnoh lociranja vec tako sto u pozadini nadje grubu lokaciju i zumira na nivo grada. To moze pomocu mreze i wirelessa, bez gps-a. moze da bude neki drugi pocetni panel za startnu aktivnost i to ucitava u medjuvremenu dok si na tom pocetnom ekranu, kao pozadinsku Async proces. Stavi da snima nekako i kesira mapu, da ne mora uvek od nule da ucitava?
//     TODO : OVO JAKO BITNO!!! Dodaj opciju da moze da napravi snapshot trenutnog ekrana mape koju si odabrao, sa sve eventualnim textom kojeg si ukucao iznad te odabrane tacke, pa da posalje tu sliku nekome, bilo cime. Recimo Viber ili slicno...


// TODO : BITNO!!!!   Dodaj i da moze da se odabere trenutna lokacija (ili mesto gde zelimo da se nadjemo) jednostavnim dodirom prsta na mapi.
public class FindMeFragment extends SupportMapFragment {

    private static final String TAG = "LocatrFragment";
    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private Location mCurrentLocation;
    private float zoomLevel = 15;

    public static FindMeFragment newInstance() {
        return new FindMeFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {  // TODO : Primer Builder patterna
            @Override
            public void onConnected(Bundle bundle) {
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        }).build();

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                if (!SharedPreferences.checkFirstRun(getActivity()).equalsIgnoreCase("firstRun")) { // Ovo je bitno jer pri prvom pokretanju nema snimljenje poslednje pozicije
                    mCurrentLocation = SharedPreferences.getStoredLocation(getActivity());
                }
                updateUI(); // Zasto mora ovaj poziv ovde?
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().invalidateOptionsMenu();  // Zasto ovde? Zar nije logicno da ide tek posle sledeceg reda gde se connectuje? Mozda na ovaj nacin postize da ikonica bude disabled ako nije uspeo da se nakaci?
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        mClient.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_find_me, menu);    // Zar nije logicno da ova linije bude iznad poziva super-u, da li se ovako vide promene koje je napravio inlate-ovanjem nakon prosledjivanja?

        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mClient.isConnected());

        MenuItem sendItem = menu.findItem(R.id.action_send_location);
        if (mCurrentLocation != null) sendItem.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                findLocation();
                return true;
            case R.id.action_send_location:
                startComposeMessageFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // ***** Iznad su override-ovani lifecycle metodi. Ispod su moji custom *****

    private void findLocation() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i(TAG, "Got a fix: " + location);
                        mCurrentLocation = location;
                        updateUI();
                    }
                });
    }


    private void updateUI() {
        if (mMap == null || mCurrentLocation == null) {  // ne moze bez ove provere da li je mCurentLocation == null jer prvi put kad se pozove updateUI() u onCreate(), bice null i onda puca aplikacija...
            return;
        }

        SharedPreferences.setStoredLocation(getActivity(), mCurrentLocation);

        LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        LatLngBounds bounds = new LatLngBounds.Builder().include(myPoint).build();
        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);

        MarkerOptions myMarker = new MarkerOptions().position(myPoint);
        mMap.clear();
        mMap.addMarker(myMarker);

//        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myPoint, zoomLevel);
        mMap.animateCamera(update);
    }





    public void startComposeMessageFragment(){
        String latitude = Double.toString(mCurrentLocation.getLatitude());
        String longitude = Double.toString(mCurrentLocation.getLongitude());
        Intent intent = ComposeMessageActivity.newIntent(getActivity(), latitude, longitude);
        startActivity(intent);
    }










    // Slanje obicnog text sms-a pomocu impicit intent-a. Dakle, koristi se default android sms aplikacija.
    private void sendSMS() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setType("vnd.android-dir/mms-sms");
        i.putExtra("sms_body", getLocationData());
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.sms_subject));
        startActivity(i);

        // TODO : Razmisli da dodas da salje direktno sms iz tvoje aplikacije, otvaranjem nekog lepog popup prozorcica. Koristi SmsManager? Takodje, neka na prijemu presretne sms i ako je od tvoje aplikacije da ne ide u klasican inbox.
        SmsManager smsManager = SmsManager.getDefault();
        // vidi koji metod da koristis za slanje, ima a data, za text i slicno...
    }


    // *** TODO : Sredi ovo ispisivanje stringa tako da bude promenljivo. Dakle da ne bude zakucana poruka koja je u sms-u vec da mozes da otkucas prilikom slanja sta hoces. Npr, "nalazimo se na ovoj lokaciji, dodjite i ponesite pivo"...
    private String getLocationData() {
        String latitude = Double.toString(mCurrentLocation.getLatitude());
        String longitude = Double.toString(mCurrentLocation.getLongitude());
        String location = "FindMe Location : latitude - " + latitude + " longitude - " + longitude;
        return location;
    }
}
