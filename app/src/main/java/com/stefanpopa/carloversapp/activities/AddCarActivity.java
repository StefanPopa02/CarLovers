package com.stefanpopa.carloversapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.ui.BrandAdapter;
import com.stefanpopa.carloversapp.model.BrandItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCarActivity extends AppCompatActivity {

    private ArrayList<BrandItem> brandItems;
    private BrandAdapter brandAdapter;
    private FirebaseFirestore db;
    private Spinner brandSpinner;
    private Spinner modelSpinner;
    private Spinner yearSpinner;
    private Spinner versionSpinner;
    private Spinner engineSpinner;
    private BrandItem brandClickedItem;
    private ProgressDialog pd;
    private ImageView carImage;
    private String letterFinal;
    private String clickedBrandNameFinal;
    private String clickedModelNameFinal;
    private String clickedYearNameFinal;
    private String clickedVersionNameFinal;
    private String carImageUrlFinal;
    private String carLogoUrlFinal;
    private String caracteristiciFinal;
    private String engineFinal;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait");
        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (letterFinal != null && clickedBrandNameFinal != null && clickedModelNameFinal != null && clickedYearNameFinal != null && clickedVersionNameFinal != null &&
                        carImageUrlFinal != null && carLogoUrlFinal != null && caracteristiciFinal != null && engineFinal != null) {
                    Log.d("ADD_CAR_ACTIVITY", "Letter " + letterFinal + "\n Brand " + clickedBrandNameFinal
                            + "\n Model " + clickedModelNameFinal + "\n Year " + clickedYearNameFinal + " \n ImageUrl " + carImageUrlFinal + " \n LogoUrl " +
                            carLogoUrlFinal + " \n engine " + engineFinal + " \n Caracteristici " + caracteristiciFinal);

                    Intent i = new Intent(AddCarActivity.this, ConfirmAddCarActivity.class);
                    i.putExtra("Letter",letterFinal);
                    i.putExtra("Brand", clickedBrandNameFinal);
                    i.putExtra("Model", clickedModelNameFinal);
                    i.putExtra("Year", clickedYearNameFinal);
                    i.putExtra("Version", clickedVersionNameFinal);
                    i.putExtra("Engine", engineFinal);
                    i.putExtra("Caracteristici", caracteristiciFinal);
                    i.putExtra("CarPhoto", carImageUrlFinal);
                    i.putExtra("BrandLogo", carLogoUrlFinal);
                    startActivity(i);
                    finish();

                } else {
                    Toast.makeText(AddCarActivity.this, "Check all fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        carImage = findViewById(R.id.car_image_confirm);
        brandSpinner = findViewById(R.id.spinner_brand);
        modelSpinner = findViewById(R.id.text_view_model);
        yearSpinner = findViewById(R.id.text_view_year);
        versionSpinner = findViewById(R.id.spinner_version);
        engineSpinner = findViewById(R.id.spinner_engine);
        db = FirebaseFirestore.getInstance();
        initBrandList();
    }

    private void initBrandList() {
        pd.show();
        Task<QuerySnapshot> task = db.collection("carBrands").get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    brandItems = new ArrayList<>();
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    for (DocumentSnapshot documentSnapshot : documents) {
                        brandItems.add(new BrandItem(documentSnapshot.get("Brand").toString().trim().replace("_", " "), (String) documentSnapshot.get("logoImgURL")));
                    }
                    setBrandSpinnerAdapter();
                }
            }
        });
    }

    private void setBrandSpinnerAdapter() {
        brandAdapter = new BrandAdapter(AddCarActivity.this, brandItems);
        brandSpinner.setAdapter(brandAdapter);
        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                brandClickedItem = (BrandItem) parent.getItemAtPosition(position);
                String clickedBrandName = brandClickedItem.getBrandName();
                initModelList(clickedBrandName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        pd.dismiss();
    }

    private void initModelList(String clickedBrandName) {

        List<String> modelList = new ArrayList<>();
        char letter = clickedBrandName.charAt(0);

        Task<QuerySnapshot> task = db.collection("carsData").document(String.valueOf(letter)).collection(clickedBrandName).get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    for (DocumentSnapshot doc : documents) {
                        modelList.add(doc.getId());
                    }
                    setModelSpinnerAdapter(letter, clickedBrandName, modelList);
                }
            }
        });

    }

    private void setModelSpinnerAdapter(char letter, String clickedBrandName, List<String> modelList) {
        ArrayAdapter<CharSequence> modelAdapter = new ArrayAdapter(AddCarActivity.this, android.R.layout.simple_spinner_item, modelList);
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(modelAdapter);
        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String clickedModelName = (String) parent.getItemAtPosition(position);
                initYearList(letter, clickedBrandName, clickedModelName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initYearList(char letter, String clickedBrandName, String clickedModelName) {
        DocumentReference current_db = db.collection("carsData").document(String.valueOf(letter)).collection(clickedBrandName).document(clickedModelName);
        current_db.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    Map<String, Object> docMap = doc.getData();
                    List<Object> yearsPlaceHolder = (ArrayList<Object>) docMap.get("years");
                    List<String> years = new ArrayList<>();
                    for (Object obj : yearsPlaceHolder) {
                        if (obj instanceof String) {
                            years.add((String) obj);
                        }
                    }
                    years.sort((year1, year2) -> Integer.valueOf(year1.substring(0, 4)).compareTo(Integer.valueOf(year2.substring(0, 4))));
                    setYearSpinnerAdapter(letter, clickedBrandName, clickedModelName, years);
                }
            }
        });
    }

    private void setYearSpinnerAdapter(char letter, String clickedBrandName, String clickedModelName, List<String> yearsList) {
        ArrayAdapter<CharSequence> yearAdapter = new ArrayAdapter(AddCarActivity.this, android.R.layout.simple_spinner_item, yearsList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String clickedYearName = (String) parent.getItemAtPosition(position);
                initVersionList(letter, clickedBrandName, clickedModelName, clickedYearName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initVersionList(char letter, String clickedBrandName, String clickedModelName, String clickedYearName) {
        List<String> versionlist = new ArrayList<>();
        CollectionReference current_db = db.collection("carsData").document(String.valueOf(letter)).collection(clickedBrandName).document(clickedModelName).collection(clickedYearName);
        current_db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    for (DocumentSnapshot doc : documents) {
                        versionlist.add(doc.getId());
                    }
                    setVersionSpinnerAdapter(letter, clickedBrandName, clickedModelName, clickedYearName, versionlist);
                }
            }
        });
    }

    private void setVersionSpinnerAdapter(char letter, String clickedBrandName, String clickedModelName, String clickedYearName, List<String> versionList) {
        ArrayAdapter<CharSequence> versionAdapter = new ArrayAdapter(AddCarActivity.this, android.R.layout.simple_spinner_item, versionList);
        versionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        versionSpinner.setAdapter(versionAdapter);
        versionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String clickedVersionName = (String) parent.getItemAtPosition(position);
                initEngineList(letter, clickedBrandName, clickedModelName, clickedYearName, clickedVersionName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initEngineList(char letter, String clickedBrandName, String clickedModelName, String clickedYearName, String clickedVersionName) {
        DocumentReference current_db = db.collection("carsData").document(String.valueOf(letter)).collection(clickedBrandName).document(clickedModelName).collection(clickedYearName).document(clickedVersionName);
        current_db.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    String imgUrl = (String) doc.get("imgUrl");
                    setCarPhoto(imgUrl);
                    //set engines
                    Map<String, Object> docMap = doc.getData();
                    List<Object> enginesContent = (List<Object>) docMap.get("Engines:");
                    HashMap<String, String> enginesHashMapTmp = new HashMap<>();
                    HashMap<String, String> enginesHashMap = new HashMap<>();
                    for (Object obj : enginesContent) {
                        enginesHashMapTmp = (HashMap<String, String>) obj;
                        String caracteristici = enginesHashMapTmp.get("Caracteristici");
                        String engine = enginesHashMapTmp.get("Engine");
                        enginesHashMap.put(engine, caracteristici);
                    }
                    setEngineSpinnerAdapter(letter, clickedBrandName, clickedModelName, clickedYearName, clickedVersionName, enginesHashMap, imgUrl);
                }
            }
        });
    }

    private void setEngineSpinnerAdapter(char letter, String clickedBrandName, String clickedModelName, String clickedYearName, String clickedVersionName, HashMap<String, String> enginesHashMap, String imgUrl) {
        List<String> engineList = new ArrayList<>();
        for (String key : enginesHashMap.keySet()) {
            engineList.add(key);
        }
        ArrayAdapter<CharSequence> engineAdapter = new ArrayAdapter(AddCarActivity.this, android.R.layout.simple_spinner_item, engineList);
        engineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        engineSpinner.setAdapter(engineAdapter);
        engineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String clickedEngineName = (String) parent.getItemAtPosition(position);
                letterFinal = String.valueOf(letter);
                clickedBrandNameFinal = clickedBrandName;
                clickedModelNameFinal = clickedModelName;
                clickedYearNameFinal = clickedYearName;
                clickedVersionNameFinal = clickedVersionName;
                caracteristiciFinal = enginesHashMap.get(clickedEngineName);
                engineFinal = clickedEngineName;
                carImageUrlFinal = imgUrl;
                carLogoUrlFinal = brandClickedItem.getBrandLogoImgURL();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setCarPhoto(String imgUrl) {
        Picasso.get().load(imgUrl).into(carImage);
    }
}