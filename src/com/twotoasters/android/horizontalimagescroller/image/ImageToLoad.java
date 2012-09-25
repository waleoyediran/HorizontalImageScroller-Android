package com.twotoasters.android.horizontalimagescroller.image;

import android.widget.ImageView;

public abstract class ImageToLoad {
	protected ImageView imageView;

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}
	
}