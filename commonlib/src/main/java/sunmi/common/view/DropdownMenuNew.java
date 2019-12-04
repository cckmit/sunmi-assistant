package sunmi.common.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.commonlibrary.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yinhui
 * @date 2019-11-11
 */
public class DropdownMenuNew extends FrameLayout implements View.OnClickListener {

    public static final int MODE_DIALOG = 0;
    public static final int MODE_POPUP = 1;
    public static final int MODE_CUSTOM = 2;
    private static final int MODE_DEFAULT = -1;

    private int mode;
    private boolean isAnimated = true;
    private boolean isAutoDismiss = true;
    private boolean isMultiSelect = false;

    private DropdownPopup popup;
    private DropdownAnimation animation;
    private RecyclerView.LayoutManager manager;

    private ViewHolder title;
    private boolean isPopupInit = false;

    public DropdownMenuNew(@NonNull Context context) {
        this(context, MODE_DEFAULT);
    }

    public DropdownMenuNew(@NonNull Context context, int mode) {
        this(context, null, mode);
    }

    public DropdownMenuNew(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, MODE_DEFAULT);
    }

    public DropdownMenuNew(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, MODE_DEFAULT);
    }

    public DropdownMenuNew(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0, mode);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DropdownMenuNew(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes, mode);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.DropdownMenuNew, defStyleAttr, defStyleRes);

        // 获取自定义布局
        int layoutId = a.getResourceId(R.styleable.DropdownMenuNew_dm_layout, 0);
        int listId = a.getResourceId(R.styleable.DropdownMenuNew_dm_list, 0);
        if (layoutId == 0) {
            layoutId = R.layout.dropdown_menu_item_rv;
            listId = R.id.dropdown_rv;
        }

        // 获取下拉菜单的样式
        if (mode == MODE_DEFAULT) {
            mode = a.getInt(R.styleable.DropdownMenuNew_dm_style, MODE_CUSTOM);
        }

        switch (mode) {
            case MODE_DIALOG:
                break;
            case MODE_POPUP:
                break;
            case MODE_CUSTOM:
                popup = new ViewListPopup(context, layoutId, listId);
                break;
            default:
        }

        // 列表最大高度所展示的列表元素个数 / 列表最大高度限定，元素个数优先级高
        float count = a.getFloat(R.styleable.DropdownMenuNew_dm_maxHeightCount, -1);
        float height = a.getDimension(R.styleable.DropdownMenuNew_dm_maxHeight, -1);
        if (count > 0 || height > 0) {
            manager = new FixedLayoutManager(context, count, height);
        }

        // 点击选择后是否自动dismiss菜单
        isAutoDismiss = a.getBoolean(R.styleable.DropdownMenuNew_dm_autoDismiss, isAutoDismiss);
        // 是否为多选
        isMultiSelect = a.getBoolean(R.styleable.DropdownMenuNew_dm_multiSelect, isMultiSelect);
        // 是否开启动画
        isAnimated = a.getBoolean(R.styleable.DropdownMenuNew_dm_animated, isAnimated);

        a.recycle();
    }

    public void setAdapter(Adapter adapter) {
        popup.init();
        popup.setAdapter(adapter);
        adapter.setMenu(this);
        title = adapter.getTitle();
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        addView(title.itemView, lp);
        adapter.updateTitleAndContent();
        setOnClickListener(this);
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        this.manager = manager;
        popup.setLayoutManager(manager);
    }

    public boolean isShowing() {
        return popup.isShowing();
    }

    public void show(boolean animated) {
        popup.show(animated);
    }

    public void dismiss(boolean animated) {
        popup.dismiss(animated);
    }

    public void toggle(boolean animated) {
        if (isShowing()) {
            dismiss(animated);
        } else {
            show(animated);
        }
    }

    @Override
    public void onClick(View v) {
        toggle(isAnimated);
    }

    /**
     * Implements some sort of popup selection interface for selecting a dropdown menu option.
     * Allows for different dropdown menu modes.
     */
    private interface DropdownPopup {

        /**
         * Initiation for popup
         */
        void init();

        /**
         * Setup adapter for dropdown menu.
         *
         * @param adapter Adapter
         */
        void setAdapter(Adapter adapter);

        /**
         * Setup layout manager for recycler view in dropdown menu
         *
         * @param manager layout manager
         */
        void setLayoutManager(RecyclerView.LayoutManager manager);

        /**
         * whether popup is showing or not.
         *
         * @return true if the popup is showing, false otherwise.
         */
        boolean isShowing();

        /**
         * Show the popup
         *
         * @param animated is animated or not
         */
        void show(boolean animated);

        /**
         * Dismiss the popup
         *
         * @param animated is animated or not
         */
        void dismiss(boolean animated);

    }

    private class ViewListPopup implements DropdownPopup {

        private Context context;
        private ViewHolder title;
        private AnimatorSet currentAnim;
        private int layoutId;
        private int listId;

        boolean isInit = false;
        boolean isShowing = false;
        private ViewGroup container;
        private RecyclerView list;
        private View overlay;

        public ViewListPopup(Context context, @LayoutRes int layoutId, @IdRes int listId) {
            this.context = context;
            this.layoutId = layoutId;
            this.listId = listId;
        }

        @Override
        public void init() {
            if (isInit) {
                return;
            }
            if (getId() == View.NO_ID) {
                setId(View.generateViewId());
            }
            // 获取添加Menu和Overlay的视图index
            ViewGroup parent = (ViewGroup) getParent();
            int index = getIndexOfMenu();
            if (index < 0) {
                throw new IllegalStateException("No DropdownMenu in the layout: "
                        + parent.getClass().getSimpleName());
            }

            // 创建和初始化Menu列表View
            container = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, parent, false);
            parent.addView(container, index);
            initContainer(parent);

            // 创建和初始化Overlay
            overlay = new View(context);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            parent.addView(overlay, index, lp);
            initOverlay(parent);

            // 查找并获取Menu列表中的RecyclerView对象
            int count;
            if (listId == 0) {
                count = container.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = container.getChildAt(i);
                    if (child instanceof RecyclerView) {
                        list = (RecyclerView) child;
                        break;
                    }
                }
                if (list == null) {
                    throw new RuntimeException("No RecyclerView in container layout: "
                            + container.getClass().getSimpleName());
                }
            } else {
                list = container.findViewById(listId);
            }

            // 初始化List动画和布局管理器
            if (animation == null) {
                animation = new DefaultAnimation();
            }
            if (manager == null) {
                manager = new LinearLayoutManager(container.getContext());
            }
            list.setLayoutManager(manager);
            isInit = true;
        }

        private void initContainer(ViewGroup parent) {
            ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
            container.setVisibility(INVISIBLE);
            if (container.getId() == View.NO_ID) {
                container.setId(View.generateViewId());
            }
            container.setBackgroundColor(ContextCompat.getColor(context, R.color.c_white));
            int menuTop = getBottom();
            if (parent instanceof FrameLayout) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layoutParams;
                lp.setMargins(0, menuTop, 0, 0);
            } else if (parent instanceof RelativeLayout) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layoutParams;
                lp.addRule(RelativeLayout.BELOW, getId());
            } else if (parent instanceof ConstraintLayout) {
                ConstraintSet set = new ConstraintSet();
                set.clone((ConstraintLayout) parent);
                set.connect(container.getId(), ConstraintSet.TOP, getId(), ConstraintSet.BOTTOM, 0);
                if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    set.connect(container.getId(), ConstraintSet.BOTTOM,
                            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
                    set.setVerticalBias(container.getId(), 0f);
                    set.constrainDefaultHeight(container.getId(), ConstraintSet.MATCH_CONSTRAINT_WRAP);
                    set.constrainHeight(container.getId(), ConstraintSet.MATCH_CONSTRAINT);
                }
                set.applyTo((ConstraintLayout) parent);
            } else {
                throw new UnsupportedOperationException("Unsupported parent layout type:"
                        + parent.getClass().getSimpleName()
                        + "; Only FrameLayout, RelativeLayout & ConstraintLayout allowed.");
            }
        }

        private void initOverlay(ViewGroup parent) {
            // TODO: 同页面复用
            overlay.setId(View.generateViewId());
            overlay.setBackgroundColor(ContextCompat.getColor(context, R.color.black_60));
            overlay.setAlpha(0f);
            overlay.setOnClickListener(v -> dismiss(isAnimated));
        }

        @Override
        public void setAdapter(Adapter adapter) {
            title = adapter.getTitle();
            list.setAdapter(adapter);
            adapter.setContent(container);
        }

        @Override
        public void setLayoutManager(RecyclerView.LayoutManager manager) {
            if (list != null) {
                list.setLayoutManager(manager);
            }
        }

        @Override
        public boolean isShowing() {
            return isShowing;
        }

        @Override
        public void show(boolean animated) {
            if (isShowing) {
                return;
            }
            isShowing = true;
            if (container.getVisibility() == INVISIBLE) {
                dismissWithoutAnim();
                container.setVisibility(VISIBLE);
            }
            if (animated && animation != null) {
                AnimatorSet set = animation.showAnimator(title, container, overlay);
                addShowAnimListener(set);
                if (currentAnim != null && currentAnim.isRunning()) {
                    currentAnim.cancel();
                }
                set.start();
                currentAnim = set;
            } else {
                showWithoutAnim();
            }
        }

        @Override
        public void dismiss(boolean animated) {
            if (!isShowing) {
                return;
            }
            isShowing = false;
            if (animated && animation != null) {
                AnimatorSet set = animation.dismissAnimator(title, container, overlay);
                addDismissAnimListener(set);
                if (currentAnim != null && currentAnim.isRunning()) {
                    currentAnim.cancel();
                }
                set.start();
                currentAnim = set;
            } else {
                dismissWithoutAnim();
            }
        }

        private int getIndexOfMenu() {
            ViewGroup parent = (ViewGroup) getParent();
            int count = parent.getChildCount();
            int index = 0;
            for (; index < count; index++) {
                if (parent.getChildAt(index) instanceof DropdownMenuNew) {
                    return index;
                }
            }
            return -1;
        }

        private void addShowAnimListener(AnimatorSet set) {
            if (set.getListeners() == null || set.getListeners().isEmpty()) {
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        showWithoutAnim();
                    }
                });
            }
        }

        private void addDismissAnimListener(AnimatorSet set) {
            if (set.getListeners() == null || set.getListeners().isEmpty()) {
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        dismissWithoutAnim();
                    }
                });
            }
        }

        private void showWithoutAnim() {
            container.setTranslationY(0);
            overlay.setAlpha(1f);
        }

        private void dismissWithoutAnim() {
            container.setTranslationY(-container.getHeight());
            overlay.setAlpha(0f);
        }

    }

    public static abstract class Adapter<T extends Model>
            extends RecyclerView.Adapter<ViewHolder<T>> {

        private DropdownMenuNew menu;
        private ViewHolder<T> title;
        private ViewHolder<T> content;

        @LayoutRes
        private int itemRes;

        private List<T> data = new ArrayList<>();
        private List<T> selected = new ArrayList<>();

        private OnItemClickListener<T> listener;

        protected Adapter(Context context, @LayoutRes int titleRes, @LayoutRes int itemRes) {
            View title = LayoutInflater.from(context).inflate(titleRes, null);
            this.title = new ViewHolder<>(title);
            this.itemRes = itemRes;
        }

        protected Adapter(Context context, View title, @LayoutRes int itemRes) {
            this.title = new ViewHolder<>(title);
            this.itemRes = itemRes;
        }

        public void setMenu(DropdownMenuNew menu) {
            this.menu = menu;
        }

        public void setContent(View content) {
            this.content = new ViewHolder<>(content);
        }

        @Override
        @NonNull
        public ViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(itemRes, parent, false);
            ViewHolder<T> holder = new ViewHolder<>(itemView);
            holder.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                T model = data.get(position);
                setSelected(position);
                if (listener != null) {
                    listener.onItemSelected(this, model, position);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder<T> holder, int position) {
            T model = data.get(position);
            setupItem(holder, model, position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void setSelected(int position) {
            T model = data.get(position);
            if (menu.isMultiSelect) {
                boolean checked = !model.isChecked();
                model.setChecked(checked);
                if (!checked) {
                    selected.remove(model);
                } else {
                    selected.add(model);
                }
            } else {
                model.setChecked(true);
                for (T item : selected) {
                    item.setChecked(false);
                }
                selected.clear();
                selected.add(model);
            }
            updateTitleAndContent();
            if (menu.isAutoDismiss) {
                menu.dismiss(menu.isAnimated);
            }
        }

        public void updateTitleAndContent() {
            setupTitle(title, selected);
            setupContent(title, selected);
        }

        public void setData(@NonNull Collection<? extends T> items) {
            this.data.clear();
            this.selected.clear();
            this.data.addAll(items);
            for (T item : items) {
                if (item.isChecked()) {
                    this.selected.add(item);
                }
            }
            updateTitleAndContent();
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(OnItemClickListener<T> l) {
            listener = l;
        }

        public ViewHolder getTitle() {
            return title;
        }

        public ViewHolder<T> getContent() {
            return content;
        }

        public List<T> getData() {
            return data;
        }

        public List<T> getSelected() {
            return selected;
        }

        /**
         * 根据Model更新标题（DropdownMenu本身）
         *
         * @param holder 标题的ViewHolder
         * @param models Model
         */
        protected abstract void setupTitle(@NonNull ViewHolder<T> holder, List<T> models);

        /**
         * 根据Model更新内容（下拉列表中除了List以外的自定义组件）
         *
         * @param holder 内容的ViewHolder
         * @param models Model
         */
        protected void setupContent(@NonNull ViewHolder<T> holder, List<T> models) {
        }

        /**
         * 根据Model更新List元素
         *
         * @param holder   List元素的ViewHolder
         * @param model    Model
         * @param position 位置
         */
        protected void setupItem(@NonNull ViewHolder<T> holder, T model, int position) {
        }

    }

    public static class ViewHolder<T> extends RecyclerView.ViewHolder {

        private SparseArray<View> views = new SparseArray<>();

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void setOnClickListener(@Nullable OnClickListener l) {
            itemView.setOnClickListener(l);
        }

        public Context getContext() {
            return itemView.getContext();
        }

        @SuppressWarnings("unchecked")
        public <V extends View> V getView(int resId) {
            View view = views.get(resId);
            if (view == null) {
                view = itemView.findViewById(resId);
                if (view == null) {
                    return null;
                }
                views.put(resId, view);
            }
            return (V) view;
        }

    }

    private static class FixedLayoutManager extends LinearLayoutManager {

        private float maxCount;
        private float maxHeight;

        private FixedLayoutManager(Context context, float count, float height) {
            super(context);
            this.maxCount = count;
            this.maxHeight = height;
        }

        @Override
        public void onMeasure(@NonNull RecyclerView.Recycler recycler,
                              @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
            if (getChildCount() == 0) {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
                return;
            }
            View firstChildView = recycler.getViewForPosition(0);
            measureChild(firstChildView, widthSpec, heightSpec);
            int itemHeight = firstChildView.getMeasuredHeight();
            int height;
            if (maxCount > 0) {
                height = getChildCount() > maxCount ?
                        (int) (itemHeight * maxCount) : itemHeight * getChildCount();
            } else {
                height = Math.min(itemHeight * getChildCount(), (int) maxHeight);
            }
            setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), height);
        }
    }

    private static class DefaultAnimation implements DropdownAnimation {

        @Override
        public AnimatorSet showAnimator(ViewHolder title, View menu, View overlay) {
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator menuAnim = ObjectAnimator.ofFloat(menu, "translationY",
                    menu.getTranslationY(), 0);
            ObjectAnimator overlayAnim = ObjectAnimator.ofFloat(overlay, "alpha",
                    overlay.getAlpha(), 1f);
            set.play(menuAnim).with(overlayAnim);
            set.setDuration(250);
            set.setInterpolator(new DecelerateInterpolator());
            return set;
        }

        @Override
        public AnimatorSet dismissAnimator(ViewHolder title, View menu, View overlay) {
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator menuAnim = ObjectAnimator.ofFloat(menu, "translationY",
                    menu.getTranslationY(), -menu.getHeight());
            ObjectAnimator overlayAnim = ObjectAnimator.ofFloat(overlay, "alpha",
                    overlay.getAlpha(), 0);
            set.play(menuAnim).with(overlayAnim);
            set.setDuration(250);
            set.setInterpolator(new DecelerateInterpolator());
            return set;
        }
    }

    public static class Model {

        private boolean isChecked;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            this.isChecked = checked;
        }
    }

    public interface DropdownAnimation {

        /**
         * 菜单展示时的动画
         *
         * @param title   标题
         * @param menu    菜单
         * @param overlay 遮罩
         * @return 动画集
         */
        AnimatorSet showAnimator(ViewHolder title, View menu, View overlay);

        /**
         * 菜单消失时的动画
         *
         * @param title   标题
         * @param menu    菜单
         * @param overlay 遮罩
         * @return 动画集
         */
        AnimatorSet dismissAnimator(ViewHolder title, View menu, View overlay);
    }

    public interface OnItemClickListener<T extends Model> {

        /**
         * 菜单列表选择事件回调
         *
         * @param adapter  适配器
         * @param model    Model
         * @param position 位置
         */
        void onItemSelected(Adapter<T> adapter, T model, int position);
    }

}
