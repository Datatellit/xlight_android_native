package com.umarbhutta.xlightcompanion.settings;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.roundview.RoundLinearLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.StringUtil;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.imgloader.ImageLoaderOptions;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.views.CircleImageView;
import com.umarbhutta.xlightcompanion.views.DialogUtils;
import com.umarbhutta.xlightcompanion.views.pickerview.lib.TimePickerUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/5.
 * user信息修改页面
 */
public class UserMsgModifyActivity extends ShowPicSelectBaseActivity implements View.OnClickListener {

    private String TAG = UserMsgModifyActivity.class.getSimpleName();

    private LinearLayout llBack;
    private TextView btnSure;
    private TextView tvTitle;
    private TextView nick_name;
    private TextView user_name;
    private TextView sex;
    private CircleImageView user_icon;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private EditText editText;
    /**
     * 性别
     */
    private ArrayList<String> sexList = new ArrayList<String>();
    private String usernameResult;
    private String nickNameResult;
    private int sexResResult;
    private final int WRITE_PERMISSION_REQ_CODE = 100;

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("image/x-markdown; charset=utf-8");

    @Override
    public void selectPicResult(String picPath) {
//        ToastUtil.showToast(this, "url = " + picPath);
//        user_icon.setImageBitmap(BitmapFactory.decodeFile(picPath));
        uploadPic2(picPath);
    }

    public static final String TYPE = "application/octet-stream";
    private OkHttpClient client;

