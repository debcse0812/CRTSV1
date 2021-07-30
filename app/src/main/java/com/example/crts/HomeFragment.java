package com.example.crts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.crts.Adapters.ComplaintListAdapter;
import com.example.crts.complaintModel.ComplaintsModel;
import com.example.crts.interfaces.RecyclerViewClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements RecyclerViewClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String phoneNumber;
    private static String userToken;

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private ProgressBar progressBar;
    private FloatingActionButton plusButton;
    ArrayList<ComplaintsModel> arrayList;

    ComplaintListAdapter complaintListAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            phoneNumber = getArguments().getString(ARG_PARAM1);
            userToken = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyTextView = view.findViewById(R.id.textView);
        progressBar = view.findViewById(R.id.progressBar);
        plusButton = view.findViewById(R.id.plusButton);

        plusButton.setOnClickListener(currentView -> {
            Toast.makeText(getActivity(), "Fill the form for a new complaint.", Toast.LENGTH_SHORT).show();

            // going to Complaint Form Activity
            Intent myIntent = new Intent(getActivity(), ComplaintFormActivity.class);
            myIntent.putExtra("phone", phoneNumber);
            Intent userToken = myIntent.putExtra("userToken", HomeFragment.userToken);
            getActivity().startActivity(myIntent);
        });

        // initializing the recyclerview to show data.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        getComplaints(); // fetch data from API and save to ArrayList, that is to be shown in recyclerview.
        return view;
    }

    private void getComplaints() {
        arrayList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
//        Collections.sort(arrayList);
        String url = "https://crtsapp.herokuapp.com/api/complaint/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if(response.getBoolean("success")){
//                            Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                            JSONArray jsonArray = response.getJSONArray("complaints");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                ComplaintsModel complaintsModel = new ComplaintsModel(jsonObject.getString("_id"),
                                        jsonObject.getString("name"),
                                        jsonObject.getString("email"),
                                        jsonObject.getString("address"),
                                        jsonObject.getString("c_type"),
                                        jsonObject.getString("c_detail"),
                                        jsonObject.getString("date"),
                                        jsonObject.getString("status"),
                                        jsonObject.getString("assigned_to"),
                                        jsonObject.getString("feedback"),
                                        jsonObject.getString("resolved_on"));

                                arrayList.add(complaintsModel);
                            }
                            contentToBeDisplayed(); // shows empty message is arrayList is empty
                            Collections.sort(arrayList,ComplaintsModel.complaintsModelComparator); // sort the arrayList according to date
                            complaintListAdapter = new ComplaintListAdapter(getActivity(), arrayList, HomeFragment.this);
                            recyclerView.setAdapter(complaintListAdapter);
                        }
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                },
                error -> {

                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    NetworkResponse response = error.networkResponse;
                    if(error instanceof ServerError && response!=null){
                        try {
                            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            JSONObject obj = new JSONObject(res);
                            Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();

                        }catch (JSONException | UnsupportedEncodingException jsonException){
                            jsonException.printStackTrace();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization",userToken);
                return headers;
            }
        };

        // Adding a retry policy to ensure user can try again to login in case there is an issue with the backend.
        int socketTime = 3000;  // 5sec time is given to register
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onItemClick(int position) {
//        Toast.makeText(getActivity(), "Position= "+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongItemClick(int position) {
//        Toast.makeText(getActivity(), "Position= "+position, Toast.LENGTH_SHORT).show();
        editButtonClick(position);
    }

    @Override
    public void editButtonClick(int position) {
    //        Toast.makeText(getActivity(), "Email = "+ arrayList.get(position).getEmail(), Toast.LENGTH_SHORT).show();
        String status = arrayList.get(position).getStatus();
        String complaintID = arrayList.get(position).getCid();
        String complaintDetail = arrayList.get(position).getC_detail();
        String name = arrayList.get(position).getName();
        String address = arrayList.get(position).getAddress();

        if(isStatusRegistered(status)==false){
            Toast.makeText(getActivity(), "Cannot edit an Assigned/Resolved complaint.", Toast.LENGTH_SHORT).show();
            return;
        }else showUpdateDialog(complaintID, name, address, complaintDetail);
    }

    @Override
    public void deleteButtonClick(int position) {
//        Toast.makeText(getActivity(), "Position= "+position, Toast.LENGTH_SHORT).show();
        String status = arrayList.get(position).getStatus();
        String complaintID = arrayList.get(position).getCid();
        if(isStatusRegistered(status)==false){
            Toast.makeText(getActivity(), "Cannot delete an Assigned/Resolved complaint.", Toast.LENGTH_SHORT).show();
            return;
        }else showDeleteDialog(complaintID, position);
    }

    private boolean isStatusRegistered(String status) {
        return status.equals("Registered");
    }
    private void showUpdateDialog(final String complaintID, String name, String address, String complaintDetail) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.edit_dialog_layout, null);

        final EditText nameEditText = alertLayout.findViewById(R.id.name);
        final EditText addressEditText = alertLayout.findViewById(R.id.address);
        final EditText complaint_detailEditText = alertLayout.findViewById(R.id.complaint_detail);

        nameEditText.setText(name);
        addressEditText.setText(address);
        complaint_detailEditText.setText(complaintDetail);

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(alertLayout)
                .setTitle("Edit Complaint")
                .setPositiveButton("Update",null)
                .setNegativeButton("Cancel", null)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String edited_name = nameEditText.getText().toString();
                        String edited_address = addressEditText.getText().toString();
                        String edited_complaint = complaint_detailEditText.getText().toString();

                        updateComplaintInDatabase(complaintID, edited_name, edited_address, edited_complaint);
//                        Toast.makeText(getActivity(), edited_name + " \n" + edited_address + "\n " +edited_complaint, Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }
    // Updating the complaint using API
    private void showDeleteDialog(String complaintID, int position) {

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Are you sure you want to delete the complaint ?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        getComplaints();
                    }
                })
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog)alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteComplaint(complaintID, position);
                        alertDialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();

    }

    private void deleteComplaint(String complaintID, int position) {


        String url = "https://crtsapp.herokuapp.com/api/complaint/"+complaintID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                Toast.makeText(getActivity(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                                arrayList.remove(position);
                                contentToBeDisplayed(); // shows empty message is arrayList is empty
                                complaintListAdapter.notifyItemRemoved(position);
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);

    }

    private void updateComplaintInDatabase(String complaintID, String name, String address, String complaint) {

        HashMap<String,String> body = new HashMap<String, String>();

        body.put("name", name);
        body.put("address", address);
        body.put("c_detail", complaint);
//        Toast.makeText(getActivity(), new JSONObject(body).toString(), Toast.LENGTH_SHORT).show();

        String url = "https://crtsapp.herokuapp.com/api/complaint/"+complaintID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(body),
                new Response.Listener<JSONObject>() {
                @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                getComplaints();
                                contentToBeDisplayed(); // shows empty message is arrayList is empty
                                Toast.makeText(getActivity(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    NetworkResponse response = error.networkResponse;
                    if(error instanceof ServerError && response!=null){
                        try {
                            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            JSONObject obj = new JSONObject(res);
                            getComplaints();
                            contentToBeDisplayed(); // shows empty message is arrayList is empty
                            Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();

                        }catch (JSONException | UnsupportedEncodingException jsonException){
                            jsonException.printStackTrace();
                        }
                    }
                }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", userToken);
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }
    private void contentToBeDisplayed(){
        if(arrayList.size() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
    }
}