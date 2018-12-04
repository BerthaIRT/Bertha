package com.ua.cs495f2018.berthaIRT.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import com.ua.cs495f2018.berthaIRT.Client;
import com.ua.cs495f2018.berthaIRT.FirebaseNet;
import com.ua.cs495f2018.berthaIRT.Message;
import com.ua.cs495f2018.berthaIRT.R;
import com.ua.cs495f2018.berthaIRT.adapter.MessageAdapter;


public class MessagesFragment extends Fragment {

    private EditText editMessageText;
    private MessageAdapter adapter;

    ImageButton msgSendButton;

    public MessagesFragment(){
    }

    class LinearLayoutManagerWrapper extends LinearLayoutManager {

        public LinearLayoutManagerWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater flater, ViewGroup tainer, Bundle savedInstanceState){

        View v = flater.inflate(R.layout.fragment_messages, tainer, false);
        RecyclerView rv = v.findViewById(R.id.chat_recycler_view);

        MessageAdapter.RecyclerViewClickListener listener = (view, position) -> {
//            if(messageList.get(position).sendingError) {
//                resendMessage(position);
//            }
        };

        adapter = new MessageAdapter(getContext(), listener);
        rv.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
        rv.setAdapter(adapter);

        editMessageText = v.findViewById(R.id.input_chat_message);

        msgSendButton = v.findViewById(R.id.button_chat_send);
        msgSendButton.setOnClickListener(view -> sendMessage());

        editMessageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editMessageText.getText().length() > 0)
                    msgSendButton.setAlpha(1.0f);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        return v;
    }

//        adapter.notifyItemRangeRemoved(0, messageList.size());
//        if(m.size() == 0) {
//            messageList.clear();
//            messageList.addAll(m);
//            adapter.notifyDataSetChanged();
//        }
//        else{
//            messageList.add(m.get(m.size()-1)); //check
//            adapter.notifyItemInserted(messageList.size());
//            rv.smoothScrollToPosition(messageList.size() - 1);
//        }
    //fuck u stray bracket}


    private void sendMessage() {
        String msgContent = editMessageText.getText().toString();
        if (!TextUtils.isEmpty(msgContent)) {
            Message m = new Message();
            m.setMessageBody(msgContent);
            Client.activeReport.getMessages().add(m);
            Client.net.syncActiveReport(getContext(), ()->{
                editMessageText.setText("");
                msgSendButton.setAlpha(0.4f);
                //adapter.updateMessages(Client.activeReport.getMessages());
            });

/*            //If there was a problem updating the report then set the error message
            if(!//Client.updateReportMap()) {
                message.sendingError = true;
                messageList.set(messageList.size() - 1, message);
                adapter.notifyDataSetChanged();
            }*/

//            //makes the last sent items time visible
//            for(int i = messageList.size() - 2; i >= 0; i --) {
//                //gets that item
//                Log temp = messageList.get(i);
//                //make sure it belongs to the currentUser
//                if(temp.sender.equals(Client.currentUserName)) {
//                    messageList.set(i, temp);
//                    adapter.notifyDataSetChanged();
//                    break;
//                }
//            }

            //replies back with the same for now
            //Message msgDto1 = new Message(msgContent,"31321");
            //messageList.add(msgDto1);

            //temp if until the messages are sent
//            if(messageList.size() > 0) {
//                adapter.notifyItemInserted(messageList.size() - 1);
//                rv.smoothScrollToPosition(messageList.size() - 1);
//            }
        }
    }

//    public void resendMessage(int position) {
//        Toast.makeText(getActivity(),"RESEND", Toast.LENGTH_SHORT).show();
//
//        Log message = messageList.get(position);
//        //message.sendingError = false;
//
//        //get the current report Object and add the new message
//        Client.activeReport.messages.add(message);
//
//        //If there was a problem updating the report then set the error message back
//        /*if(!//Client.updateReportMap(Client.activeReport))
//            message.sendingError = true;*/
//
//        messageList.set(position, message);
//        adapter.notifyDataSetChanged();
//    }

//    public void recieveMessage() {
//        //TODO Scott look at
//        Client.net.netSend("message/get", null, (r)->{
//            JsonObject jay = Client.net.jp.parse(r).getAsJsonObject();
//            for(Map.Entry<String, JsonElement> e : jay.entrySet())
//                messageList.add(Client.net.gson.fromJson(e.getValue().getAsString(), Log.class));
//        });
//    }

    @Override
    public void onResume(){
        super.onResume();
        if(adapter == null) return;
        adapter.updateMessages(Client.activeReport.getMessages());
        FirebaseNet.setOnRefreshHandler((r)->{
            if(Integer.valueOf(r).equals(Client.activeReport.getReportID()))
                adapter.updateMessages(Client.activeReport.getMessages());
        });
    }
}
