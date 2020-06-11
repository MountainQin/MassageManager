package com.baima.massagemanager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * 可以用来监听RecyclerView是否滚动到最后，从而加载更多数据
 */
public abstract class OnLoadMoreListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager linearLayoutManager;
    private int itemCount;
    private int lastPosition;
    private boolean isScrollToLast;

    public abstract void onLoadMore(int lastPosition);

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //当停止滑动时
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            itemCount = linearLayoutManager.getItemCount();
            lastPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

            // 判断是否滚动到底部，并且是向后滚动
            if (lastPosition == itemCount - 1 && isScrollToLast) {
                this.onLoadMore(lastPosition);
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
        //dx>0:向右滑动,dx<0:向左滑动
        //dy>0:向下滑动,dy<0:向上滑动
        if (dy > 0) {
            isScrollToLast = true;
        } else {
            isScrollToLast = false;
        }
    }
}
