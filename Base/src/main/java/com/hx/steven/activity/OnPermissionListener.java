package com.hx.steven.activity;

import java.util.List;

public interface OnPermissionListener {
    void onPermissionGranted();
    void onPermissionDenied(List<String> deniedList);
}
