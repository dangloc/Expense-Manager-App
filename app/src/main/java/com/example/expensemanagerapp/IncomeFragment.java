package com.example.expensemanagerapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanagerapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


/*
 * A simple {@link Fragment} subclass.
 * Use the {@link IncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncomeFragment extends Fragment {

    //Firebase DB
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //RecyclerView
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;

    //Text view
    private TextView incomeTotalSum;

    ///update edit text
    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;

    ///button for update and delete
    private Button btnUpdate;
    private Button btnDelete;

    //data item value
    private String type;
    private String note;
    private int amount;

    private String post_key;

    /**   // TODO: Rename parameter arguments, choose names that match
     // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
     private static final String ARG_PARAM1 = "param1";
     private static final String ARG_PARAM2 = "param2";

     // TODO: Rename and change types of parameters
     private String mParam1;
     private String mParam2;

     public IncomeFragment() {
     // Required empty public constructor
     }

     /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncomeFragment.
     */
    // TODO: Rename and change types and number of parameters

    /**
     * public static IncomeFragment newInstance(String param1, String param2) {
     * IncomeFragment fragment = new IncomeFragment();
     * Bundle args = new Bundle();
     * args.putString(ARG_PARAM1, param1);
     * args.putString(ARG_PARAM2, param2);
     * fragment.setArguments(args);
     * return fragment;
     * }
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**if (getArguments() != null) {
         //mParam1 = getArguments().getString(ARG_PARAM1);
         //mParam2 = getArguments().getString(ARG_PARAM2);
         }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_income, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        incomeTotalSum=myview.findViewById(R.id.income_txt_result);

        recyclerView = myview.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {

                int totalvalue = 0;
                for (DataSnapshot mysanapshot:snapshot.getChildren()){

                    Data data=mysanapshot.getValue(Data.class);

                    totalvalue+=data.getAmount();

                    String stTotalvalue=String.valueOf(totalvalue);

                    incomeTotalSum.setText(stTotalvalue+".000");
                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

        return myview;
    }




    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options=
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeDatabase,Data.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<Data, MyViewHolder> firebaseRecyclelerAdapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data,parent,false));
            }
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setAmmount(model.getAmount());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        post_key=getRef(position).getKey();

                        type=model.getType();
                        note=model.getNote();
                        amount= model.getAmount();

                        updateDataItem();
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclelerAdapter);
    }
    class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        void setNote(String note) {

            TextView mNote = mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        void setAmmount(int ammount) {
            TextView mAmmount = mView.findViewById(R.id.ammount_txt_income);
            String stammount = String.valueOf(ammount);
            mAmmount.setText(stammount);
        }

    }

    private void updateDataItem(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= LayoutInflater.from(getActivity());
        View myview= inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        edtAmmount=myview.findViewById(R.id.ammount_edt);
        edtType=myview.findViewById(R.id.type_edt);
        edtNote=myview.findViewById(R.id.note_edt);

        //set data to edit text..
        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(String.valueOf(amount));
        edtAmmount.setSelection(String.valueOf(amount).length());

        btnUpdate=myview.findViewById(R.id.btn_upd_Update);
        btnDelete=myview.findViewById(R.id.btnuDP_Delete);

        final AlertDialog dialog=mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();

                String mdammount=String.valueOf(amount);

                mdammount=edtAmmount.getText().toString().trim();

                int myAmmount=Integer.parseInt(mdammount);

                String mDate= DateFormat.getTimeInstance().format(new Date());

                Data data=new Data(myAmmount, type, note,post_key, mDate);

                mIncomeDatabase.child(post_key).setValue(data);

                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIncomeDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}



