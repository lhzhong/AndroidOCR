package com.lhzhong.ocr_test4;


import java.io.File;
import java.io.FileNotFoundException;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final int PHOTO_CAPTURE = 0x11;
	private static final int PHOTO_RESULT = 0x12;

	private static String LANGUAGE = "eng";
	private static String IMG_PATH = getSDPath() + "tessdata/";

	private static TextView tvResult;
	private static EditText edResult;
	private static ImageView ivSelected;
	private static Button btnCamera;
	private static Button btnSelect;
	private static RadioGroup radioGroup;
	private static String textResult;
	private static Bitmap bitmapSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		File path = new File(IMG_PATH);
		if (!path.exists()) {
			path.mkdirs();
		}

		tvResult = (TextView) findViewById(R.id.tv_result);
		edResult = (EditText) findViewById(R.id.et_result);
		ivSelected = (ImageView) findViewById(R.id.iv_selected);
		btnCamera = (Button) findViewById(R.id.btn_camera);
		btnSelect = (Button) findViewById(R.id.btn_select);
		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);

		btnCamera.setOnClickListener(new cameraButtonListener());
		btnSelect.setOnClickListener(new selectButtonListener());

		
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_en:
					LANGUAGE = "eng";
					break;
				case R.id.rb_ch:
					LANGUAGE = "chi_sim";
					break;
				}
			}

		});
	}

	class cameraButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(IMG_PATH, "temp.jpg")));
			startActivityForResult(intent, PHOTO_CAPTURE);
		}
	};

	class selectButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("scale", true);
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(IMG_PATH, "temp_cropped.jpg")));
			intent.putExtra("outputFormat",
					Bitmap.CompressFormat.JPEG.toString());
			intent.putExtra("noFaceDetection", true); // no face detection
			startActivityForResult(intent, PHOTO_RESULT);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_CANCELED)
			return;

		if (requestCode == PHOTO_CAPTURE) {
			startPhotoCrop(Uri.fromFile(new File(IMG_PATH, "temp.jpg")));
		}

		if (requestCode == PHOTO_RESULT) {
			bitmapSelected = decodeUriAsBitmap(Uri.fromFile(new File(IMG_PATH,
					"temp_cropped.jpg")));
			ivSelected.setImageBitmap(bitmapSelected);
			textResult = doOcr(bitmapSelected, LANGUAGE);
			edResult.setText(textResult); 
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	

 	public String doOcr(Bitmap bitmap, String language) {
 		
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
		baseApi.init(getSDPath(), language);
		baseApi.setImage(bitmap);

		String text = baseApi.getUTF8Text();

		baseApi.clear();
		baseApi.end();

		return text;
	}

	public static String getSDPath() {
		String sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); 
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory()+ "/tesseract/";
		}
		return sdDir;
	}

	public void startPhotoCrop(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(IMG_PATH, "temp_cropped.jpg")));
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, PHOTO_RESULT);
	}

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
}