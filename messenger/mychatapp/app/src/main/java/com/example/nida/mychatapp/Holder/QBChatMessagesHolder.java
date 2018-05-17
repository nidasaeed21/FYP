package com.example.nida.mychatapp.Holder;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QBChatMessagesHolder
{
    private static QBChatMessagesHolder instance;
    private HashMap<String,ArrayList<QBChatMessage>> qbChatMessageArray;

    public static synchronized QBChatMessagesHolder getInstance()
    {
        QBChatMessagesHolder qbChatMessagesHolder;
        synchronized (QBChatMessagesHolder.class)
        {
            if(instance==null)
                instance=new QBChatMessagesHolder();
            qbChatMessagesHolder=instance;
        }
        return qbChatMessagesHolder;

    }

    private QBChatMessagesHolder()
    {
        this.qbChatMessageArray=new HashMap<>();
    }

    public  void  putMessages(String dialogId,ArrayList<QBChatMessage> qbChatMessages)
    {
        this.qbChatMessageArray.put(dialogId,qbChatMessages);
    }

    public void putMessage(String dialogId,QBChatMessage qbChatMessage)
    {
        List<QBChatMessage> FirstResult=(List)this.qbChatMessageArray.get(dialogId);
        FirstResult.add(qbChatMessage);
        ArrayList<QBChatMessage> FirstAdded=new ArrayList<>(FirstResult.size());
        FirstAdded.addAll(FirstResult);

    }

    public  ArrayList<QBChatMessage> getChatMessageByDialogId(String dialogID)
    {
        return (ArrayList<QBChatMessage>)this.qbChatMessageArray.get(dialogID);
    }
}