    private void uploadPic2(String picPath) {

        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        File file = new File(picPath);
        if (!file.exists()) {
            Toast.makeText(UserMsgModifyActivity.this, R.string.file_has_no, Toast.LENGTH_SHORT).show();
        } else {
            RequestBody fileBody = RequestBody.create(MediaType.parse(TYPE), file);
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("lightscrop.jpg", file.getName(), fileBody).build();
            String url = NetConfig.URL_UPLOAD_IMG + UserUtils.getUserInfo(this).getId() + "/uploadimg?access_token=" + UserUtils.getAccessToken(this);
            Log.d("XLight", url);
            Request requestPostFile = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            ToastUtil.showLoading(this);
            client.newCall(requestPostFile).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.dismissLoading();
                            ToastUtil.showToast(UserMsgModifyActivity.this, getString(R.string.avar_set_fail));
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String jsonResult = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Logger.i("图片地址=" + jsonResult);
                            try {
                                ToastUtil.dismissLoading();
                                JSONObject object = new JSONObject(jsonResult);
                                if (object.has("filePath")) {
                                    String filePath = object.getString("filePath");
                                    ImageLoader.getInstance().displayImage(NetConfig.SERVER_ADDRESS_DOMAIN + filePath, user_icon, ImageLoaderOptions.getImageLoaderOptions());
                                    LoginResult infos = UserUtils.getUserInfo(UserMsgModifyActivity.this);
                                    infos.image = NetConfig.SERVER_ADDRESS_DOMAIN + filePath;
                                    UserUtils.saveUserInfo(UserMsgModifyActivity.this, infos);
                                } else {
                                    ToastUtil.showToast(UserMsgModifyActivity.this, getString(R.string.avar_setting_fail));
                                }
                            } catch (JSONException e) {
                                ToastUtil.showToast(UserMsgModifyActivity.this, getString(R.string.avar_setting_fail));
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }

    private boolean hasPermision = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_msg_modify);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
//        getSupportActionBar().hide();
        initViews();
        hasPermision = checkPublishPermission();
        ImmersionBar.with(this).titleBar(R.id.ll_top_edit).statusBarDarkFont(true).init();
    }


    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }

            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        (String[]) permissions.toArray(new String[0]),
                        WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    private List<String> userMsgs = new ArrayList<String>();

    private void initViews() {
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSure = (TextView) findViewById(R.id.tvEditSure);
        btnSure.setVisibility(View.INVISIBLE);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.user_info);
        sexList.add(getString(R.string.women));
        sexList.add(getString(R.string.man));
        sexList.add(getString(R.string.bu_queding));


        findViewById(R.id.nick_name_layout).setOnClickListener(this);
        findViewById(R.id.avatar_layout).setOnClickListener(this);
        findViewById(R.id.sex_layout).setOnClickListener(this);

        user_name = (TextView) findViewById(R.id.user_name);
        nick_name = (TextView) findViewById(R.id.nick_name);
        sex = (TextView) findViewById(R.id.sex);
        user_icon = (CircleImageView) findViewById(R.id.user_icon);
        updateUserinfo();
        initDialog();
    }


    private void initDialog() {
        builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        View view = View.inflate(this, R.layout.layout_edittext, null);
        // dialog.setView(view);// 将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题
        editText = (EditText) view.findViewById(R.id.etName);
        ((TextView) view.findViewById(R.id.txtTitle)).setText(getString(R.string.modify_nick));
        editText.setText(nick_name.getText().toString());
        ((ImageView) view.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintKeyBoard();
                // 关闭
                dialog.dismiss();
            }
        });
        ((RoundLinearLayout) view.findViewById(R.id.rll_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用保存接口
                hintKeyBoard();
                modifyUserInfo(1, editText.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
    }

    public void hintKeyBoard() {
        editText.clearFocus();
        //拿到InputMethodManager
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    private void updateUserinfo() {
        LoginResult info = UserUtils.getUserInfo(this);
        user_name.setText("" + info.username);
        nick_name.setText("" + ((TextUtils.isEmpty(info.nickname) ? "" : info.nickname)));
        sex.setText("" + ((info.sex == 2) ? getString(R.string.bu_queding) : (0 == info.sex) ? getString(R.string.women) : getString(R.string.man)));
        if (StringUtil.isNotEmpty(info.username, false)) {
            user_name.setText(info.username);
        } else {
            user_name.setText("");
        }
        if (StringUtil.isNotEmpty(info.nickname, false)) {
            nick_name.setText(info.nickname);
        } else {
            nick_name.setText("");
        }
        if (StringUtil.isNotEmpty(info.sex, true)) {
            if (info.sex == 0) {
                sex.setText(R.string.women);
            } else if (info.sex == 1) {
                sex.setText(R.string.man);
            } else if (info.sex == 2) {
                sex.setText(R.string.bu_queding);
            }
        } else {
            sex.setText(R.string.bu_queding);
        }
        ImageLoader.getInstance().displayImage(info.getImage(), user_icon, ImageLoaderOptions.getImageLoaderOptions());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_layout://账号
                break;
            case R.id.nick_name_layout://呢称
                dialog.show();
                break;
            case R.id.avatar_layout:
                showPictureSelector();
                break;
            case R.id.sex_layout:
                showSexPicker();
                break;
        }
    }

    public int type = 0;

    private int sexPosition;

    private void showSexPicker() {
        TimePickerUtils.alertBottomWheelOption(this, sexList, 0, new TimePickerUtils.OnWheelViewClick() {
            @Override
            public void onClick(View view, int postion) {
                sexPosition = postion;
                sex.setText(sexList.get(postion));
                modifyUserInfo(2, null);
            }
        });
    }

    /**
     * @param type   0修改账号，1修改昵称，2修改性别
     * @param result
     */
    private void modifyUserInfo(int type, String result) {
        LoginResult userInfo = UserUtils.getUserInfo(this);
        usernameResult = userInfo.getUsername();
        nickNameResult = userInfo.getNickname();
        sexResResult = userInfo.getSex();//sex=0代表女，1代表男，没选的话就不传这个参数
        Logger.e(TAG, userInfo.toString());
        this.type = type;

        switch (type) {
            case 0:
                usernameResult = result;
                break;
            case 1:
                nickNameResult = result;
                break;
            case 2:
                sexResResult = sexPosition;
                break;
        }


        JSONObject object = new JSONObject();
        try {
            object.put("username", usernameResult);
            object.put("nickname", nickNameResult);
            if ("2".equals(sexResResult)) {  //不确定，性别不确定时不用传此参数
                object.put("sex", 2);
            } else {
                object.put("sex", sexResResult);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.getInstance().putRequestInfo(NetConfig.URL_MODIFY_USER_INFO + userInfo.getId() + "?access_token=" + userInfo.getAccess_token(), object.toString(), null, new HttpUtils.OnHttpRequestCallBack() {
            @Override
            public void onHttpRequestSuccess(Object result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveUserInfo();
                        ToastUtil.showToast(UserMsgModifyActivity.this, R.string.modify_success);
                    }
                });
            }

            @Override
            public void onHttpRequestFail(int code, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(UserMsgModifyActivity.this, "" + errMsg);
                    }
                });
            }
        });
    }

    private void saveUserInfo() {
        LoginResult mLoginResult = UserUtils.getUserInfo(this);
        switch (type) {
            case 0:
                mLoginResult.username = usernameResult;
                user_name.setText(usernameResult);
                break;
            case 1:
                mLoginResult.nickname = nickNameResult;
                nick_name.setText(nickNameResult);
                break;
            case 2:
                mLoginResult.sex = sexResResult;
                break;
        }
        UserUtils.saveUserInfo(this, mLoginResult);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
