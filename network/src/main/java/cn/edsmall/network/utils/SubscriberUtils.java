package cn.edsmall.network.utils;

import java.util.HashSet;
import java.util.Iterator;

import io.reactivex.disposables.Disposable;
import io.reactivex.subscribers.DisposableSubscriber;

public class SubscriberUtils {
    private static HashSet<DisposableSubscriber> subscribers = new HashSet<>();
    private static final SubscriberUtils ourInstance = new SubscriberUtils();

    public static SubscriberUtils getInstance() {
        return ourInstance;
    }

    public  void addSubscriber(DisposableSubscriber disposable) {
        subscribers.add(disposable);
    }
    public  void cancel(){
        Iterator<DisposableSubscriber> iterator = subscribers.iterator();
          while (iterator.hasNext()){
              DisposableSubscriber next = iterator.next();
              next.dispose();
              iterator.remove();
              next=null;
          }

    }

    public int allDisposable(){
     return subscribers.size();
    }
}
