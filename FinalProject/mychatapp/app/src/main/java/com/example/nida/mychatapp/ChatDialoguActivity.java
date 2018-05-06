package com.example.nida.mychatapp;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.nida.mychatapp.Adapter.ChatDialogAdapter;
import com.example.nida.mychatapp.Common.Common;
import com.example.nida.mychatapp.Holder.QBusersHolder;
import com.example.nida.mychatapp.ListUserActivity;
import com.example.nida.mychatapp.R;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.OutputStream;
import java.util.ArrayList;

public class ChatDialoguActivity extends AppCompatActivity
{
    FloatingActionButton floatingActionButton;
    ListView FirstChatdialog;

   @Override
   protected void onResume()
    {
        super.onResume();
        loadChatDialog();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialogu);

        createSessionForChat();

        FirstChatdialog=(ListView)findViewById(R.id.FirstChatDialog);

        FirstChatdialog.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                QBChatDialog qbChatDialog=(QBChatDialog)FirstChatdialog.getAdapter().getItem(position);
                Intent intent=new Intent(ChatDialoguActivity.this,ChatMessageActivity.class);
                intent.putExtra(Common.DIALOG_EXTRA,qbChatDialog);
                startActivity(intent);
            }
        });


        loadChatDialog();

        floatingActionButton=(FloatingActionButton)findViewById(R.id.chatdialog_adduser);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v)
            {

                Intent intent=new Intent(ChatDialoguActivity.this,ListUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadChatDialog()
    {
        QBRequestGetBuilder requestBuilder= new QBRequestGetBuilder();

        requestBuilder.setLimit(100);

        QBRestChatService.getChatDialogs(null,requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle)
            {

                ChatDialogAdapter adpater=new ChatDialogAdapter(getBaseContext(),qbChatDialogs);

                FirstChatdialog.setAdapter(adpater);

                adpater.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {

                Log.e("Error",e.getMessage());

            }
         });
    }

    private void createSessionForChat()
    {

        final ProgressDialog mdialogue=new ProgressDialog(ChatDialoguActivity.this);
        mdialogue.setMessage("Waiting....I am chat dialogue activity");
        mdialogue.setCanceledOnTouchOutside(false);
        mdialogue.show();

        final String user,password;
        user=getIntent().getStringExtra("User");
        password=getIntent().getStringExtra("Password");

        //load all users and save to cache

        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                QBusersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });


        final QBUser qbUser=new QBUser(user,password);

        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>()
        {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle)
            {

                qbSession.setId(qbSession.getUserId());
                try
                {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                }
                catch (BaseServiceException e)
                {
                    e.printStackTrace();
                }

                QBChatService.getInstance().login(qbUser, new QBEntityCallback()
                {
                    @Override
                    public void onSuccess(Object o, Bundle bundle)
                    {
                        mdialogue.dismiss();
                    }

                    @Override
                    public void onError(QBResponseException e)
                    {

                        Log.e("Error" + "",e.getMessage());

                    }
                });

            }

            @Override
            public void onError(QBResponseException e)
            {

            }
        });
    }
}
