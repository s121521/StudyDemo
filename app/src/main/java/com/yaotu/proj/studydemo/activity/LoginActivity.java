package com.yaotu.proj.studydemo.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.LoginBean;
import com.yaotu.proj.studydemo.bean.UserBean;
import com.yaotu.proj.studydemo.intentData.ParseIntentData;
import com.yaotu.proj.studydemo.util.DBManager;
import com.yaotu.proj.studydemo.util.HttpUrlAddress;
import com.yaotu.proj.studydemo.util.SharedPreferencesManager;
import java.io.IOException;
import java.lang.reflect.Type;

import io.reactivex.functions.Consumer;
import okhttp3.FormBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    private Context context = LoginActivity.this;
    private EditText username_etxt, password_etxt;
    private Button bt_username_clear;
    private Button bt_pwd_clear;
    private Button bt_pwd_eye;
    private Button login;
    private String yhdh = "", userPwd = "", usertel = "", yhmc;
    private boolean isOpen = false;
    private ProgressDialog progressDialog;
    private UserBean bean = null;//用户登录成功过去用户信息
    private final int LOGINSUCCESS = 0X110;
    private final int LOGINERROR = 0x111;
    private DBManager dbManager;
    private Cursor cursor;
    RxPermissions rxPermissions;
    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOGINSUCCESS:
                    progressDialog.dismiss();
                    //1. 将用户保存本地
                    saveUserLocal(yhdh, userPwd, yhmc);
                    Log.i(TAG, "handleMessage: ----------请求网络数据登录并将数据保存在本地--------");
                    //2.跳转主界面
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("yhdh", yhdh);
                    intent.putExtra("yhmc", yhmc);
                    startActivity(intent);
                    finish();
                    break;
                case LOGINERROR:
                    progressDialog.dismiss();
                    showMessage("登录失败");
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initMethod();
        mDialog = defineSetIpDialog();
        rxPermissions = new RxPermissions(this);
        requestPermissions();
    }

    private void initView() {
        username_etxt = (EditText) findViewById(R.id.username);
        password_etxt = (EditText) findViewById(R.id.password);
        bt_username_clear = (Button) findViewById(R.id.bt_username_clear);
        bt_pwd_clear = (Button) findViewById(R.id.bt_pwd_clear);
        bt_pwd_eye = (Button) findViewById(R.id.bt_pwd_eye);
        login = (Button) findViewById(R.id.login);
        dbManager = new DBManager(context);
    }

    private void initMethod() {
        // 监听文本框内容变化
        username_etxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 获得文本框中的用户
                yhdh = username_etxt.getText().toString().trim();
                if ("".equals(yhdh)) {
                    // 用户名为空,设置按钮不可见
                    bt_username_clear.setVisibility(View.INVISIBLE);
                } else {
                    // 用户名不为空，设置按钮可见
                    bt_username_clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.i(TAG, "afterTextChanged: --------------yhdh--------->"+yhdh);
                UserBean userObj = queryLocalUserByYhdh(yhdh);
                if (userObj != null) {
                    Log.i(TAG, "afterTextChanged: --------------userObj--------->" + userObj);
                    password_etxt.setText(userObj.getPwd().toString().trim());
                } else {
                    password_etxt.setText("");
                }
            }
        });

        // 监听文本框内容变化
        password_etxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 获得文本框中的密码
                userPwd = password_etxt.getText().toString().trim();
                if ("".equals(userPwd)) {
                    // 密码为空,设置按钮不可见
                    bt_pwd_clear.setVisibility(View.INVISIBLE);
                } else {
                    // 密码不为空，设置按钮可见
                    bt_pwd_clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        bt_username_clear.setOnClickListener(this);
        bt_pwd_clear.setOnClickListener(this);
        bt_pwd_eye.setOnClickListener(this);
        login.setOnClickListener(this);
        /*
        * 获取服务器IP地址
        * */
        if ("".equals(HttpUrlAddress.getHttpUrl())) {
            String httpUrl = getResources().getString(R.string.http_url);
            SharedPreferencesManager.getHttpPreferencesUtils().put(SharedPreferencesManager.sHttpurl, httpUrl);
        }
    }

    /**
     * 密码可见与不可见的切换
     *
     * @param flag
     */
    private void changePwdOpenOrClose(boolean flag) {
        // 第一次过来是false，密码不可见
        if (flag) {
            // 密码可见
            bt_pwd_eye.setBackgroundResource(R.drawable.password_open);
            // 设置EditText的密码可见
            password_etxt.setTransformationMethod(HideReturnsTransformationMethod
                    .getInstance());
        } else {
            // 密码不接见
            bt_pwd_eye.setBackgroundResource(R.drawable.password_close);
            // 设置EditText的密码隐藏
            password_etxt.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_username_clear:
                // 清除登录名
                boolean r = removeLocalUser(yhdh);
                Log.i(TAG, "onClick: ------------删除一条本地用户记录---------------" + r);
                username_etxt.setText("");
                break;
            case R.id.bt_pwd_clear:
                // 清除密码
                password_etxt.setText("");
                break;
            case R.id.bt_pwd_eye:
                // 密码可见与不可见的切换
                if (isOpen) {
                    isOpen = false;
                } else {
                    isOpen = true;
                }
                // 默认isOpen是false,密码不可见
                changePwdOpenOrClose(isOpen);

                break;
            case R.id.login:
                // TODO 登录按钮
                if ("".equals(yhdh) || "".equals(userPwd)) {
                    showMessage("用户名或密码不能为空!");
                    return;
                }
                //---------------------------------------------
                /*Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("yhdh", yhdh);
                intent.putExtra("yhmc", yhmc);
                startActivity(intent);
                finish();*/
                //------------------------------------------------

                //-----------------修改日期：2017-11-12-------------------------
                //1.先查本地数据库，判断数据库是否存在该用户(查询历史登录用户)
                UserBean obj = queryLocalUser(yhdh, userPwd);
                //2.存在：直接登录；不存在：请求服务器登录,若登录成功则跳转界面并保存用户信息
                if (obj == null) {
                    loginService(yhdh, userPwd);
                    Log.i(TAG, "onClick: ----------http request");
                } else {
                    Log.i(TAG, "onClick: --------------请求本地数据登录--------------");
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("yhdh", yhdh);
                    intent.putExtra("yhmc", obj.getYhmc());
                    startActivity(intent);
                    finish();
                }
                break;
            default:
                break;
        }
    }


    private void loginService(String name, String pwd) {
        //String pwd_jm = Encrypt.encryptPassword(pwd);//密码加密
        //Log.i(TAG, "loginService: ----------加密密码------->" + pwd_jm);
        //1.组织请求参数
        final FormBody.Builder builder = new FormBody.Builder();
        builder.add("username", name);
        builder.add("userpassword", pwd);
        //2.显示进度条,准备请求服务器
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        //3.开启一个新线程,请求服务器
        final String ipUrl = HttpUrlAddress.getHttpUrl();//getResources().getString(R.string.http_url);
        Thread myThread = null;
        if (myThread == null) {
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Message message = Message.obtain();
                        Response response = ParseIntentData.getDataPostByString(ipUrl + "/bhqService/Account/Login_Phone", builder);
                        Log.i(TAG, "run: -----------------start------"+response);
                        if (response != null && response.code() == 200) {
                            String responseValue = response.body().string();
                            Gson gson = new Gson();
                            Type type = new TypeToken<LoginBean>() {
                            }.getType();
                            LoginBean obj = gson.fromJson(responseValue, type);
                            if (obj.getRETURNVALUE().equals("SUCCEED")) {
                                message.what = LOGINSUCCESS;
                                yhmc = obj.getUSERNAME();
                                myHandler.sendMessage(message);
                            } else if (obj.getRETURNVALUE().equals("ERROR") && obj.getMESSAGE().contains("已登陆")) {
                                message.what = LOGINSUCCESS;
                                yhmc = obj.getUSERNAME();
                                myHandler.sendMessage(message);
                            } else {
                                message.what = LOGINERROR;
                                myHandler.sendMessage(message);
                            }
                        } else {
                            message.what = LOGINERROR;
                            myHandler.sendMessage(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            myThread.start();
        } else if (!myThread.isAlive()) {
            Log.i(TAG, "loginService: ---------"+myThread.isAlive());
            myThread.start();
        }
    }

    private boolean saveUserLocal(String yhdh, String pwd, String yhmc) {
        boolean result;
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
        result = dbManager.updateBySql("insert into users(yhdh,pwd,yhmc) values(?,?,?)", new String[]{yhdh, pwd, yhmc});
        return result;
    }

    private UserBean queryLocalUser(String yhdh, String pwd) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
        cursor = dbManager.queryEntity("select yhdh,pwd,yhmc from users where yhdh = ? and pwd = ? ", new String[]{yhdh, pwd});
        UserBean userObj = null;
        if (cursor.moveToNext()) {
            userObj = new UserBean();
            userObj.setYhdh(cursor.getString(cursor.getColumnIndex("yhdh")));
            userObj.setYhmc(cursor.getString(cursor.getColumnIndex("yhmc")));
            userObj.setPwd(cursor.getString(cursor.getColumnIndex("pwd")));
        }
        if (cursor != null) {
            cursor.close();
        }
        return userObj;
    }

    private UserBean queryLocalUserByYhdh(String yhdh) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
        cursor = dbManager.queryEntity("select yhdh,pwd,yhmc from users where yhdh = ? ", new String[]{yhdh});
        UserBean userObj = null;
        if (cursor.moveToNext()) {
            userObj = new UserBean();
            userObj.setYhdh(cursor.getString(cursor.getColumnIndex("yhdh")));
            userObj.setYhmc(cursor.getString(cursor.getColumnIndex("yhmc")));
            userObj.setPwd(cursor.getString(cursor.getColumnIndex("pwd")));
        }
        if (cursor != null) {
            cursor.close();
        }
        return userObj;
    }

    private boolean removeLocalUser(String yhdh) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
        boolean result;
        result = dbManager.deleteTableData("users", "yhdh = ?", new String[]{yhdh});
        return result;
    }

    /*
    * 设置服务器IP地址
    * */
    private int clicks = 0;
    private long firstClickTime = 0;
    private AlertDialog mDialog;
    public void showSetIpDialog(View view) {
        clicks++;
        if (clicks == 1) {
            firstClickTime = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - firstClickTime >= 2 * 1000) {
            clicks = 0;
        }
        if (clicks == 5) {//显示对话框，修改ip地址
            showDialog();
        }
        Log.i(TAG, "showSetIpDialog: ------" + clicks + "------" + firstClickTime + "----------" + (System.currentTimeMillis() - firstClickTime));
    }

    private AlertDialog defineSetIpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view =  LayoutInflater.from(this).inflate(R.layout.set_ip_dialog, null);
        final EditText editText = (EditText) view.findViewById(R.id.ip_address_editxt);
        editText.setText(HttpUrlAddress.getHttpUrl());//显示当前服务器IP地址
        builder.setView(view);
        builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferencesManager.getHttpPreferencesUtils().put(SharedPreferencesManager.sHttpurl,editText.getText().toString().trim());
                showMessage("服务器地址修改成功");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
    private void showDialog(){
        if (null == mDialog) {
            mDialog = defineSetIpDialog();
        }
        mDialog.setCanceledOnTouchOutside(false);//点击外部是否消失
        mDialog.show();
    }
    private void closeDialog(){
        if (null != mDialog) {
            mDialog.dismiss();
        }
    }

    //=============================================================================
    private void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbManager != null) {
            dbManager.closeDB();
        }
        closeDialog();
    }

    private void requestPermissions() {
        rxPermissions.requestEach(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框

                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                        }
                    }
                });


    }
}
