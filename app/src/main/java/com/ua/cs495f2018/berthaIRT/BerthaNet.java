package com.ua.cs495f2018.berthaIRT;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;
import com.ua.cs495f2018.berthaIRT.dialog.OkDialog;
import com.ua.cs495f2018.berthaIRT.dialog.WaitDialog;

import java.io.ByteArrayOutputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.ua.cs495f2018.berthaIRT.Client.aesDecrypter;
import static com.ua.cs495f2018.berthaIRT.Client.aesEncrypter;
import static com.ua.cs495f2018.berthaIRT.Client.rsaDecrypter;


public class BerthaNet {
    public static boolean ENCRYPTION_ENABLED = true;

    public static String ip = "http://54.236.113.200/";
    //public static String ip = "http://10.0.0.185:6969/";
    //Utilities for converting objects to server-friendly JSONs
    JsonParser jp;
    private Gson gson;

    //Volley RequestQueue
    private RequestQueue netQ;

    WaitDialog dialog;

    BerthaNet(Context c) {
        jp = new JsonParser();
        gson = new Gson();
        netQ = Volley.newRequestQueue(c);
    }

    private Interface.WithStringListener secureResponseWrapper(Interface.WithStringListener callback) {
        return response -> {
            //Result will be hex-encoded for URL safety and encrypted with AES for security
            try {
                //Decode hex into bytes
                byte[] encrypted = Util.fromHexString(response);
                String decrypted;
                if(!ENCRYPTION_ENABLED) decrypted = new String(encrypted);
                //Use cipher to decrypt bytes
                else decrypted = new String(aesDecrypter.doFinal(encrypted));
                System.out.println("Server response: " + decrypted);
                //Do the original callback with the decrypted string
                callback.onEvent(decrypted);
            } catch (Exception e) {
                System.out.println("Unable to decrypt server response!");
                e.printStackTrace();
            }
        };
    }

    //Basic network HTTP request.
    //netSend will call this function with strings already encrypted.
    //If the user is logged in, their JWT is attached to the Authentication header.
    //Only one string is sent as the body.  Up to calling functions to parse JSON / map values
    public void netSend(Context ctx, String path, String body, boolean ignoreEncryption, Interface.WithStringListener callback) {
        CognitoUserSession sess = Client.cogNet.getSession();
        if (ENCRYPTION_ENABLED && !ignoreEncryption && sess != null && aesDecrypter != null) { //logged in, so encrypt
            try {
                byte[] encrypted = aesEncrypter.doFinal(body.getBytes());
                body = Util.asHex(encrypted);
            } catch (Exception e) {
                System.out.println("Unable to encrypt data packet!");
                e.printStackTrace();
            }
            //wrap the response so it gets decoded
            callback = secureResponseWrapper(callback);
        }
        addRequest(ctx, sess, "app/" + path, body, callback);
    }

