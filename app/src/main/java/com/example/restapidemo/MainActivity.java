package com.example.restapidemo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button btnInsert, btnUpdate, btnDelete, btnShow;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        btnInsert = findViewById(R.id.btnInsert);
        database = FirebaseFirestore.getInstance();
        tvResult = findViewById(R.id.result);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnShow = findViewById(R.id.btnShow);

        btnInsert.setOnClickListener(v -> {
            insertFirebase(tvResult);
        });

        btnUpdate.setOnClickListener(v -> {
            updateFirebase(tvResult);
        });
        btnDelete.setOnClickListener(v -> {
            deleteFirebase(tvResult);
        });
        btnShow.setOnClickListener(v -> {
            showDataFirebase(tvResult);
        });
    }

    String id = "";
    ToDo toDo = null;
    FirebaseFirestore database;

    public void insertFirebase(TextView tvResult) {
        id = UUID.randomUUID().toString(); // Chọn random id
        toDo = new ToDo(id, "title", "content 1 ");
        // Chuyển đổi sang đối tượng có thể thao tác với Firebase
        HashMap<String, Object> mapTodo = toDo.converHashMap();
        // Insert vào database
        database.collection("ToDo").document(id)
                .set(mapTodo) // Đối tượng cần insert
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        tvResult.setText("Thêm thành công, ID: " + id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        tvResult.setText(e.getMessage());
                    }
                });
    }

    public void updateFirebase(TextView tvResult) {
        database.collection("ToDo")
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            id = document.getId();
                            toDo = new ToDo(id, "Sửa Title 1", "Content 1");
                            database.collection("ToDo").document(toDo.getId())
                                    .update(toDo.converHashMap())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            tvResult.setText("Sửa thành công, ID: " + id);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            tvResult.setText(e.getMessage());
                                        }
                                    });
                        } else {
                            tvResult.setText("Không tìm thấy sản phẩm để sửa");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        tvResult.setText(e.getMessage());
                    }
                });
    }

    public void deleteFirebase(TextView tvResult) {
        database.collection("ToDo")
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            id = document.getId();
                            database.collection("ToDo").document(id)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            tvResult.setText("Xóa thành công, ID: " + id);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            tvResult.setText(e.getMessage());
                                        }
                                    });
                        } else {
                            tvResult.setText("Không tìm thấy sản phẩm để xóa");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        tvResult.setText(e.getMessage());
                    }
                });
    }

    String strResult;

    public ArrayList<ToDo> showDataFirebase(TextView tvResult) {
        ArrayList<ToDo> list = new ArrayList<>();
        database.collection("ToDo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            strResult = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ToDo toDo1 = document.toObject(ToDo.class);
                                strResult += "id :" + toDo1.getId() + "\n";
                                list.add(toDo1);
                            }
                            tvResult.setText(strResult);
                        } else {
                            tvResult.setText("Fail");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        tvResult.setText(e.getMessage());
                    }
                });
        return list;
    }
}
