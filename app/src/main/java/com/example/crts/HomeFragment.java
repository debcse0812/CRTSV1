package com.example.crts;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
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
        Toast.makeText(getActivity(), "Position= "+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongItemClick(int position) {
        Toast.makeText(getActivity(), "Position= "+position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void editButtonClick(int position) {
        Toast.makeText(getActivity(), "Email = "+ arrayList.get(position).getEmail(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void deleteButtonClick(int position) {
        Toast.makeText(getActivity(), "Position= "+position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void doneButtonClick(int position) {
        Toast.makeText(getActivity(), "Position= "+position, Toast.LENGTH_SHORT).show();

    }
}