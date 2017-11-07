package com.umarbhutta.xlightcompanion.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;

import com.umarbhutta.xlightcompanion.R;

/**
 * Created by luomengxin on 2017/5/5.
 */

public class DialogUtils {
    public EditText et;
    public Context mContext;

    public AlertDialog getEditTextDialog(Context context, String title, final OnClickOkBtnListener mOnClickOkBtnListener) {
        this.mContext = context;
        et = (EditText) LayoutInflater.from(context).inflate(R.layout.layout_edittext, null);
        et.setSingleLine(true);
        AlertDialog mAlertDialog = new AlertDialog.Builder(mContext).setTitle(title)
                .setView(et)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (null != mOnClickOkBtnListener) {
                            mOnClickOkBtnListener.onClickOk(input);
                        }

                    }
                })
                .setNegativeButton(mContext.getString(R.string.cancel), null).show();
        Button btn1 = mAlertDialog.getButton(mAlertDialog.BUTTON_POSITIVE);
        btn1.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        Button btn2 = mAlertDialog.getButton(mAlertDialog.BUTTON_NEGATIVE);
        btn2.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        return mAlertDialog;
    }

    /**
     * @param context
     * @param title
     * @param defaultContent        默认填充到输入框中
     * @param mOnClickOkBtnListener
     * @return
     */
    public AlertDialog getEditTextDialog(Context context, String title, String defaultContent, final OnClickOkBtnListener mOnClickOkBtnListener) {
        this.mContext = context;
        et = (EditText) LayoutInflater.from(context).inflate(R.layout.layout_edittext, null);
        et.setText("" + defaultContent);
        et.setSingleLine(true);
        AlertDialog mAlertDialog = new AlertDialog.Builder(mContext).setTitle(title)
                .setView(et)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (null != mOnClickOkBtnListener) {
                            mOnClickOkBtnListener.onClickOk(input);
                        }

                    }
                })
                .setNegativeButton(mContext.getString(R.string.cancel), null).show();
        Button btn1 = mAlertDialog.getButton(mAlertDialog.BUTTON_POSITIVE);
        btn1.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        Button btn2 = mAlertDialog.getButton(mAlertDialog.BUTTON_NEGATIVE);
        btn2.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        return mAlertDialog;
    }


    /**
     * @param context
     * @param title
     * @param mOnClickOkBtnListener
     * @return
     */
    public AlertDialog getConfirmCancelDialog(Context context, String title, final OnClickOkBtnListener mOnClickOkBtnListener) {
        this.mContext = context;
        AlertDialog mAlertDialog = new AlertDialog.Builder(mContext).setTitle(title)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (null != mOnClickOkBtnListener) {
                            if (null == et) {
                                mOnClickOkBtnListener.onClickOk(null);
                            } else {
                                String input = et.getText().toString();
                                mOnClickOkBtnListener.onClickOk(input);
                            }
                        }

                    }
                })
                .setNegativeButton(mContext.getString(R.string.cancel), null).show();
        Button btn1 = mAlertDialog.getButton(mAlertDialog.BUTTON_POSITIVE);
        btn1.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        Button btn2 = mAlertDialog.getButton(mAlertDialog.BUTTON_NEGATIVE);
        btn2.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        return mAlertDialog;
    }


    public interface OnClickOkBtnListener {
        void onClickOk(String editTextStr);
    }

}
