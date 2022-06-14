 package com.example.lab_6_api_a182209;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

 public class MainActivity extends AppCompatActivity { Button btn_add;
    TextInputEditText et_matric,et_name;
    Spinner spn_club;
    ToggleButton tb_status;
    ListView lv_member;

    ArrayAdapter<String> clubAdapter,memberAdapter;
    //array list for club
    ArrayList<String>  club_id_list,club_name_list;
    ArrayList<String> club_list;

     //array list for member
     ArrayList<String>  member_list;
     ArrayList<Member> member_list_object;

     String matric,club_id,member_id,member_name,member_status,id;
     String name,status;

     Bundle bundle;

     public static final String API_GET_CLUB = "http://lrgs.ftsm.ukm.my/users/lam/get_club.php";
     public static final String API_ADD_MEMBER = "http://lrgs.ftsm.ukm.my/users/lam/add_member.php";
     public static final String API_GET_MEMBER = "http://lrgs.ftsm.ukm.my/users/lam/view_member.php";


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_add=findViewById(R.id.btn_add);
        et_matric=findViewById(R.id.et_matric_num);
        et_name=findViewById(R.id.et_name);

        spn_club=findViewById(R.id.spinnerClub);
        tb_status=findViewById(R.id.tb_status);
        lv_member=findViewById(R.id.lv_member);
        memberAdapter = new ArrayAdapter<String>(MainActivity.this , android.R.layout.simple_list_item_1);



         club_id_list=new ArrayList<>();
        club_name_list=new ArrayList<>();
        club_list=new ArrayList<>();

        member_list=new ArrayList<>();
        member_list_object= new ArrayList<Member>();


         btn_add.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(TextUtils.isEmpty(et_matric.getText())|| TextUtils.isEmpty(et_name.getText().toString()))
                 {
                     if(TextUtils.isEmpty(et_matric.getText().toString()))
                     {
                         et_matric.setError("Matrix cannot be empty");
                     }
                     if(TextUtils.isEmpty(et_name.getText().toString()))
                     {
                         et_name.setError("Name cannot be empty");
                     }
                 }else
                 {
                     matric =et_matric.getText().toString();
                     member_name =et_name.getText().toString();

                     if(tb_status.isChecked()) {
                         member_status = "Active";
                     }else
                     {
                         member_status= "Not Active";


                     }
                     club_id = club_id_list.get(spn_club.getSelectedItemPosition());
                     AddMember ();

                     //assign all the data to the variable
                     //call the add_member API using volley
                 }
             }
         });
         lv_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                 Intent intent = new Intent(MainActivity.this , editActivity.class);

                 member_id = member_list_object.get(position).getMatric();
                 member_name = member_list_object.get(position).getName();
                 club_id = member_list_object.get(position).getClubID();
                 member_status = member_list_object.get(position).getStatus();

                 bundle = new Bundle ();
                 bundle.putString("id" , member_id);
                 bundle.putString("name" , member_name);
                 bundle.putString("club_id" , club_id);
                 bundle.putString("status" , member_status);
                 intent.putExtras(bundle);
                 startActivity(intent);
             }
         });

    }

     @Override
     protected void onResume() {
         super.onResume();
         populateClub();
         populateMembers();

     }

     private void AddMember()
     {
         StringRequest myReq = new StringRequest(Request.Method.POST, API_ADD_MEMBER, new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {
                 populateMembers();
             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {

             }

         }){ //this space for to send the data inside body section

             @Override
             protected Map<String, String> getParams() throws AuthFailureError {
                 Map<String , String>params = new HashMap<String, String>();
                 params.put("matrix",matric);
                 params.put("name",member_name);
                 params.put("club_id",club_id);
                 params.put("status",member_status);

                 return params;
             }
         };
         RequestQueue queue = Volley.newRequestQueue(this);
         queue.add(myReq);
     }
     private void populateMembers(){
         member_list.clear();
         member_list_object.clear();
         memberAdapter.clear();

         StringRequest myReq = new StringRequest(Request.Method.GET, API_GET_MEMBER, new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {
                 try {
                     JSONObject jsonResponse = new JSONObject(response);
                     JSONArray jsonArray =jsonResponse.getJSONArray("result");

                     for (int i =0;i<jsonArray.length();i++)
                     {
                         JSONObject jo = jsonArray.getJSONObject(i);

                         id= jo.getString("fld_member_id");

                         String matric =jo.getString("fld_matrix_no");
                         String name =jo.getString("fld_member_name");
                         String club =jo.getString("fld_club_id");
                         String status =jo.getString("fld_status");

                         Member member = new Member(matric , name , club , status , id);
                         member_list_object.add(member);
                         member_list.add(matric +" , "+name +" , " +club +" , "+status);

                     }
                     memberAdapter.addAll(member_list);
                     lv_member.setAdapter(memberAdapter);



                 } catch (JSONException e){
                     e.printStackTrace();
                 }

             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {


             }
         });

         RequestQueue queue = Volley.newRequestQueue(this);
         queue.add(myReq);


     }


     private void populateClub(){

         club_id_list.clear();
         club_name_list.clear();
         club_list.clear();

         StringRequest myReq=new StringRequest(Request.Method.GET, API_GET_CLUB, new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {
                 try {
                     JSONObject jsonResponse = new JSONObject(response);
                     JSONArray jsonArray = jsonResponse.getJSONArray("result");

                     for(int i=0;i<jsonArray.length();i++){
                         JSONObject jsonObject= jsonArray.getJSONObject(i);
                         String id = jsonObject.getString("fld_club_id");
                         String name = jsonObject.getString("fld_club_name");

                         club_id_list.add(id);
                         club_name_list.add(name);
                         club_list.add(id+" , "+name);
                     }
                    clubAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item,club_list);
                    clubAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spn_club.setAdapter(clubAdapter);


                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {

             }
         });

         RequestQueue queue= Volley.newRequestQueue(this);
         queue.add(myReq);
     }
 }