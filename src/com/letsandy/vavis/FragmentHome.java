package com.letsandy.vavis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.letsandy.vavis.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v4.app.Fragment;

public class FragmentHome extends Fragment {
	private static final int CAMERA_PIC_REQUEST = 1111;
	private static final int GALLERY_PIC_REQUEST = 2222;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		FrameLayout home_frame = (FrameLayout) view.findViewById(R.id.home_frame);
		home_frame.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				final Dialog dialog = new Dialog(getActivity());
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.imagedialog);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				dialog.show();

				LinearLayout takePhotoLinearLayout = (LinearLayout) dialog.findViewById(R.id.takePhoto);
				takePhotoLinearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + File.separator + "temp.jpg")));
						startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
					}
				});

				LinearLayout choosePhotoLinearLayout = (LinearLayout) dialog.findViewById(R.id.choosePhoto);
				choosePhotoLinearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
						choosePictureIntent.setType("image/*");
						startActivityForResult(choosePictureIntent, GALLERY_PIC_REQUEST);

					}
				});

				return true;

			}
		});
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GALLERY_PIC_REQUEST && resultCode == Activity.RESULT_OK && null != data) {
			Uri selectedImageUri = data.getData();
			if (null != selectedImageUri) {
				String imageFilePath = getRealPathFromURI(getActivity(), selectedImageUri);
				String pathName = Environment.getExternalStorageDirectory() + File.separator + "image.jpg";
				Bitmap selectedImage = BitmapFactory.decodeFile(imageFilePath);
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(pathName);
					selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				setBitmap(pathName);
			}
		}

		if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
			String pathName = Environment.getExternalStorageDirectory() + File.separator + "image.jpg";
			String imageFilePath = Environment.getExternalStorageDirectory() + File.separator + "temp.jpg";
			File imgFile = new File(imageFilePath);
			if (imgFile.exists()) {
				Bitmap selectedImage = BitmapFactory.decodeFile(imageFilePath);
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(pathName);
					selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				setBitmap(pathName);
			}

		}

	}

	public void setBitmap(String pathName) {
		ImageView imgView = (ImageView) getActivity().findViewById(R.id.home);
		File file = new File(pathName);
		Glide.with(getActivity().getApplicationContext()).load(file).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).dontAnimate().into(imgView);
		// Picasso.with(getActivity()).load(file).centerCrop().fit().into(imgView);
	}

	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaColumns.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

}
