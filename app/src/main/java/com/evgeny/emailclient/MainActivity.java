package com.evgeny.emailclient;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.evgeny.emailclient.databinding.ActivityMainBinding;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static String username =
            "suetinevgeny";// change accordingly
    public static String password = "89273710628";// change accordingly
    static Properties props;
    Store store;

    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        props = System.getProperties();
        props.put("mail.store.protocol", "pop3s");
        props.put("mail.pop3.host", "pop.yandex.com");
        props.put("mail.pop3.user", MainActivity.username);
        props.put("mail.pop3.socketFactory", 995);
        props.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.pop3.port", 995);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMessage()
                        .doOnSubscribe(() -> mBinding.refreshView.setRefreshing(true))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Message[]>() {
                            @Override
                            public void onCompleted() {
                                Log.d(TAG, "onCompleted: ");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: " + e.getMessage());
                            }

                            @Override
                            public void onNext(Message[] messages) {
                                initList(messages);
                                mBinding.refreshView.setRefreshing(false);
                            }
                        });
            }
        });
        getMessage()
                .doOnSubscribe(() -> mBinding.refreshView.setRefreshing(true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Message[]>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Message[] messages) {
                        initList(messages);
                        mBinding.refreshView.setRefreshing(false);
                    }
                });
    }

    public class FetchAsync extends AsyncTask<String,Void, Message[]> {

        @Override
        protected void onPreExecute() {
            mBinding.refreshView.setRefreshing(true);
        }

        @Override
        protected Message[] doInBackground(String... tests) {
            return fetch();
        }

        @Override
        protected void onPostExecute(Message[] messages) {
            MainActivity.this.initList(messages);
            mBinding.refreshView.setRefreshing(false);
        }
    }


    public Message[] fetch() {
        try {
            if (store == null) {
                // create properties field
                Session session = Session.getDefaultInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(MainActivity.username, MainActivity.password);

                    }
                });
                // emailSession.setDebug(true);

                // create the POP3 store object and connect with the pop server
                store = session.getStore("pop3s");
                store.connect("pop.yandex.com", MainActivity.username, MainActivity.password);
            }
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();
            Log.d(TAG, "size = " + inbox.getMessageCount());
            inbox.addMessageChangedListener(messageChangedEvent -> {
                Log.d(TAG, "change listener: " + messageChangedEvent.toString());
            });

            return messages;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public Observable<Message[]> getMessage(){
        return Observable.create(subscriber -> {
            try {
                Message[] messages = fetch();
                subscriber.onNext(messages);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public void initList(Message[] messages){
        if(messages!=null){
            mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            mBinding.recyclerView.setAdapter(new MailAdapter(messages));
        }
    }

}
