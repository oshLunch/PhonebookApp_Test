package com.cos.phoneapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity2";

    private RecyclerView rvPhone;
    private PhoneAdapter adapter;
    private FloatingActionButton fabAdd;
    private List<Phone> phones = new ArrayList<>();
    private PhoneService phoneService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initData();
        initEvent();
    }

    private void init() {
        rvPhone = findViewById(R.id.rv_phone);
        fabAdd = findViewById(R.id.fab_save);

        adapter = new PhoneAdapter(this);
        rvPhone.setAdapter(adapter);
        rvPhone.setLayoutManager(new LinearLayoutManager(this));

        phoneService = PhoneService.retrofit.create(PhoneService.class);
    }

    private void initData() {
        Call<CMRespDto<List<Phone>>> call = phoneService.findAll();
        call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
            @Override
            public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                CMRespDto<List<Phone>> cmRespDto = response.body();

                if (cmRespDto.getCode() == 1) {
                    phones = cmRespDto.getData();
                    adapter.setItems(phones);
                }
            }

            @Override
            public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {
                Log.d(TAG, "findAll() Fail");
            }
        });
    }

    private void initEvent() {
        // 추가버튼 기능
        fabAdd.setOnClickListener(v -> {
            View dialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tel_detail, null);
            AlertDialog.Builder dig = new AlertDialog.Builder(MainActivity.this);

            CircleImageView ivImg = dialog.findViewById(R.id.civ_image);
            ivImg.setImageResource(R.drawable.ic_person);
            EditText etName = dialog.findViewById(R.id.et_name);
            EditText etPhone = dialog.findViewById(R.id.et_tel);

            dig.setTitle("새 연락처 등록");
            dig.setView(dialog);

            dig.setNegativeButton("닫기", null);
            dig.setPositiveButton("저장", (dialog1, which) -> {
                if (etName.getText().toString().equals("") || etPhone.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "내용이 입력되지 않았습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Phone phone = new Phone();
                    phone.setName(etName.getText().toString());
                    phone.setTel(etPhone.getText().toString());
                    Call<CMRespDto<Phone>> call = phoneService.save(phone);
                    call.enqueue(new Callback<CMRespDto<Phone>>() {
                        @Override
                        public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                            CMRespDto<?> cmRespDto = response.body();
                            if (cmRespDto.getCode() == 1) {
                                Toast.makeText(MainActivity.this, "등록되었습니다", Toast.LENGTH_SHORT).show();
                                adapter.addItem(phone);
                                initData();
                            }
                        }

                        @Override
                        public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "등록 실패", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "save() 실패 : " + t.getMessage());
                        }
                    });
                }
            });
            dig.show();
        });

        // Swipe 삭제 기능
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Call<CMRespDto<Phone>> call = phoneService.deleteById(phones.get(viewHolder.getAdapterPosition()).getId());
                call.enqueue(new Callback<CMRespDto<Phone>>() {
                    @Override
                    public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                        CMRespDto<?> cmRespDto = response.body();
                        if (cmRespDto.getCode() == 1) {
                            Toast.makeText(MainActivity.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                            adapter.removeItem(viewHolder.getAdapterPosition());
                        }
                    }

                    @Override
                    public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "deleteById() 실패 : " + t.getMessage());
                    }
                });


            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvPhone);
    }

    public List<Phone> getContactList() {
        return phones;
    }

    public void updateContact(Phone phone, int position) {
        View dialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tel_detail, null);
        CircleImageView ivImg = dialog.findViewById(R.id.civ_image);
        ivImg.setImageResource(R.drawable.ic_person);
        EditText etName = dialog.findViewById(R.id.et_name);
        EditText etPhone = dialog.findViewById(R.id.et_tel);

        Call<CMRespDto<Phone>> call = phoneService.findById(phone.getId());
        call.enqueue(new Callback<CMRespDto<Phone>>() {
            @Override
            public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                CMRespDto<?> cmRespDto = response.body();
                if (cmRespDto.getCode() == 1) {
                    Phone contactEntity = (Phone) cmRespDto.getData();
                    etName.setText(contactEntity.getName());
                    etPhone.setText(contactEntity.getTel());
                }
            }

            @Override
            public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "상세보기 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "findById() 실패 : " + t.getMessage());
            }
        });
        AlertDialog.Builder dig = new AlertDialog.Builder(MainActivity.this);
        dig.setTitle("연락처 수정");
        dig.setView(dialog);
        dig.setNegativeButton("삭제", (dialog1, which) -> {
            Call<CMRespDto<Phone>> callDelete = phoneService.deleteById(phone.getId());
            callDelete.enqueue(new Callback<CMRespDto<Phone>>() {
                @Override
                public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                    CMRespDto<?> cmRespDto = response.body();
                    if (cmRespDto.getCode() == 1) {
                        adapter.removeItem(position);
                        Toast.makeText(MainActivity.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "deleteById() 실패 : " + t.getMessage());
                }
            });
        });
        dig.setPositiveButton("변경", (dialog1, which) -> {
            Phone mContact = new Phone();
            mContact.setName(etName.getText().toString());
            mContact.setTel(etPhone.getText().toString());
            Call<CMRespDto<Phone>> callUpdate = phoneService.update(phone.getId(), mContact);
            callUpdate.enqueue(new Callback<CMRespDto<Phone>>() {
                @Override
                public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                    CMRespDto<?> cmRespDto = response.body();
                    if (cmRespDto.getCode() == 1) {
                        adapter.setItem(position, mContact);
                        Toast.makeText(MainActivity.this, "수정되었습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "수정 실패", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "update() 실패 : " + t.getMessage());
                }
            });
        });
        dig.show();
    }
}