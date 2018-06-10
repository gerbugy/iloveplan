package com.iloveplan.android.asis.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.iloveplan.android.R;

public class DraggableListView extends ListView {

    private ImageView mDragView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private int mDragPos; // At which position is the item currently being dragged. Note that this takes in to account header items.
    private int mSrcDragPos; // At which position was the item being dragged originally
    private int mDragPointX; // at what x offset inside the item did the user grab it
    private int mDragPointY; // at what y offset inside the item did the user grab it
    private int mXOffset; // the difference between screen coordinates and coordinates in this view
    private int mYOffset; // the difference between screen coordinates and coordinates in this view
    private DragListener mDragListener;
    private DropListener mDropListener;
    private RemoveListener mRemoveListener;
    private int mUpperBound;
    private int mLowerBound;
    private int mHeight;
    private GestureDetector mGestureDetector;
    private static final int FLING = 0;
    private static final int SLIDE = 1;
    private static final int TRASH = 2;
    private int mRemoveMode = -1;
    private Rect mTempRect = new Rect();
    private Bitmap mDragBitmap;
    private final int mTouchSlop;
    private int mItemHeightNormal;
    private int mItemHeightExpanded;
    private int mItemHeightHalf;
    private Drawable mTrashcan;

    public DraggableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO SharedPreferences pref = context.getSharedPreferences("Music", 3);
        // TODO mRemoveMode = pref.getInt("deletemode", -1);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        // TODO Resources res = getResources();
        // TODO mItemHeightNormal = res.getDimensionPixelSize(R.dimen.normal_height);
        // TODO mItemHeightHalf = mItemHeightNormal / 2;
        // TODO mItemHeightExpanded = res.getDimensionPixelSize(R.dimen.expanded_height);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mRemoveListener != null && mGestureDetector == null) {
            if (mRemoveMode == FLING) {
                mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        if (mDragView != null) {
                            if (velocityX > 1000) {
                                Rect r = mTempRect;
                                mDragView.getDrawingRect(r);
                                if (e2.getX() > r.right * 2 / 3) {
                                    // fast fling right with release near the right edge of the screen
                                    stopDragging();
                                    mRemoveListener.remove(mSrcDragPos);
                                    unExpandViews(true);
                                }
                            }
                            // flinging while dragging should have no effect
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
        // TODO if (mDragListener != null || mDropListener != null) {
        if (mDropListener != null) { // TODO 추가
            switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                int itemnum = pointToPosition(x, y);
                if (itemnum == AdapterView.INVALID_POSITION) {
                    break;
                }
                ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
                mItemHeightNormal = item.getHeight(); // TODO 추가
                mItemHeightHalf = mItemHeightNormal / 2; // TODO 추가
                mItemHeightExpanded = mItemHeightNormal * 2; // TODO 추가
                mDragPointX = x - item.getLeft();
                mDragPointY = y - item.getTop();
                mXOffset = ((int) ev.getRawX()) - x;
                mYOffset = ((int) ev.getRawY()) - y;
                // The left side of the item is the grabber for dragging the item
                if (x < 84) { // TODO 원래 64였음
                    item.setDrawingCacheEnabled(true);
                    // Create a copy of the drawing cache so that it does not get recycled
                    // by the framework when the list tries to clean up memory
                    Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
                    item.setDrawingCacheEnabled(false); // TODO 잔상방지를 위해 추가(2012.10.10)
                    startDragging(bitmap, x, y);
                    mDragPos = itemnum;
                    mSrcDragPos = mDragPos;
                    mHeight = getHeight();
                    int touchSlop = mTouchSlop;
                    mUpperBound = Math.min(y - touchSlop, mHeight / 3);
                    mLowerBound = Math.max(y + touchSlop, mHeight * 2 / 3);
                    return true; // TODO false를 true로 변경(2012.10.11)
                }
                stopDragging();
                break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    private int myPointToPosition(int x, int y) {
        if (y < 0) {
            int pos = myPointToPosition(x, y + mItemHeightNormal);
            if (pos > 0) {
                return pos - 1;
            }
        }
        Rect frame = mTempRect;
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            child.getHitRect(frame);
            if (frame.contains(x, y)) {
                return getFirstVisiblePosition() + i;
            }
        }
        return INVALID_POSITION;
    }

    private int getItemForPosition(int y) {
        int adjustedy = y - mDragPointY - mItemHeightHalf;
        int pos = myPointToPosition(0, adjustedy);
        if (pos >= 0) {
            if (pos <= mSrcDragPos) {
                pos += 1;
            }
        } else if (adjustedy < 0) {
            pos = 0;
        }
        return pos;
    }

    private void adjustScrollBounds(int y) {
        if (y >= mHeight / 3) {
            mUpperBound = mHeight / 3;
        }
        if (y <= mHeight * 2 / 3) {
            mLowerBound = mHeight * 2 / 3;
        }
    }

    private void unExpandViews(boolean deletion) {
        for (int i = 0;; i++) {
            View v = getChildAt(i);
            if (v == null) {
                if (deletion) {
                    // HACK force update of mItemCount
                    int position = getFirstVisiblePosition();
                    int y = getChildAt(0).getTop();
                    setAdapter(getAdapter());
                    setSelectionFromTop(position, y);
                    // end hack
                }
                try {
                    layoutChildren(); // force children to be recreated where needed
                    v = getChildAt(i);
                } catch (IllegalStateException ex) {
                    // layoutChildren throws this sometimes, presumably because we're
                    // in the process of being torn down but are still getting touch
                    // events
                }
                if (v == null) {
                    return;
                }
            }
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.height = mItemHeightNormal;
            v.setLayoutParams(params);
            v.setVisibility(View.VISIBLE);
        }
    }

    private void doExpansion() {
        int childnum = mDragPos - getFirstVisiblePosition();
        if (mDragPos > mSrcDragPos) {
            childnum++;
        }
        int numheaders = getHeaderViewsCount();
        View first = getChildAt(mSrcDragPos - getFirstVisiblePosition());
        for (int i = 0;; i++) {
            View vv = getChildAt(i);
            if (vv == null) {
                break;
            }
            int height = mItemHeightNormal;
            int visibility = View.VISIBLE;
            if (mDragPos < numheaders && i == numheaders) {
                if (vv.equals(first)) {
                    visibility = View.INVISIBLE;
                } else {
                    height = mItemHeightExpanded;
                }
            } else if (vv.equals(first)) {

                // TODO
                // 최하단건을 최상단으로 옮기면 중간에 빈행이 생기는 현상이 발견되었습니다.
                // 그래서 아래와 같이 변경했습니다.
                if (mDragPos == mSrcDragPos) {
                    visibility = View.INVISIBLE;
                } else if (mDragPos >= getCount() && mSrcDragPos == getCount() - 1) {
                    visibility = View.INVISIBLE;
                } else {
                    height = 1;
                }
            } else if (i == childnum) {
                if (mDragPos >= numheaders && mDragPos < getCount() - 1) {
                    height = mItemHeightExpanded;
                }
            }
            ViewGroup.LayoutParams params = vv.getLayoutParams();
            params.height = height;
            vv.setLayoutParams(params);
            vv.setVisibility(visibility);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(ev);
        }
        // TODO if ((mDragListener != null || mDropListener != null) && mDragView != null) {
        if (mDropListener != null && mDragView != null) { // TODO 추가
            int action = ev.getAction();
            switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Rect r = mTempRect;
                mDragView.getDrawingRect(r);
                stopDragging();
                if (mRemoveMode == SLIDE && ev.getX() > r.right * 3 / 4) {
                    if (mRemoveListener != null) {
                        mRemoveListener.remove(mSrcDragPos);
                    }
                    unExpandViews(true);
                } else {
                    if (mDropListener != null && mDragPos >= 0 && mDragPos < getCount()) {
                        mDropListener.drop(mSrcDragPos, mDragPos);
                    }
                    unExpandViews(false);
                }
                break;

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                dragView(x, y);
                int itemnum = getItemForPosition(y);

                if (itemnum >= 0) {
                    if (action == MotionEvent.ACTION_DOWN || itemnum != mDragPos) {
                        if (mDragListener != null) {
                            mDragListener.drag(mDragPos, itemnum);
                        }
                        mDragPos = itemnum;
                        doExpansion();
                    }
                    int speed = 0;
                    adjustScrollBounds(y);
                    if (y > mLowerBound) {
                        // scroll the list up a bit
                        if (getLastVisiblePosition() < getCount() - 1) {
                            speed = y > (mHeight + mLowerBound) / 2 ? 16 : 4;
                        } else {
                            speed = 1;
                        }
                    } else if (y < mUpperBound) {
                        // scroll the list down a bit
                        speed = y < mUpperBound / 2 ? -16 : -4;
                        if (getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= getPaddingTop()) {
                            // if we're already at the top, don't try to scroll, because
                            // it causes the framework to do some extra drawing that messes
                            // up our animation
                            speed = 0;
                        }
                    }
                    if (speed != 0) {
                        smoothScrollBy(speed, 30);
                    }
                }
                break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private void startDragging(Bitmap bm, int x, int y) {
        stopDragging();

        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowParams.x = x - mDragPointX + mXOffset;
        mWindowParams.y = y - mDragPointY + mYOffset;

        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        Context context = getContext();
        ImageView v = new ImageView(context);
        // int backGroundColor = context.getResources().getColor(R.color.dragndrop_background);
        // v.setBackgroundColor(backGroundColor);
        // TODO v.setBackgroundResource(R.drawable.playlist_tile_drag);
        v.setImageBitmap(bm); // TODO 추가
        v.setBackgroundResource(R.drawable.list_item_tile_drag); // TODO 추가
        v.setPadding(0, 0, 0, 0);
        v.setImageBitmap(bm);
        mDragBitmap = bm;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(v, mWindowParams);
        mDragView = v;
    }

    private void dragView(int x, int y) {
        if (mRemoveMode == SLIDE) {
            float alpha = 1.0f;
            int width = mDragView.getWidth();
            if (x > width / 2) {
                alpha = ((float) (width - x)) / (width / 2);
            }
            mWindowParams.alpha = alpha;
        }

        if (mRemoveMode == FLING || mRemoveMode == TRASH) {
            mWindowParams.x = x - mDragPointX + mXOffset;
        } else {
            mWindowParams.x = 0;
        }
        mWindowParams.y = y - mDragPointY + mYOffset;
        mWindowManager.updateViewLayout(mDragView, mWindowParams);

        if (mTrashcan != null) {
            int width = mDragView.getWidth();
            if (y > getHeight() * 3 / 4) {
                mTrashcan.setLevel(2);
            } else if (width > 0 && x > width / 4) {
                mTrashcan.setLevel(1);
            } else {
                mTrashcan.setLevel(0);
            }
        }
    }

    private void stopDragging() {
        if (mDragView != null) {
            mDragView.setVisibility(GONE);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
        if (mDragBitmap != null) {
            mDragBitmap.recycle();
            mDragBitmap = null;
        }
        if (mTrashcan != null) {
            mTrashcan.setLevel(0);
        }
    }

    public void setTrashcan(Drawable trash) {
        mTrashcan = trash;
        mRemoveMode = TRASH;
    }

    public void setDragListener(DragListener l) {
        mDragListener = l;
    }

    public void setDropListener(DropListener l) {
        mDropListener = l;
    }

    public void setRemoveListener(RemoveListener l) {
        mRemoveListener = l;
    }

    public interface DragListener {
        void drag(int from, int to);
    }

    public interface DropListener {
        void drop(int from, int to);
    }

    public interface RemoveListener {
        void remove(int which);
    }
}
