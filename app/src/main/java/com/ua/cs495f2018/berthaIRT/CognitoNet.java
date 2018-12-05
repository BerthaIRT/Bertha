package com.ua.cs495f2018.berthaIRT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.UpdateAttributesHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoServiceConstants;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CognitoNet {//Performs AWS Cognito login.

    //Cognito user pool.  Handles request security by itself
    private CognitoUserPool pool;

    //Stores JWKs and other security information
    private CognitoUserSession session = null;

    CognitoNet(Context ctx) {
        //Initialize AWS
        AWSMobileClient.getInstance().initialize(ctx).execute();

        //Configuration should be in /res/raw
        AWSConfiguration awsConfiguration = new AWSConfiguration(ctx);
        if (IdentityManager.getDefaultIdentityManager() == null) {
            final IdentityManager identityManager = new IdentityManager(ctx, awsConfiguration);
            identityManager.signOut();
            IdentityManager.setDefaultIdentityManager(identityManager);
        }
        pool = new CognitoUserPool(ctx, awsConfiguration);

        //If for some reason user obtains keys from last session, sign out / get rid of them.
        //If you don't sign out and try secure communication with the server the Client IDs wont match up and encryption fails
        signOut();
    }

    public void signOut(){
        if (pool.getCurrentUser() != null) pool.getCurrentUser().signOut();
        session = null;
    }

    CognitoUserSession getSession(){
        return session;
    }

    //Occurs on both admin and student sign-in.
    void performCognitoLogin(Context ctx, String username, String password, Interface.WithStringListener callback) {
        final boolean isAdmin = (ctx instanceof AdminLoginActivity);
        //Flow for Cognito sign-in
        AuthenticationHandler handler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                session = userSession;
                callback.onEvent("AUTHENTICATED");
            }

            //Cognito always looks here to grab username and password
            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                authenticationContinuation.setAuthenticationDetails(new AuthenticationDetails(username, password, null));
                authenticationContinuation.continueTask();
            }

            //Multi-Factor Authentication which we aren't using
            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                continuation.continueTask();
            }

            //Cognito goes here if user requires a new password to be set.
            //If user is an administrator, prompt to update details.
            //Otherwise generate a random password for the student and store it in userfile
            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                //if student is logging in for the first time
                if (!isAdmin) {
                    String rPassword = Util.generateRandomPassword();
                    JsonObject jay = new JsonObject();
                    jay.addProperty("username", username);
                    jay.addProperty("password", rPassword);

                    //Stores login details to userfile.  If these details are modified they will NOT be able to login.
                    Util.writeToUserfile(ctx, jay);

                    //Update to newly generated password
                    continuation.setChallengeResponse("NEW_PASSWORD", rPassword);
                    continuation.setChallengeResponse("USERNAME", username);
                    continuation.continueTask();
                }
                else{
                    //Take the new admin to dashboard upon login
                    Client.startOnDashboard = true;
                    @SuppressLint("InflateParams")
                    View v = ((AppCompatActivity) ctx).getLayoutInflater().inflate(R.layout.dialog_admin_completesignup, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setView(v);
                    builder.show();

                    //After Update Details dialog is closed, update attributes and continue Cognito login
                    v.findViewById(R.id.completesignup_button_confirm).setOnClickListener(x -> {
                        continuation.setChallengeResponse("NEW_PASSWORD", ((EditText) v.findViewById(R.id.completesignup_input_password)).getText().toString());
                        continuation.setChallengeResponse("USERNAME", username);
                        continuation.setChallengeResponse(CognitoServiceConstants.CHLG_PARAM_USER_ATTRIBUTE_PREFIX + "name", ((EditText) v.findViewById(R.id.completesignup_input_name)).getText().toString());
                        continuation.continueTask();
                    });
                }
            }

            @Override
            public void onFailure(Exception exception) {
                System.out.println(exception.getMessage());
                callback.onEvent("INVALID_CREDENTIALS");
            }
        };
        //For some reason I have to put this here too
        signOut();

        //Log in the user
        pool.getUser(username).getSessionInBackground(handler);
    }

    //The server will use the username on the verified JWT that was given upon Cognito sign-in.
    //Using JWT claims, server will look up the RSA key in the user's attributes
    void getCognitoAttributes(Interface.WithGenericListener callback) {
        pool.getCurrentUser().getDetailsInBackground(new GetDetailsHandler() {
            @Override
            public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                Map<String, String> attribs = cognitoUserDetails.getAttributes().getAttributes();
                attribs.put("username", pool.getCurrentUser().getUserId());
                callback.onEvent(attribs);
            }

            @Override
            public void onFailure(Exception exception){
                System.out.println(exception.getMessage());
            }
        });
        //Prepare the client's blank ciphers
//        Client.rsaDecrypter = Cipher.getInstance("RSA/ECB/PKCS1Padding");


        //Create an RSA keypair and initialize rsaDecrypter with it
//        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
//        keygen.initialize(2048);
//        return keygen.generateKeyPair();

        //Now the user is logged in and RSA public key is updated in user attributes
        //So now the server will look up the RSA key and encrypt a new AES key.
    }

    public void updateCognitoAttribute(String attribute, String value, Interface.WithVoidListener callback) {
        CognitoUserAttributes attribs = new CognitoUserAttributes();
        attribs.addAttribute(attribute, value);
        pool.getCurrentUser().updateAttributesInBackground(attribs, new UpdateAttributesHandler() {
            @Override
            public void onSuccess(List<CognitoUserCodeDeliveryDetails> attributesVerificationList) {
                callback.onEvent();
            }

            @Override
            public void onFailure(Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
