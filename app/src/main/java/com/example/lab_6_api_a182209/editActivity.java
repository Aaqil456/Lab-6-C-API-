package com.example.lab_6_api_a182209;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class editActivity extends AppCompatActivity {
    Button btn_update , btn_delete,btn_back;
    EditText et_name;
    TextView tv_matrix;
    Spinner spn_club;
    ToggleButton tb_status;

    String member_matrix , member_name , club_id , member_status;

    ArrayAdapter<String> clubAdapter;
    ArrayList<String> club_list;
    Bundle bundle;

    ArrayList<String> club_id_list , club_name_list;
    public static final String API_GET_CLUB = "http://lrgs.ftsm.ukm.my/users/lam/get_club.php";
    public static final String API_UPDATE_MEMBER = "http://lrgs.ftsm.ukm.my/users/lam/update_member.php";
    public static final String API_DELETE_MEMBER = "http://lrgs.ftsm.ukm.my/users/lam/delete_member.php?matrix_no=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);
        btn_back=findViewById(R.id.btnBack);
        tv_matrix = findViewById(R.id.tv_matric);
        et_name = findViewById(R.id.et_name);
        tb_status = findViewById(R.id.tb_status);
        spn_club = findViewById(R.id.spn_club);

        bundle = getIntent().getExtras();
        member_matrix = bundle.getString("id");
        member_name = bundle.getString("name");
        club_id = bundle.getString("club_id");
        member_status = bundle.getString("status");

        tv_matrix.setText(member_matrix);
        et_name.setText(member_name);

        if(member_status.equals("Active")){
            tb_status.setChecked(true);
        }else{
            tb_status.setChecked(false);
        }
        club_id_list = new ArrayList<String>();
        club_name_list = new ArrayList<String>();
        club_list = new ArrayList<String>();
        populateClub();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(et_name.getText().toString()))
                {
                    et_name.setError("Name cannot be empty");
                }else
                {
                    member_matrix = tv_matrix.getText().toString();
                    member_name = et_name.getText().toString();
                    if(tb_status.isChecked()){
                        member_status="Active";

                    } else{
                        member_status="Not Active";
                    }
                    club_id = club_id_list.get(spn_club.getSelectedItemPosition());
                    UpdateMemberRequest();
                }


            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteMemberRequest();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(editActivity.this , MainActivity.class);
                startActivity(intent);
            }
        });


    }
    private void populateClub(){
        club_id_list.clear();
        club_name_list.clear();
        club_list.clear();
        StringRequest myReq = new StringRequest(Request.Method.GET, API_GET_CLUB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("result");

                    for (int i=0; i <jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id =jsonObject.getString("fld_club_id");
                        String name = jsonObject.getString("fld_club_name");

                        club_id_list.add(id);
                        club_name_list.add(name);
                        club_list.add(id + " , " + name);

                    }

                    clubAdapter = new ArrayAdapter<String>(editActivity.this ,android.R.layout.simple_spinner_item , club_list);
                    clubAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    spn_club.setAdapter(clubAdapter);

                    for(int i=0 ;i<club_id_list.size();i++)
                    {
                        if (club_id_list.get(i).equals(club_id))
                        {
                            spn_club.setSelection(i);
                            return;
                        }
                    }


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
    public class UpdateMember extends StringRequest
    {
        private Map<String , String> params;
        public UpdateMember(String matric , String name , String club , String status , Response.Listener<String> listener) {
            super(Method.POST, API_UPDATE_MEMBER, listener, null);

            params = new HashMap<>();
            params.put("matrix",matric);
            params.put("name",name);
            params.put("club_id",club);
            params.put("status",status);

        }

        @Nullable
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return params;
        }
    }
    private void UpdateMemberRequest(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success){
                        Toast.makeText(editActivity.this , "Update Success" , Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(editActivity.this , "Update Failed" , Toast.LENGTH_SHORT).show();
                } catch (JSONException e){
                    e.printStackTrace();
                }


            }
        };
        UpdateMember updateMember = new UpdateMember(member_matrix , member_name , club_id , member_status , responseListener);
        RequestQueue queue =Volley.newRequestQueue(getApplicationContext());
        queue.add(updateMember);
    }

    public class DeleteMember extends StringRequest{

        public DeleteMember(String matrix_no , Response.Listener<String> listener) {
            super(Method.DELETE, API_DELETE_MEMBER +matrix_no, listener, null);
        }
    }
    private void DeleteMemberRequest ()
    {
        Response.Listener responseListener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Toast.makeText(editActivity.this, "Delete Success", Toast.LENGTH_SHORT).show();
            }
        };
        DeleteMember deleteMember = new DeleteMember(member_matrix,responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(deleteMember);
    }
}