package com.group70.mobileoffloading.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

public class AlertUtil {

    public static void showSnackBarShort(Context context, View view, String msg) {
        if (context != null) {
            Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public static void showSnackBarLong(Context context, View view, String msg, String buttonTitle, final View.OnClickListener onClickListener) {
        if (context != null) {
            Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
            snackbar.setAction(buttonTitle, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(view);
                }
            });
            snackbar.show();
        }
    }

    public static void showSnackBarLong(Context context, View view, String msg) {
        if (context != null) {
            Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public static void showToastShort(Context context, String msg) {
        if (context != null)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, String msg) {
        if (context != null)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showAlertDialog(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", null);
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public static void showAlertDialogWithListener(Context context, String msg, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(null);
        builder.setMessage(msg);
        builder.setPositiveButton("Got it!", okListener);
        builder.setCancelable(true);
        builder.setCancelable(false);
        builder.show();
    }

    public static void showCustomAlertDialogWithListener(Context context, String msg, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(null);
        builder.setMessage(msg);
        builder.setPositiveButton("Got it!", okListener);
        builder.setCancelable(true);
        builder.setCancelable(false);
        builder.show();
        AlertDialog dialog = builder.create();
    }

    public static void showAlertDialogDismissableWithTitle(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    public static void showAlertDialogDismissableWithOK(Context context, String msg, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(null);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", okListener);
        builder.setCancelable(false);
        builder.show();
    }

    public static void showAlertDialogDismissable(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(null);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    public static void showAlertDialog(Context context, String title, String msg, String button1, String button2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(button1, null);
        builder.setNegativeButton(button2, null);
        builder.show();
    }

    public static void showAlertDialog(Context context, String title, String msg, String button1, String button2, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(button1, okListener);
        builder.setNegativeButton(button2, cancelListener);
        builder.show();
    }

    public static void showAlertDialog(Context context, String title, String msg, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", okListener);
        builder.setNegativeButton("Cancel", cancelListener);
        builder.show();
    }


    public static void showAlertDialogWithOk(Context context, String title, String msg, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", okListener);
        builder.setNegativeButton("Cancel", cancelListener);
        builder.show();
    }

}