    private void addRequest(Context ctx, final CognitoUserSession tokens, final String path, final String body, Interface.WithStringListener callback){
        StringRequest req = new StringRequest(Request.Method.PUT, ip.concat(path), callback::onEvent, error->{
            String errorMessage;
            if(error.getCause() instanceof ConnectException)
                errorMessage = "Unable to establish a connection!";
            }
            else errorMessage = ((VolleyError) error).getLocalizedMessage();
            new OkDialog(ctx, "Network error", errorMessage, ()->{
                if(dialog != null)
                    dialog.dismiss();
            }).show();
        }) {
            @Override
            public byte[] getBody(){
                return body.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                if(tokens != null)
                    header.put("Authentication", Util.asHex(tokens.getIdToken().getJWTToken().getBytes()));
                else if (!ENCRYPTION_ENABLED && Client.userAttributes != null){
                    header.put("user", Client.userAttributes.get("cognito:username"));
                    header.put("group", Client.userAttributes.get("custom:groupID"));
                }
                return header;
            }
        };
        netQ.add(req);
    }

    //We need to recieve an AES key from the server in order to encrypt our requests.
    //When this is called, an RSA key will have been made and updated to Cognito user attributes.
    void exchangeKeys(Context ctx, Interface.WithGenericListener callback) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( (AppCompatActivity) ctx, instanceIdResult -> {
            String token = instanceIdResult.getToken();
            System.out.println(token);
            netSend(ctx, "keyexchange", token, true, r -> {
                if(!ENCRYPTION_ENABLED){
                    callback.onEvent(null);
                    return;
                }
                try {
                    //AES keys come in two parts, the key itself, and initialization vectors
                    JsonObject jay = jp.parse(r).getAsJsonObject();
                    String encodedKey = jay.get("key").getAsString();
                    String encodedIv = jay.get("iv").getAsString();

                    //Response is hex-encoded for URL safety, so decode to byte
                    byte[] decodedKey = Util.fromHexString(encodedKey);
                    byte[] decodedIv = Util.fromHexString(encodedIv);

                    //Now use RSA cipher to decrypt the AES key
                    byte[] decryptedKey = rsaDecrypter.doFinal(decodedKey);
                    byte[] decryptedIv = rsaDecrypter.doFinal(decodedIv);

                    //Use data to make a new SecretKeySpec
                    IvParameterSpec iv = new IvParameterSpec(decryptedIv);
                    SecretKeySpec spec = new SecretKeySpec(decryptedKey, "AES");

                    //Initialize AES ciphers.  Will be used for all further secure communication.//
                    Cipher encrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    Cipher decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    encrypter.init(Cipher.ENCRYPT_MODE, spec, iv);
                    decrypter.init(Cipher.DECRYPT_MODE, spec, iv);
                    callback.onEvent(new Cipher[]{encrypter, decrypter});
                } catch (Exception e) {
                    System.out.println("Unable to initialize AES ciphers!");
                    e.printStackTrace();
                }
            });
        });
    }

    public void lookupGroup(Context ctx, String groupID, Interface.WithVoidListener callback){
        String path = "group/info";
        if(Client.cogNet.getSession() == null) path+="/";
        netSend(ctx, path, groupID, true, r->{
            JsonObject jay = jp.parse(r).getAsJsonObject();
            Client.userGroupName = jay.get("groupName").getAsString();
            if(!Client.userGroupName.equals("NONE")) {
                Client.userGroupStatus = jay.get("groupStatus").getAsString();
                Client.userGroupAdmins = gson.fromJson(jay.get("admins").getAsString(), List.class);
            }
            if (callback != null)
                callback.onEvent();
        });
    }

    void pullAllReports(Context ctx, Interface.WithVoidListener callback) {
        netSend(ctx, "report/pull/all", "", false, r->{
            JsonArray reportList = jp.parse(r).getAsJsonArray();
            for(JsonElement e : reportList){
                Report rp = gson.fromJson(e.getAsString(), Report.class);
                Client.reportMap.put(rp.getReportID(), rp);
            }
            callback.onEvent();
        });
    }

    void pullReport(Context ctx, String id, Interface.WithVoidListener callback){
        netSend(ctx, "report/pull", id, false, r->{
            Report report =  gson.fromJson(r, Report.class);
            Client.reportMap.put(Integer.valueOf(id), report);
            if(Client.activeReport != null && report.getReportID().equals(Client.activeReport.getReportID()))
                Client.activeReport = report;
            callback.onEvent();
        });
    }

    public void pullAlerts(Context ctx, Interface.WithVoidListener callback){
        netSend(ctx, "alerts", "", false, rr->{
            JsonArray alertList = jp.parse(rr).getAsJsonArray();
            Client.alertList = new ArrayList<>();
            for(JsonElement e : alertList)
                Client.alertList.add(gson.fromJson(e.toString(), Message.class));
            callback.onEvent();
        });
    }

    public void syncActiveReport(Context ctx, Interface.WithVoidListener callback){
        WaitDialog d = new WaitDialog(ctx);
        d.show();
        String path = "report/update";
        if(Client.activeReport.getReportID() == null)
            path = "report/create";
        netSend(ctx, path, gson.toJson(Client.activeReport), false, r->{
            Client.activeReport = Client.net.gson.fromJson(r, Report.class);
            Client.reportMap.put(Client.activeReport.getReportID(), Client.activeReport);
            d.dismiss();
            if(callback != null)
                callback.onEvent();
        });
    }

    public void toggleRegistration(Context ctx, Interface.WithStringListener callback){
        netSend(ctx, "group/togglestatus", "", false, callback);
    }

    public void dismissAlert(Context ctx, Long messageID, Interface.WithVoidListener callback) {
        //netSend(ctx, "group/alert/dismiss", messageID.toString(), false, r->callback.onEvent());
    }

    void createGroup(Context ctx, String email, String institution, Interface.WithVoidListener callback) {
        JsonObject req = new JsonObject();
        req.addProperty("newAdmin", email);
        req.addProperty("groupName", institution);

        netSend(ctx, "/group/create", req.toString(), true, r -> {
            if (r.equals(email))
                callback.onEvent();
        });
    }

    void joinGroup(Context ctx, String groupID, Interface.WithVoidListener callback) {
        netSend(ctx, "group/join", groupID, false, (r) ->
                Client.performLogin(ctx, r, "BeRThAfirsttimestudent", x -> {
                    //Login successful and details stored - launch main activity
                    if (x.equals("SECURE"))
                        callback.onEvent();
                }));
    }

    public void uploadBitmap(Context ctx, Bitmap bitmap, Interface.WithStringListener listener){
        Bitmap b = Bitmap.createBitmap(bitmap);

        if(ctx instanceof AdminMainActivity) {
            b = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
            sentBitmap(ctx,"group/emblem",b, ()->listener.onEvent("group/emblem"));
        }
        else
            sentBitmap(ctx,"report/media",b, ()->listener.onEvent("report/media"));

    }

    private void sentBitmap(Context ctx, String path, Bitmap b, Interface.WithVoidListener listener) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String hexImg = Util.asHex(bytes.toByteArray());

        netSend(ctx, path, hexImg, true, (r) ->{
            Toast.makeText(ctx, "Image upload successful.", Toast.LENGTH_SHORT).show();
            if(listener != null)
                listener.onEvent();
        });
    }

    public void getEmblem(String groupID, ImageView into){
        Picasso.get().load(ip + "emblem/" + groupID + ".png")
                .placeholder(R.drawable.emblem_default)
                .into(into);
    }

    public void getEmblem(ImageView into){
        getEmblem(Client.userAttributes.get("custom:groupID"), into);
    }

    public void getReportImage(int index, ImageView into) {
        Picasso.get().load(ip + "media/" + Client.userAttributes.get("custom:groupID") + "/" + Client.activeReport.getReportID() + "/" + index + ".png")
                .placeholder(R.drawable.media_default)
                .into(into);
    }

    public void forgotPassword(Context ctx, String username){
        if(username.equals("")) return;
        netSend(ctx, "forgotpassword", username, true, (r)->{
            new OkDialog(ctx, "Password Reset", "A new temporary password has been sent to " + username, null).show();
        });
    }

    public void updateInstitutionName(Context ctx, String name, Interface.WithVoidListener callback) {
        netSend(ctx, "group/changename", name, false, (s)->callback.onEvent());
    }
}