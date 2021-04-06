package cn.edsmall.tablayoutfloatting.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import cn.edsmall.router_annotation.destination.FragmentDestination;
import cn.edsmall.tablayoutfloatting.R;
import cn.edsmall.tablayoutfloatting.adapter.RecyclerAdapter;
import cn.edsmall.tablayoutfloatting.ui.notifications.NotificationsViewModel;

public class RecyclerViewFragment extends Fragment {
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        recyclerView = root.findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(manager);
        RecyclerAdapter adapter = new RecyclerAdapter(getData(), this.getActivity());
        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
//        final RecyclerAdapter adapter=new RecyclerAdapter(getData());
//        recyclerView.setAdapter(adapter);
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            private static final int THRESHOLD_LOAD_MORE=3;
//            private boolean hasLoadMore;
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//              if (newState==RecyclerView.SCROLL_STATE_SETTLING){
//                  hasLoadMore=false;
//              }
//              if (newState!=RecyclerView.SCROLL_STATE_SETTLING&&!hasLoadMore){
//                  int[] positions=new int[2];
//                  int lastPosition=((StaggeredGridLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPositions(positions).length;
//                  int offset=recyclerView.getAdapter().getItemCount()-lastPosition-1;
//                  if (offset<=THRESHOLD_LOAD_MORE){
//                      hasLoadMore=true;
//                      adapter.data.addAll(getData());
//                      adapter.notifyDataSetChanged();
//                  }
//              }
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });

        return root;
    }



    private List<String> getData() {
        List<String> data = new ArrayList<>();
        for (int i=0; i < 10; i++) {
            data.add("ChildView item" + i);
        }
        return data;
    }
}