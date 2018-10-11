package com.example.apple.wechathook;

public interface OnResponseListner {
    void onSucess(String response);
    void onError(String error);
}