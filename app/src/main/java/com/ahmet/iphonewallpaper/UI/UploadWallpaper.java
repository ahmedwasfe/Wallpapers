package com.ahmet.iphonewallpaper.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UploadWallpaper extends AppCompatActivity {


//    private ImageView imageUpload;
//    private MaterialSpinner categorySpinner;
//    private Button mBrowser,mUpload;
//    private Toolbar mToolbar;
//
//    private Uri filepath;
//    private String categorySelected;
//
//    private FirebaseStorage firebaseStorage;
//    private StorageReference storageReference;
//
//    Map<String,String> spinnerDataMap = new HashMap<>();
//
//    private SaveSettings saveSettings;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        saveSettings = new SaveSettings(this);
//        if (saveSettings.getNightModeState() == true){
//            setTheme(R.style.DarkTheme);
//        }else {
//            setTheme(R.style.AppTheme);
//        }
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_upload_wallpaper);
//
//        mToolbar = findViewById(R.id.toolbar_upload);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("Upload ViewWallpapersFavorite");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        imageUpload = findViewById(R.id.image_upload);
//        categorySpinner = findViewById(R.id.spinner);
//        mBrowser = findViewById(R.id.btn_browser);
//        mUpload = findViewById(R.id.btn_upload);
//
//        //Firebase Storage
//        firebaseStorage = FirebaseStorage.getInstance();
//        storageReference = firebaseStorage.getReference();
//
//        // load Spinner data
//        loadCategoryToSpinner();
//
//
//        // Button Event
//        mBrowser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseImage();
//            }
//        });
//
//        mUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (categorySpinner.getSelectedIndex() == 0){
//                    Toast.makeText(UploadWallpaper.this, getString(R.string.Choose_category), Toast.LENGTH_SHORT).show();
//                }else {
//                    uploadImage();
//                }
//            }
//        });
//
//    }
//
//    private void uploadImage() {
//
//        if (filepath != null){
//            ProgressDialog progressDialog = new ProgressDialog(this);
//            final AlertDialog dialog = new SpotsDialog(this);
//            dialog.setTitle(("Uploading..."));
//            dialog.show();
//
//            StorageReference mRef = storageReference.child(new StringBuilder("images/")
//                    .append(UUID.randomUUID().toString())
//                    .toString());
//
//            mRef.putFile(filepath)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            dialog.dismiss();
//                            saveUrlToCategory(categorySelected,
//                                    taskSnapshot.getUploadSessionUri().toString());
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            dialog.dismiss();
//                            Toast.makeText(UploadWallpaper.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
//                            dialog.setMessage(getString(R.string.upload)+" : " + (int)progress + "%" );
//                        }
//                    });
//
//        }
//    }
//
//    private void saveUrlToCategory(String categorySelected, String imageUrl) {
//
//        FirebaseDatabase.getInstance()
//                .getReference(Common.STR_WALLPAPER)
//                .push() // Gen Key
//                .setValue(new WallpaperItem(imageUrl, categorySelected))
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(UploadWallpaper.this, getString(R.string.success_uploaded), Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                });
//
//    }
//
//    private void chooseImage() {
//
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Image : "), Common.PICK_IMAGE_REQUEST2);
//
//    }
//
//    private void loadCategoryToSpinner() {
//
//        FirebaseDatabase.getInstance()
//                .getReference(Common.STR_CATEGORY_BG)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                            Category category = snapshot.getValue(Category.class);
//                            String key = snapshot.getKey();
//                            spinnerDataMap.put(key, category.getName());
//                        }
//                        // because material spinner with not receive hint so we need custom hint
//                        // Thid id my Tip ^^
//                        Object[] valueArray = spinnerDataMap.values().toArray();
//                        List<Object> valueList = new ArrayList<>();
//                        valueList.add(0, "Category"); // We Will add first item is Hint
//                        valueList.addAll(Arrays.asList(valueArray)); // And add all remain category name
//                        categorySpinner.setItems(valueList); // set source data for spinner
//                        categorySpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
//                                // when user choose category , we will get categoryId (key)
//                                Object[] keyArray = spinnerDataMap.keySet().toArray();
//                                List<Object> keyList = new ArrayList<>();
//                                keyList.add(0, "Category_Key");
//                                keyList.addAll(Arrays.asList(keyArray));
//
//                                categorySelected = keyList.get(position).toString(); // Asigne Key when user choose Category
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == Common.PICK_IMAGE_REQUEST2
//                && resultCode == RESULT_OK
//                && data != null
//                && data.getData() != null){
//
//            filepath = data.getData();
//
//            try {
//                Bitmap  bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
//                imageUpload.setImageBitmap(bitmap);
//                mUpload.setEnabled(true);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
}
