package com.example.expensemanagerapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanagerapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;


public class DashboardFragment extends Fragment {



 //neu loi thi quay lai day :v
    //floating button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //floating textview..
    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    //boolean
    private boolean isOpen=false;

    //Animation
    private Animation FadOpen,FadeClose;

    //dashboard income and expense result..
    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //recycler view
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_dashboard, container, false);


        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase=FirebaseDatabase.getInstance().getReference().child("ExpenseDatabse").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        //connect floating button to layout

        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_Ft_btn);

        //connect floating textview

        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);

        //total income and expense result set..

        totalIncomeResult=myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_resulr);

        //Recycler
        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_epense);

        //connect animation..
        FadOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose=AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addData();

                if(isOpen){
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;
                }else {
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;

                }

            }
        });

        //calculate total income..

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {

                int totalsum=0;

                for (DataSnapshot mysnap:snapshot.getChildren()){
                    Data data=mysnap.getValue(Data.class);

                    totalsum+=data.getAmount();

                    String stResult=String.valueOf(totalsum);

                    totalIncomeResult.setText(stResult+".000");
                }

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

        //calculate total expense..
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                int totalsum = 0;

                for (DataSnapshot mysnapshot:snapshot.getChildren()){

                    Data data=mysnapshot.getValue(Data.class);
                    totalsum+=data.getAmount();

                    String strTotalSum=String.valueOf(totalsum);

                    totalExpenseResult.setText(strTotalSum+".000");
                }

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });



        //Recycler
        LinearLayoutManager layoutManagerIncome=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);




        return myview;
    }

    //floating button animation
    private void ftAnimation(){

        if(isOpen){
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;
        }else {
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;

        }

    }

    private  void addData(){

        //fab Button income...
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();

            }
        });

    }

    public void incomeDataInsert(){
       androidx.appcompat.app.AlertDialog.Builder mydialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);
        final EditText edtAmmount=myview.findViewById(R.id.ammount_edt);
        final Spinner itemspinner=myview.findViewById(R.id.iteminsert);
        final EditText edtNote=myview.findViewById(R.id.note_edt);

        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCansel=myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type=itemspinner.getSelectedItem().toString().trim();
                String ammount=edtAmmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();

                if(type.equals("Select")){
                    Toast.makeText( getActivity() , "select a valid", Toast.LENGTH_LONG).show();
                }
                if(TextUtils.isEmpty(ammount)){
                    edtAmmount.setError("Required Field..");
                    return;
                }

                int ourammontint=Integer.parseInt(ammount);

                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Required Field..");
                    return;
                }

                String id=mIncomeDatabase.push().getKey();
                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data =new Data(ourammontint,type,note,id,mDate);
                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Date ADDED", Toast.LENGTH_LONG).show();

                ftAnimation();
                dialog.dismiss();



            }
        });

        btnCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void expenseDataInsert(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);

        final AlertDialog dialog=mydialog.create();

        dialog.setCancelable(false);
        final EditText ammount=myview.findViewById(R.id.ammount_edt);
        final Spinner itemspinner=myview.findViewById(R.id.iteminsert);
        final EditText note=myview.findViewById(R.id.note_edt);

        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCansel=myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tmAmmount= ammount.getText().toString().trim();
                String tmtype= itemspinner.getSelectedItem().toString().trim();
                String tmnote= note.getText().toString().trim();

                if (TextUtils.isEmpty(tmAmmount)){
                    ammount.setError("Required Field..");
                    return;
                }

                int inamount=Integer.parseInt(tmAmmount);

                if(tmtype.equals("Select")){
                    Toast.makeText( getActivity() , "select a valid", Toast.LENGTH_LONG).show();
                }

                if(TextUtils.isEmpty(tmnote)){
                    note.setError("Required Field..");
                    return;
                }

                String id=mExpenseDatabase.push().getKey();
                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data = new Data(inamount,tmtype,tmnote,id,mDate);
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data added",Toast.LENGTH_LONG).show();

                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options=
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeDatabase,Data.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<Data, IncomeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(options) {
            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new IncomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income,parent,false));
            }
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {

                holder.setIncomeAmmount(model.getAmount());
                holder.setIncomeType(model.getType());
                holder.setIncomeDate(model.getDate());

            }
        };
        mRecyclerIncome.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();



        FirebaseRecyclerOptions<Data> changer=
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mExpenseDatabase,Data.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> firebaseRecyclerAdapter1=new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(changer) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull DashboardFragment.ExpenseViewHolder holder, int position, @NonNull @NotNull Data model) {
                holder.setExpenseType(model.getType());
                holder.setExpenseDate(model.getDate());
                holder.setExpenseAmmount(model.getAmount());

            }
            @NonNull
            @NotNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                return new ExpenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense,parent, false));
            }
        };

        mRecyclerExpense.setAdapter(firebaseRecyclerAdapter1);
        firebaseRecyclerAdapter1.startListening();


    }



    //For Income Data
    class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;

        public IncomeViewHolder( View itemView) {
            super(itemView);
            mIncomeView=itemView;
        }

        public void setIncomeType(String type){
            TextView mtype=mIncomeView.findViewById(R.id.type_Income_ds);
            mtype.setText(type);
        }
        public void  setIncomeAmmount(int ammount){
            TextView mAmmount=mIncomeView.findViewById(R.id.ammount_income_ds);
            String strAmmount=String.valueOf(ammount);
            mAmmount.setText(strAmmount);
        }

        public  void  setIncomeDate(String date){
            TextView mDate=mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }
    }

    //For Expense data
    class  ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;

        public ExpenseViewHolder( View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }

        public void setExpenseType(String type){
            TextView mtype=mExpenseView.findViewById(R.id.type_expense_ds);
            mtype.setText(type);
        }
        public void setExpenseAmmount(int ammount){
            TextView mAmmount=mExpenseView.findViewById(R.id.ammount_expense_ds);
            String strAmmount=String.valueOf(ammount);
            mAmmount.setText(strAmmount);
        }
        public void setExpenseDate(String date){
            TextView mdate=mExpenseView.findViewById(R.id.date_expense_ds);
            mdate.setText(date);
        }
    }
}