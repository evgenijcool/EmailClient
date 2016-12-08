package com.evgeny.emailclient;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.evgeny.emailclient.databinding.MailItemBinding;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Evgeny on 06.12.2016.
 */
public class MyViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "MyHolder";
    private MailItemBinding mBinding;

    public static MyViewHolder create(ViewGroup parent) {
        return new MyViewHolder(MailItemBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    private MyViewHolder(MailItemBinding itemView) {
        super(itemView.getRoot());
        this.mBinding = itemView;
    }

    public void bindTo(Message message) {
        try {
            getSubject(message)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(Throwable::printStackTrace)
                    .subscribe(s -> mBinding.date.setText(s));
            getBody(message)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(Throwable::printStackTrace)
                    .doOnCompleted(() -> Log.d(TAG, "bindTo: getBody completed"))
                    .subscribe(s -> mBinding.description.setText(Html.fromHtml(s)));
            getEmail(message)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(Throwable::printStackTrace)
                    .subscribe(s -> mBinding.name.setText(s));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Observable<String> getBody(Message message) {
        return Observable.create(subscriber -> {
            try {
                if (message.getContent() instanceof Multipart) {
                    InputStream inputStream = ((MimeMultipart) message.getContent()).getBodyPart(0).getInputStream();
                    String d = IOUtils.toString(inputStream);
                    Log.d(TAG, "getBody: " + d);
                    inputStream.close();
                    subscriber.onNext(d);
                    subscriber.onCompleted();
                } else {
                    String s = message.getContent().toString();
                    subscriber.onNext(s);
                    subscriber.onCompleted();
                }
            } catch (MessagingException | IOException e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<String> getSubject(Message message) {
        return Observable.create(subscriber -> {
            try {
                String d = message.getSubject();
                Log.d(TAG, "getSubject: " + d);
                subscriber.onNext(d);
                subscriber.onCompleted();
            } catch (MessagingException e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<String> getEmail(Message message) {
        return Observable.create(subscriber -> {
            try {
                String email = ((InternetAddress) message.getFrom()[0]).getAddress();
                Log.d(TAG, "getEmail: " + email);
                subscriber.onNext(email);
                subscriber.onCompleted();
            } catch (MessagingException e) {
                subscriber.onError(e);
            }
        });
    }

}
