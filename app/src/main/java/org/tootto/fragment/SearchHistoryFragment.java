package org.tootto.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.tootto.App;
import org.tootto.R;
import org.tootto.adapter.SearchHistoryAdapter;
import org.tootto.dao.SearchHistory;
import org.tootto.dao.SearchHistoryDao;
import org.tootto.listener.SearchActionListener;
import org.tootto.util.ListUtils;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SearchHistoryFragment extends DialogFragment implements SearchActionListener {
    RecyclerView recycler_viewSearchHistory;
    SearchHistoryAdapter searchHistoryAdapter;
    LinearLayoutManager linearLayoutManager;
    EditText et_searchHistory;
    ImageView iv_arrow_back;
    ImageView iv_search_confirm;
    private static SearchHistoryDao searchHistoryDao = App.getDB().searchHistoryDao();
    public static SearchHistoryFragment newInstance(int title){
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.search_history_fragment, container, false);
        recycler_viewSearchHistory = inflate.findViewById(R.id.recycler_viewSearchHistory);
        et_searchHistory = inflate.findViewById(R.id.et_searchHistory);
        iv_arrow_back = inflate.findViewById(R.id.iv_arrow_back);
        iv_search_confirm = inflate.findViewById(R.id.iv_search_confirm);
        iv_search_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = et_searchHistory.getText().toString().trim();
                insertData(s);
            }
        });

        final Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                |WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        searchHistoryAdapter = new SearchHistoryAdapter(this);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recycler_viewSearchHistory.setAdapter(searchHistoryAdapter);
        recycler_viewSearchHistory.setLayoutManager(linearLayoutManager);
        recycler_viewSearchHistory.setHasFixedSize(true);
//        ArrayList<SearchHistory> searchHistories = new ArrayList<SearchHistory>();
//        SearchHistory searchHistory = new SearchHistory();
//        searchHistory.searchHistoryText="aaa";
//        searchHistory.id=111;
//        searchHistories.add(searchHistory);
//        searchHistories.add(searchHistory);
//        searchHistories.add(searchHistory);
//        searchHistoryAdapter.setSearchHistoryData(searchHistories);
//        searchHistoryAdapter.notifyDataSetChanged();

//        searchHistoryDao.queryAll()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(searchHistories ->{
//                    if (!ListUtils.isEmpty(searchHistories)){
//                        searchHistoryAdapter.setData(searchHistories);
//                    }
//                }, error ->{
//                    Log.i("fragment","queryError");
//                },()->{}
//                );
        searchHistoryDao.queryAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<SearchHistory>>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(List<SearchHistory> searchHistories) {
                        if (!ListUtils.isEmpty(searchHistories)){
                        searchHistoryAdapter.setData(searchHistories);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i("fragment","queryError");
                    }

                    @Override
                    public void onComplete() {

                    }
                });


//        Flowable.create(new FlowableOnSubscribe<List>(){
//            @Override
//            public void subscribe(FlowableEmitter<List> e) throws Exception {
//                List<SearchHistory> searchHistories;
//                searchHistories = searchHistoryDao.queryAll();
//                e.onNext(searchHistories);
//                e.onComplete();
//            }
//        }, BackpressureStrategy.BUFFER)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List>() {
//            @Override
//            public void onSubscribe(Subscription s) {
//                s.request(Long.MAX_VALUE);
//                //subscription = s;
//            }
//            @Override
//            public void onNext(List list) {
//                if (!ListUtils.isEmpty(list)){
//                    searchHistoryAdapter.setData(list);
//                }
//            }
//            @Override
//            public void onError(Throwable t) {
//            }
//            @Override
//            public void onComplete() {}
//        });
        recycler_viewSearchHistory.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(1000);
        defaultItemAnimator.setRemoveDuration(1000);
        recycler_viewSearchHistory.setItemAnimator(defaultItemAnimator);


        return inflate;
    }

//    private void insertData(String s) {
//        SearchHistory searchHistory = new SearchHistory();
//        searchHistory.searchHistoryText = s;
//        Flowable.create(new FlowableOnSubscribe<List>(){
//            @Override
//            public void subscribe(FlowableEmitter<List> e) throws Exception {
//                searchHistoryDao.insertSearchHistory(searchHistory);
//                List list = searchHistoryDao.queryAll();
//                e.onNext(list);
//                e.onComplete();
//            }
//        }, BackpressureStrategy.BUFFER)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List>() {
//            @Override
//            public void onSubscribe(Subscription s) {
//                s.request(Long.MAX_VALUE);
//                //subscription = s;
//            }
//            @Override
//            public void onNext(List list) {
//                if (!ListUtils.isEmpty(list)){
//                    searchHistoryAdapter.setData(list);
//                }
//            }
//            @Override
//            public void onError(Throwable t) {
//            }
//            @Override
//            public void onComplete() {}
//        });
//
//    }

    private void insertData(String s) {


        Flowable.create(new FlowableOnSubscribe<SearchHistory>(){
            @Override
            public void subscribe(FlowableEmitter<SearchHistory> e) throws Exception {
                SearchHistory searchHistory = new SearchHistory(s);
                List<SearchHistory> queryResults;
                queryResults = searchHistoryDao.queryText(s);
                if (ListUtils.isEmpty(queryResults)){
                    searchHistoryDao.insertSearchHistory(searchHistory);

                }else {
                    searchHistoryDao.deleteHistory(searchHistory);
                    searchHistoryDao.insertSearchHistory(searchHistory);
                }
                e.onNext(searchHistory);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<SearchHistory>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }
            @Override
            public void onNext(SearchHistory searchHistory) {
                searchHistoryAdapter.insertData(searchHistoryAdapter.getItemCount(), searchHistory);
            }
            @Override
            public void onError(Throwable t) {
            }
            @Override
            public void onComplete() {}
        });

    }

    public void deleteData(int position){

        Flowable.create(new FlowableOnSubscribe<Integer>(){
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {

                int i = searchHistoryDao.deleteHistory(searchHistoryAdapter.getData().get(position));
                Log.i("delete", "id"+i);
//                List list = searchHistoryDao.queryAllList();
                e.onNext(i);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
                //subscription = s;
            }
            @Override
            public void onNext(Integer i) {
                if (i == 1){
                    searchHistoryAdapter.removeData(position);
                }
            }
            @Override
            public void onError(Throwable t) {
            }
            @Override
            public void onComplete() {}
        });
    }

    @Override
    public void onItemClick(int position) {
        deleteData(position);
    }
}
