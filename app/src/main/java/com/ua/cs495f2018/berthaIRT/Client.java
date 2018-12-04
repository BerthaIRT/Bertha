package com.ua.cs495f2018.berthaIRT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.google.gson.JsonObject;
import com.ua.cs495f2018.berthaIRT.dialog.WaitDialog;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

public class Client extends AppCompatActivity {

    //Class for secure and insecure network functions, AWS Cognito functionality
    public static BerthaNet net;
    public static CognitoNet cogNet;

    //Encrypter/Decrypters for secure HTTP
    //Valid for this session only and expires on application exit
    public static Cipher rsaDecrypter;
    public static Cipher aesEncrypter;
    public static Cipher aesDecrypter;

    public static String userGroupName;
    public static String userGroupStatus;
    public static List<String> userGroupAdmins;
    public static Map<String,String> userAttributes;
    public static boolean isAdmin;

    //List containing reports pulled from server.
    //Should NOT be updated outside of a network callback function or else inconsistencies between client and server occur
    public static Map<Integer, Report> reportMap;

    //List containing alerts pulled from server.
    public static List<Message> alertList;

    //The report object currently being viewed.
    //This CAN be updated - but be sure to call /report/update with it
    public static Report activeReport;

    //For new admins to start on the dashboard screen instead of an empty RV
    static boolean startOnDashboard = false; //for new admins, maybe could be a pref

    public static float displayWidth;
    public static float displayHeight;
    public static int displayWidthDPI;
    public static int dpiDensity;

    @SuppressLint("UseSparseArrays")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landingpage);
        reportMap = new HashMap<>();
        net = new BerthaNet(this);
        cogNet = new CognitoNet(this);

        displayWidth = getResources().getDisplayMetrics().widthPixels;
        displayHeight = getResources().getDisplayMetrics().heightPixels;
        dpiDensity = getResources().getDisplayMetrics().densityDpi;
        displayWidthDPI = Math.round(displayWidth / ((float) dpiDensity / DisplayMetrics.DENSITY_DEFAULT));
        System.out.println(displayWidthDPI);

        JsonObject studentLogin = Util.readFromUserfile(Client.this);
        if(studentLogin != null){
            performLogin(this , studentLogin.get("username").getAsString(), studentLogin.get("password").getAsString(), x->{
                if(x.equals("SECURE")){
                 startActivity(new Intent(this, StudentMainActivity.class));
                finish();
                }
            });
        }
        else{
            startActivity(new Intent(this, NewUserActivity.class));
            finish();
        }
   }

   public static void performLogin(Context ctx, String username, String password, Interface.WithStringListener loginResult){
        final boolean admintest = (ctx instanceof AdminLoginActivity);

       WaitDialog dialog = new WaitDialog(ctx);
       dialog.show();
       dialog.setMessage("Signing in...");

        //log in with cognito
        cogNet.performCognitoLogin(ctx, username, password, (r)->{
            if(r.equals("INVALID_CREDENTIALS")){
                dialog.dismiss();
                loginResult.onEvent(r);
            }
            else{
                isAdmin = admintest;
                dialog.setMessage("Establishing secure connection...");
                //now make and update rsa key
                try {
                    //Create an RSA keypair and initialize rsaDecrypter with it
                    KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
                    keygen.initialize(2048);
                    KeyPair keys = keygen.generateKeyPair();

                    rsaDecrypter = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    rsaDecrypter.init(Cipher.DECRYPT_MODE, keys.getPrivate());

                    cogNet.getCognitoAttributes((a)->{
                        userAttributes = (Map<String, String>) a;
                        cogNet.updateCognitoAttribute("custom:rsaPublicKey", Util.asHex(keys.getPublic().getEncoded()), ()->{
                            //now RSA is all set so we can send our firebase token and grab an AES key
                            net.exchangeKeys(ctx, (c)->{
                                if(c!=null) {
                                    aesEncrypter = ((Cipher[]) c)[0];
                                    aesDecrypter = ((Cipher[]) c)[1];
                                }

                                dialog.setMessage("Pulling data...");
                                net.lookupGroup(ctx, userAttributes.get("custom:groupID"), ()->
                                    net.pullAllReports(ctx, ()->{
                                        if(isAdmin) net.pullAlerts(ctx, ()->{
                                            dialog.dismiss();
                                            loginResult.onEvent("SECURE");
                                        });
                                        else{
                                            dialog.dismiss();
                                            loginResult.onEvent("SECURE");
                                        }
                                    })
                                );
                            });
                        });
                    });

                }catch (Exception e){e.printStackTrace();}
            }
        });
   }
}