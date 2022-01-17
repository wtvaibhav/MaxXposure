package com.maxxposure.app.cropper.callback;

import android.net.Uri;

public interface SaveCallback extends Callback {
  void onSuccess(Uri uri);
}
