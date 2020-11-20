package br.com.infoshop.utils;

import android.graphics.Canvas;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import br.com.infoshop.holder.FavsProjectsViewHolder;
import br.com.infoshop.holder.HomeProjectsViewHolder;

/**
 * Created by ravi on 29/09/17.
 */

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            View foregroundView;
            try {
                foregroundView = ((HomeProjectsViewHolder) viewHolder).viewForeground;
            } catch (ClassCastException e) {
                foregroundView = ((FavsProjectsViewHolder) viewHolder).viewForeground;
            }
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        View foregroundView;
        try {
            foregroundView = ((HomeProjectsViewHolder) viewHolder).viewForeground;
        } catch (ClassCastException e) {
            foregroundView = ((FavsProjectsViewHolder) viewHolder).viewForeground;
        }
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        View foregroundView;
        try {
            foregroundView = ((HomeProjectsViewHolder) viewHolder).viewForeground;
        } catch (ClassCastException e) {
            foregroundView = ((FavsProjectsViewHolder) viewHolder).viewForeground;
        }
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        View foregroundView;
        try {
            foregroundView = ((HomeProjectsViewHolder) viewHolder).viewForeground;
        } catch (ClassCastException e) {
            foregroundView = ((FavsProjectsViewHolder) viewHolder).viewForeground;
        }

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
