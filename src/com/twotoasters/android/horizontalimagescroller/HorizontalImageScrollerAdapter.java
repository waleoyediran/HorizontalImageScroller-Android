package com.twotoasters.android.horizontalimagescroller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class HorizontalImageScrollerAdapter extends BaseAdapter {
	protected Context _context;
	protected int _imageSize;
	protected int _frameColor;
	protected int _frameOffColor;
	protected int _transparentColor;
	protected int _imageLayoutResourceId;
	protected int _loadingImageResourceId;
	protected LayoutInflater _inflater;
	protected int _currentImageIndex;
	protected boolean _highlightActive = true;
	protected boolean _showImageFrame = true;
	protected ImageCacheManager _imageCacheManager;
	protected OnClickListener _imageOnClickListener;
	protected List<ImageToLoad> _images;

	public HorizontalImageScrollerAdapter(final Context context, final List<ImageToLoad> images, final int imageSize, final int frameColorResourceId, final int frameOffColorResourceId,
			final int transparentColorResourceId, final int imageLayoutResourceId, final int loadingImageResourceId) {
		_context = context;
		_inflater = LayoutInflater.from(context);
		_images = images;
		Resources res = context.getResources();
		_imageSize = imageSize;
		_frameColor = res.getColor(frameColorResourceId);
		_frameOffColor = res.getColor(frameOffColorResourceId);
		_transparentColor = res.getColor(transparentColorResourceId);
		_imageLayoutResourceId = imageLayoutResourceId;
		_loadingImageResourceId = loadingImageResourceId;
		_imageCacheManager = ImageCacheManager.getInstance(context);
	}

	public HorizontalImageScrollerAdapter(final Context context, final List<ImageToLoad> images) {
		_context = context;
		_inflater = LayoutInflater.from(context);
		_images = images;
		Resources res = context.getResources();
		_imageSize = (int) res.getDimension(R.dimen.default_image_size);
		_frameColor = res.getColor(R.color.default_frame_color);
		_frameOffColor = res.getColor(R.color.default_frame_off_color);
		_transparentColor = res.getColor(R.color.default_transparent_color);
		_imageLayoutResourceId = R.layout.horizontal_image_scroller_item;
	}

	public int getImageSize() {
		return _imageSize;
	}

	public void setImageSize(int imageSize) {
		_imageSize = imageSize;
		notifyDataSetChanged();
	}

	public int getFrameColor() {
		return _frameColor;
	}

	public void setFrameColor(int frameColor) {
		_frameColor = frameColor;
		notifyDataSetChanged();
	}

	public int getFrameOffColor() {
		return _frameOffColor;
	}

	public void setFrameOffColor(int frameOffColor) {
		_frameOffColor = frameOffColor;
		notifyDataSetChanged();
	}

	public int getTransparentColor() {
		return _transparentColor;
	}

	public void setTransparentColor(int transparentColor) {
		_transparentColor = transparentColor;
		notifyDataSetChanged();
	}

	public int getImageLayoutResourceId() {
		return _imageLayoutResourceId;
	}

	public void setImageLayoutResourceId(int imageLayoutResourceId) {
		_imageLayoutResourceId = imageLayoutResourceId;
		notifyDataSetChanged();
	}

	public int getLoadingImageResourceId() {
		return _loadingImageResourceId;
	}

	public void setLoadingImageResourceId(int loadingImageResourceId) {
		_loadingImageResourceId = loadingImageResourceId;
		notifyDataSetChanged();
	}

	public boolean isShowImageFrame() {
		return _showImageFrame;
	}

	public void setCurrentIndex(int index) {
		_currentImageIndex = index;
		notifyDataSetChanged();
	}

	public void setHighlightActiveImage(boolean highlight) {
		_highlightActive = highlight;
	}

	public void setShowImageFrame(boolean b) {
		_showImageFrame = b;
	}

	protected int _getImageIdInLayout() {
		return R.id.image;
	}

	protected int _getImageFrameIdInLayout() {
		return R.id.image_frame;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(getCount() > 0) {
			if(view == null) {
				view = _inflater.inflate(_imageLayoutResourceId, null);
			}
			ImageToLoad imageToLoad = getItem(position);
			ImageView imageView = (ImageView)view.findViewById(_getImageIdInLayout());
			imageToLoad.setImageView(imageView);
			if (_imageOnClickListener != null) imageView.setOnClickListener(_imageOnClickListener);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)imageView.getLayoutParams();
			params.width = _imageSize;
			params.height = _imageSize;
			imageView.setLayoutParams(params);
			View frame = view.findViewById(_getImageFrameIdInLayout());
			if (imageToLoad instanceof ImageToLoadUrl) {
				imageView.setImageResource(_loadingImageResourceId);
				_imageCacheManager.pleaseCacheDrawable((ImageToLoadUrl) imageToLoad);
			} else if (imageToLoad instanceof ImageToLoadDrawableResource) {
				imageView.setImageDrawable(_context.getResources().getDrawable(((ImageToLoadDrawableResource) imageToLoad).getDrawableResourceId())); 
			} else if (imageToLoad instanceof ImageToLoadAsset) {
				InputStream is = null;
				try {
					is = _inflater.getContext().getAssets().open(((ImageToLoadAsset) imageToLoad).getPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bitmap bitmap = null;
				try {
					bitmap = BitmapFactory.decodeStream(is);
					imageView.setImageBitmap(bitmap);
				} finally {
			        try {
			            is.close();
			            is = null;
			        } catch (IOException e) {
			        }
			    }
			} 
			if(!_showImageFrame) {
				frame.setBackgroundColor(_transparentColor);
			}
		}
		return view;
	}

	@Override
	public ImageToLoad getItem(int position) {
		return _images.get(position);
	}

	@Override
	public int getCount() {
		return _images.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void unbindImageViews() {
		if (_images != null) {
			ImageCacheManager icm = ImageCacheManager.getInstance(_context);
			for (ImageToLoad image : _images) {
				if (image instanceof ImageToLoadUrl) {
					icm.unbindImage(((ImageToLoadUrl) image).getImageView()); 
				}
			}
		}
	}
}