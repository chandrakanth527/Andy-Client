package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
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
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
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
public class SceneGridAdapter extends BaseAdapter {
	private FragmentScenes fragment;
	private String[] listValues;
	ViewHolder viewHolder;
	View listView;
	String List_Item_Id;
	int cur_position;
	View cur_view;
	String tempFilename;
	private static final int CAMERA_PIC_REQUEST = 3333;
	private static final int GALLERY_PIC_REQUEST = 4444;

	public SceneGridAdapter(FragmentScenes fragmentScenes, String[] listValues) {
		this.fragment = fragmentScenes;
		this.listValues = listValues;
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

	@SuppressLint({ "NewApi", "ViewTag" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.scenegridsingle, parent, false);
			ImageView cur_image = (ImageView) convertView.findViewById(R.id.grid_image);
			cur_image.setTag(R.id.grid_image, position);

			viewHolder = new ViewHolder();
			viewHolder.Image = (ImageView) convertView.findViewById(R.id.grid_image);
			viewHolder.Picture = (LinearLayout) convertView.findViewById(R.id.picture);
			viewHolder.Text = (TextView) convertView.findViewById(R.id.grid_text);
		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.id.grid_image);
		}

		viewHolder.Position = position;
		convertView.setTag(R.id.grid_image, viewHolder);
		viewHolder.Text.setText(listValues[position]);

		String sceneJSON = "sceneJSON.cfg";
		String filename = "";
		try {
			FileInputStream fis = fragment.getActivity().openFileInput(sceneJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawMasterJSON;
			rawMasterJSON = bufferedReader.readLine();
			JSONObject objJSON = new JSONObject(rawMasterJSON);
			JSONArray arrJSON = objJSON.getJSONArray("Scene");
			filename = arrJSON.getJSONObject(position).getString("SceneIcon");

		} catch (Exception e) {
			e.printStackTrace();
		}

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/vavis");
		myDir.mkdirs();
		String pathName = filename;
		File file = new File(myDir, pathName);

		SharedPreferences prefs = fragment.getActivity().getSharedPreferences("SceneUnique", 0);
		String SceneUnique = prefs.getString("Scene" + position, "123");

		if (file.exists()) {
			Glide.with(fragment.getActivity().getApplicationContext()).load(file).diskCacheStrategy(DiskCacheStrategy.NONE).signature(new StringSignature(SceneUnique)).into(viewHolder.Image);
		} else {
			Glide.with(fragment.getActivity().getApplicationContext()).load(R.drawable.scene).diskCacheStrategy(DiskCacheStrategy.NONE).signature(new StringSignature(SceneUnique)).into(viewHolder.Image);
		}
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cur_view = v;
				ViewHolder vh = (ViewHolder) v.getTag(R.id.grid_image);
				cur_position = vh.Position;

				ObjectAnimator colorFade = ObjectAnimator.ofObject(vh.Picture, "backgroundColor", new ArgbEvaluator(), 0xff00a6d6, 0x00000000);
				colorFade.setDuration(700);
				colorFade.start();

				String sceneJSON = "sceneJSON.cfg";
				int scene_id = cur_position;
				try {
					FileInputStream fis = fragment.getActivity().openFileInput(sceneJSON);
					InputStreamReader isr = new InputStreamReader(fis);
					BufferedReader bufferedReader = new BufferedReader(isr);
					String rawMasterJSON;
					rawMasterJSON = bufferedReader.readLine();

					JSONObject objJSON = new JSONObject(rawMasterJSON);
					JSONArray arrJSON = objJSON.getJSONArray("Scene");
					JSONArray arrJSONCommands = arrJSON.getJSONObject(scene_id).getJSONArray("Commands");
					JSONObject newJSON = new JSONObject();
					newJSON.put("Scene", arrJSONCommands);
					String scene = newJSON.toString();
					messagePublish("request/sceneControl", scene);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		convertView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				cur_view = v;
				ViewHolder vh = (ViewHolder) v.getTag(R.id.grid_image);
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
						Long myCurrentTimeMillis = System.currentTimeMillis();
						String unique = myCurrentTimeMillis.toString();
						tempFilename = "Scene" + unique + ".jpg";
						takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + File.separator + "vavis" + File.separator + tempFilename)));
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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GALLERY_PIC_REQUEST && resultCode == Activity.RESULT_OK && null != data) {
			Uri selectedImageUri = data.getData();
			if (null != selectedImageUri) {
				Long myCurrentTimeMillis = System.currentTimeMillis();
				String unique = myCurrentTimeMillis.toString();
				// String imageFilePath =
				// getRealPathFromURI(fragment.getActivity(), selectedImageUri);
				String pathName = Environment.getExternalStorageDirectory() + File.separator + "vavis" + File.separator + "Scene" + unique + ".jpg";

				Bitmap selectedImage = null;
				try {
					selectedImage = BitmapFactory.decodeStream(fragment.getActivity().getContentResolver().openInputStream(selectedImageUri));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// Bitmap selectedImage =
				// BitmapFactory.decodeFile(imageFilePath);
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
				dataChange("Scene" + unique + ".jpg", Integer.parseInt(List_Item_Id));
			}
		}

		if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
			// String pathName = Environment.getExternalStorageDirectory() +
			// File.separator + "Scene" + List_Item_Id + ".jpg";
			String pathName = Environment.getExternalStorageDirectory() + File.separator + "vavis" + File.separator + tempFilename;
			File imgFile = new File(pathName);
			if (imgFile.exists()) {
				dataChange(tempFilename, Integer.parseInt(List_Item_Id));
			}
		}
	}

	public void dataChange(String filename, int ListPosition) {

		String sceneJSON = "sceneJSON.cfg";
		try {
			FileInputStream fis = fragment.getActivity().openFileInput(sceneJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawMasterJSON;
			rawMasterJSON = bufferedReader.readLine();
			JSONObject objJSON = new JSONObject(rawMasterJSON);
			JSONArray arrJSON = objJSON.getJSONArray("Scene");
			arrJSON.getJSONObject(ListPosition).remove("SceneIcon");
			arrJSON.getJSONObject(ListPosition).put("SceneIcon", filename);
			rawMasterJSON = objJSON.toString();
			FileOutputStream outputStream = fragment.getActivity().openFileOutput(sceneJSON, Context.MODE_PRIVATE);
			outputStream.write(rawMasterJSON.getBytes());
			outputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Long myCurrentTimeMillis = System.currentTimeMillis();
		SharedPreferences.Editor editor = fragment.getActivity().getSharedPreferences("SceneUnique", 0).edit();
		editor.putString("Scene" + cur_position, myCurrentTimeMillis.toString());
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

	public void messagePublish(String TOPIC, String MESSAGE) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("com.letsandy.publishMQTT");
		broadcastIntent.putExtra("TOPIC", TOPIC);
		broadcastIntent.putExtra("MESSAGE", MESSAGE);
		fragment.getActivity().sendBroadcast(broadcastIntent);
	}
}
