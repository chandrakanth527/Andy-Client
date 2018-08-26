package com.letsandy.vavis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

@SuppressLint("InflateParams")
public class RoomListAdapter extends BaseAdapter {
	private FragmentRoom fragment;
	private String[] listValues;
	private String[] statusData;
	ViewHolder viewHolder;
	View listView;
	String List_Item_Id;
	int cur_position;
	View cur_view;

	private static final int CAMERA_PIC_REQUEST = 11111;
	private static final int GALLERY_PIC_REQUEST = 22222;

	public RoomListAdapter(FragmentRoom fragmentRoom, String[] listValues, String[] statusData) {
		this.fragment = fragmentRoom;
		this.listValues = listValues;
		this.statusData = statusData;

	}

	@Override
	public int getCount() {
		return listValues.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("ViewTag")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.roomlistsingle, parent, false);
			ImageView cur_image = (ImageView) convertView.findViewById(R.id.room);
			cur_image.setTag(R.id.room, position);
			viewHolder = new ViewHolder();
			viewHolder.Image = (ImageView) convertView.findViewById(R.id.room);
			viewHolder.ImageSwitch = (ImageView) convertView.findViewById(R.id.roomSwitch);
			viewHolder.ImageFan = (ImageView) convertView.findViewById(R.id.roomFan);
			viewHolder.ImageCurtain = (ImageView) convertView.findViewById(R.id.roomCurtain);
			viewHolder.Text = (TextView) convertView.findViewById(R.id.roomName);
		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.id.room);
		}
		convertView.findViewById(R.id.roomSwitch).setTag(R.id.roomSwitch, viewHolder);
		convertView.findViewById(R.id.roomFan).setTag(R.id.roomFan, viewHolder);
		convertView.findViewById(R.id.roomCurtain).setTag(R.id.roomCurtain, viewHolder);
		viewHolder.Position = position;
		convertView.setTag(R.id.room, viewHolder);
		viewHolder.Text.setText(listValues[position]);

		if (statusData[position].substring(0, 1).equals("1")) {
			viewHolder.ImageSwitch.setVisibility(View.VISIBLE);
		} else {
			viewHolder.ImageSwitch.setVisibility(View.GONE);
		}

		if (statusData[position].substring(1, 2).equals("1")) {
			viewHolder.ImageFan.setVisibility(View.VISIBLE);
		} else {
			viewHolder.ImageFan.setVisibility(View.GONE);
		}

		if (statusData[position].substring(2, 3).equals("1")) {
			viewHolder.ImageCurtain.setVisibility(View.VISIBLE);
		} else {
			viewHolder.ImageCurtain.setVisibility(View.GONE);
		}
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/vavis");
		myDir.mkdirs();
		String pathName = "Room" + position + ".jpg";
		File file = new File(myDir, pathName);
		SharedPreferences prefs = fragment.getActivity().getSharedPreferences("RoomUnique", 0);
		String RoomUnique = prefs.getString("Room" + position, "123");

		DisplayMetrics metrics = new DisplayMetrics();
		fragment.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int height = metrics.heightPixels;
		int width = metrics.widthPixels;
		if (file.exists()) {
			Glide.with(fragment.getActivity().getApplicationContext()).load(file).diskCacheStrategy(DiskCacheStrategy.NONE).override(width, height).dontAnimate().dontTransform().signature(new StringSignature(RoomUnique)).into(viewHolder.Image);
		} else {
			Glide.with(fragment.getActivity().getApplicationContext()).load(R.drawable.home).diskCacheStrategy(DiskCacheStrategy.NONE).override(width, height).dontAnimate().dontTransform().signature(new StringSignature(RoomUnique)).into(viewHolder.Image);
		}
		viewHolder.ImageSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		viewHolder.ImageFan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		viewHolder.ImageCurtain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cur_view = v;
				ViewHolder vh = (ViewHolder) v.getTag(R.id.room);
				Intent intent = new Intent(fragment.getActivity().getApplicationContext(), RoomFullActivity.class);
				intent.putExtra("room_id", Integer.toString(vh.Position));
				fragment.getActivity().startActivity(intent);
			}
		});

		convertView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				cur_view = v;
				ViewHolder vh = (ViewHolder) v.getTag(R.id.room);
				cur_position = vh.Position;
				List_Item_Id = Integer.toString(vh.Position);
				final Dialog dialog = new Dialog(fragment.getActivity());
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
						String root = Environment.getExternalStorageDirectory().toString();
						File myDir = new File(root + "/vavis");
						myDir.mkdirs();
						takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(myDir, File.separator + "Room" + List_Item_Id + ".jpg")));
						fragment.startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
					}
				});

				LinearLayout choosePhotoLinearLayout = (LinearLayout) dialog.findViewById(R.id.choosePhoto);
				choosePhotoLinearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
						choosePictureIntent.setType("image/*");
						fragment.startActivityForResult(choosePictureIntent, GALLERY_PIC_REQUEST);

					}
				});
				return true;
			}
		});
		return convertView;
	}

	public void updateStatus(String[] statusData) {
		this.statusData = statusData;
		notifyDataSetChanged();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GALLERY_PIC_REQUEST && resultCode == Activity.RESULT_OK && null != data) {
			Uri selectedImageUri = data.getData();
			if (null != selectedImageUri) {
				String pathName = Environment.getExternalStorageDirectory() + File.separator + "vavis" + File.separator + "Room" + List_Item_Id + ".jpg";
				Bitmap selectedImage = null;
				try {
					selectedImage = BitmapFactory.decodeStream(fragment.getActivity().getContentResolver().openInputStream(selectedImageUri));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}

				if (selectedImage == null) {
				}
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(pathName);
					selectedImage.compress(Bitmap.CompressFormat.JPEG, 70, fos);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				dataChange();
			}
		}

		if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
			String pathName = Environment.getExternalStorageDirectory() + File.separator + "vavis" + File.separator + "Room" + List_Item_Id + ".jpg";
			File imgFile = new File(pathName);
			if (imgFile.exists()) {
				dataChange();
			}
		}
	}

	public void dataChange() {
		Long myCurrentTimeMillis = System.currentTimeMillis();
		SharedPreferences.Editor editor = fragment.getActivity().getSharedPreferences("RoomUnique", 0).edit();
		editor.putString("Room" + cur_position, myCurrentTimeMillis.toString());
		editor.commit();
		this.notifyDataSetChanged();

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
