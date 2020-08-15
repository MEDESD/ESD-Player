package com.medesd.esdplayer.Files.activity;

import java.io.File;
import java.util.ArrayList;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.medesd.esdplayer.Files.adapter.BaseFragmentAdapter;
import com.medesd.esdplayer.Files.component.PhoneMediaVideoController;
import com.medesd.esdplayer.Files.component.VideoThumbleLoader;
import com.medesd.esdplayer.R;
import com.medesd.esdplayer.Video.activities.VideoActivity;

public class VideoFragment extends Fragment implements PhoneMediaVideoController.loadAllVideoMediaInterface {

	private Context mContext;
	private ListAdapter listAdapter;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mContext = this.getActivity();
		@SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_gallery, null);
		initializeView(v);
		return v;
	}
	
	private void initializeView(View v){
		GridView mView = v.findViewById(R.id.grid_view);
		mView.setAdapter(listAdapter = new ListAdapter(mContext));

        int position = mView.getFirstVisiblePosition();

        listAdapter.notifyDataSetChanged();
        mView.setSelection(position);
        loadData();
	}
	
	private void loadData() {
		PhoneMediaVideoController.setLoadallvideomediainterface(this);
		PhoneMediaVideoController.loadAllVideoMedia();
	}

	@Override
	public void loadVideo(ArrayList<PhoneMediaVideoController.VideoDetails> arrVideoDetails) {
		arrayVideoDetails=arrVideoDetails;
		if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
	}
	
	
	private ArrayList<PhoneMediaVideoController.VideoDetails> arrayVideoDetails = null;
	private class ListAdapter extends BaseFragmentAdapter {
		private VideoThumbleLoader thumbleLoader;
		private LayoutInflater inflater;
		
		ListAdapter(Context context) {
			this.thumbleLoader=new VideoThumbleLoader();
			this.inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public boolean isEnabled(int i) {
			return true;
		}

		@Override
		public int getCount() {
			return arrayVideoDetails != null ? arrayVideoDetails.size() : 0;
		}

		@Override
		public Object getItem(int i) {
			return null;
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@SuppressLint("SetTextI18n")
		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {

			ViewHolder mViewHolder ;
			if (view == null) {
				mViewHolder=new ViewHolder();
				view = inflater.inflate(R.layout.photo_picker_album_layout,viewGroup, false);
				mViewHolder.img = view.findViewById(R.id.media_photo_image);
				mViewHolder.txtTitle = view.findViewById(R.id.album_name);
				mViewHolder.txtCount = view.findViewById(R.id.album_count);
				
				ViewGroup.LayoutParams params = view.getLayoutParams();
				params.height = 100;
				view.setLayoutParams(params);
				view.setTag(mViewHolder);
			}else { 
				mViewHolder = (ViewHolder) view.getTag();
			}
			
			PhoneMediaVideoController.VideoDetails mVideoDetails = arrayVideoDetails.get(i);
			final String videoPath=mVideoDetails.path;
			thumbleLoader.DisplayImage(""+mVideoDetails.imageId, mViewHolder.img, null);
			mViewHolder.txtTitle.setText(mVideoDetails.displayname);
			File file=new File(videoPath);
			mViewHolder.txtCount.setText(file.length()/(1024*1024) +" MB");


			view.setOnClickListener(v-> {
					try {
						Intent tostart = new Intent(getContext(), VideoActivity.class);
						tostart.putExtra("videoPath",videoPath);
						startActivity(tostart);
					} catch (Exception e) {
						e.printStackTrace();
					}
			});
			return view;
		}
		private class ViewHolder{
			ImageView img;
			TextView txtTitle;
			TextView txtCount;
		}

	}
}
	