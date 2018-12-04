package com.ua.cs495f2018.berthaIRT;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Util {
    static void writeToUserfile(Context ctx, JsonObject j) {
        try {
            new File(ctx.getFilesDir(), "user.dat");
            FileOutputStream fos = ctx.openFileOutput("user.dat", Context.MODE_PRIVATE);
            fos.write(j.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonObject readFromUserfile(Context ctx) {
        try {
            new File(ctx.getFilesDir(), "user.dat");
            InputStreamReader isr = new InputStreamReader(ctx.openFileInput("user.dat"));
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null)
                sb.append(s).append("\n");
            br.close();
            return Client.net.jp.parse(sb.toString()).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Used by BerthaNet to serialize base-64 encoded keys
    static String asHex(byte buf[]) {
        StringBuilder strbuf = new StringBuilder(buf.length * 2);
        for (byte aBuf : buf) {
            if (((int) aBuf & 0xff) < 0x10) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) aBuf & 0xff, 16));
        }
        return strbuf.toString();
    }

    static byte[] fromHexString(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Get a list of booleans to know which boxes to check
     * @param items list of choices
     * @param selected list of the already selected items
     * @return the list of booleans
     */
    public static List<Boolean> getPreChecked(List<String> items, List<String> selected) {
        List<Boolean> checked = new ArrayList<>();
        for(int i = 0; i < items.size(); i++)
            checked.add(false);

        if(selected == null)
            return checked;

        //read through the array and see if it matches with the items
        for(int i = 0; i < items.size(); i++) {
            for(int j = 0; j < selected.size(); j++) {
                if(items.get(i).equals(selected.get(j)))
                    checked.set(i,true);
            }
        }
        return checked;
    }

    /**
     * Pass this function a list and you get a comma delimited list
     * @param list the list you want to be comma delimited
     * @return the comma delimited string
     */
    public static String formatStringFromList(List<String> list) {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (int i=0; i<list.size(); i++) {
            sb.append(prefix);
            prefix = ", ";
            //makes the last thing have an and
            if (i == list.size() - 2)
                prefix = ", and ";
            if (list.size() == 2)
                prefix = " and ";
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    public static String formatTimestamp(long time){
        Date d = new Date(time);
        return new SimpleDateFormat("MM/dd/yy hh:mma", Locale.US).format(d);
    }

    public static String formatJustTime(long time){
        Date d = new Date(time);
        return new SimpleDateFormat("hh:mma", Locale.US).format(d);
    }

    public static String formatDatestamp(long time){
        Date d = new Date(time);
        return new SimpleDateFormat("MM/dd/yy", Locale.US).format(d);
    }

    //Generates a 16-character password from charSet
    static String generateRandomPassword() {
        char[] charSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQURSTUVWXYZ1234567890!@#$%^&*()[]{}/".toCharArray();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 16; i++)
            s.append(charSet[new Random().nextInt(charSet.length)]);
        return s.toString();
    }

    public static int measureViewWidth(View v){
        int specWidth = View.MeasureSpec.makeMeasureSpec(Math.round(Client.displayWidth), View.MeasureSpec.AT_MOST);
        v.measure(specWidth, specWidth);
        return Math.round(v.getMeasuredWidth() / ((float) Client.dpiDensity / DisplayMetrics.DENSITY_DEFAULT));
    }

    static boolean isValidEmail(String target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void updateAdminList() {

    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


//    public static void makeDummieReports(Context ctx, int num){
//        final String[] categoryItems = ctx.getResources().getStringArray(R.array.category_item);
//        List<String> temp = new ArrayList<>();
//        Collections.addAll(temp, categoryItems);
//        List<String> cats = new ArrayList<>();
//
//        //List of Strings for New/Open/Closed/Resolved
//        List<String> statuses = new ArrayList<>();
//        statuses.add("New");
//        statuses.add("Open");
//        statuses.add("Closed");
//        statuses.add("Resolved");
//
//        //List of Strings for tags
//        List<String> tagList = new ArrayList<>();
//        tagList.add("Billy");
//        tagList.add("Jill");
//        tagList.add("John");
//        tagList.add("Jack");
//        List<String> tagList1 = new ArrayList<>();
//
//        for(int i = 0, j = 0; i < num; i++,j+=43200) {
//            cats.add(temp.get(new Random().nextInt(temp.size()-1)));
//            tagList1.add(tagList.get(new Random().nextInt(tagList.size()-1)));
//            Report newReport = new Report();
//            newReport.setThreatLevel(((Integer) new Random().nextInt(4)).toString());
//            newReport.setDescription("I ate Chocolate");
//            newReport.setLocation("SchoolYard.");
//            newReport.setIncidentTimeStamp((System.currentTimeMillis() + j));
//            newReport.setCategories(cats);
//            newReport.setStatus(statuses.get(new Random().nextInt(statuses.size() - 1)));
//            newReport.setTags(tagList1);
//            Client.reportMap.put(newReport.reportID, newReport);
//            /*WaitDialog dialog = new WaitDialog(ctx);
//            dialog.show();
//            dialog.setMessage("Sending report...");
//            String jayReport = Client.net.gson.toJson(newReport);
//            Client.net.netSend(ctx, "/report/new", jayReport, r->{
//                Client.activeReport = Client.net.gson.fromJson(r, Report.class);
//                dialog.dismiss();
//            });*/
//            cats.remove(0);
//            tagList1.remove(0);
//        }
//    }
}