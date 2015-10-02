package ugia.io.androidbeautytreatment.viewcontroller.fragment;

import android.app.Fragment;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ugia.io.androidbeautytreatment.R;
import ugia.io.androidbeautytreatment.model.Contact;
import ugia.io.androidbeautytreatment.viewcontroller.adapter.HeaderContactListAdapter;

/**
 * Created by joseluisugia on 26/09/15.
 */
public class DynamicHeaderListFragment extends Fragment implements ClickableViewController {

    private Contact[] contacts;

    private float userAvatarResizeRatio = 0.5f;
    private View userAvatarContainer;
    private ImageView userAvatar;
    private ImageView imageStrokeVectorDrawable;
    private AnimatedVectorDrawable appearingImageStroke;
    private AnimatedVectorDrawable disappearingImageStroke;

    private View topHeader;
    private View topHeaderBackground;
    private View topHeaderDividerLine;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private HeaderContactListAdapter contactListAdapter;

    private int listHeaderHeight;
    private int userAvatarInitialDimension;

    private boolean userAvatarStrokeVisible;
    private boolean topHeaderBackgroundVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contacts = generateContacts();
        contactListAdapter = new HeaderContactListAdapter(contacts);

        initVectorDrawables();
        initDimensions();
    }

    private void initDimensions() {
        listHeaderHeight = (int) getResources().getDimension(R.dimen.recyclerview_dynamic_header_big_height);
        userAvatarInitialDimension = (int) getResources().getDimension(R.dimen
                .dynamic_header_list_user_avatar_size_big);
    }

    private void initVectorDrawables() {

        userAvatarStrokeVisible = true;
        topHeaderBackgroundVisible = false;

        appearingImageStroke = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable
                .circle_stroke_animated_show);
        disappearingImageStroke = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable
                .circle_stroke_animated_hide);
    }

    public static DynamicHeaderListFragment newInstance() {
        return new DynamicHeaderListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dynamic_header_list, container, false);
        bindViews(rootView);

        return rootView;
    }

    private void bindViews(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.fdh_recyclerview);
        initializeRecyclerView();

        userAvatarContainer = view.findViewById(R.id.fdh_avatar_stroke_group);
        userAvatar = (ImageView) view.findViewById(R.id.fdh_avatar_imageview);
        imageStrokeVectorDrawable = (ImageView) view.findViewById(R.id.fdh_avatar_stroke_imageview);
        imageStrokeVectorDrawable.setImageDrawable(disappearingImageStroke);

        topHeader = view.findViewById(R.id.fdh_top_header);
        topHeaderBackground = view.findViewById(R.id.fdh_top_header_background);
        topHeaderDividerLine = view.findViewById(R.id.fdh_top_header_divider_line);
    }

    private void initializeRecyclerView() {

        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(scrollListener);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(contactListAdapter);
    }

    private Contact[] generateContacts() {

        return new Contact[] {

                new Contact(0, "Pio Baroja", "123", true),
                new Contact(0, "Miguel Hernandez", "456", false),
                new Contact(0, "Miguel de Cervantes", "789", false),
                new Contact(0, "Juan Ramón Jimenez", "101", true),
                new Contact(0, "Miguel de Unamuno", "112", true),
                new Contact(0, "Antonio Machado", "175", false),
                new Contact(0, "Federico García Lorca", "623", false),
                new Contact(0, "Ramiro de Maeztu", "999", true),
                new Contact(0, "Ramón María del Valle-Inclán", "747", false),
                new Contact(0, "Ángel Ganivet", "622", false),
                new Contact(0, "Enrique de Mes", "278", true),
                new Contact(0, "Azorín", "345", false)
        };
    }

    private void updateViewsForHeaderSizeChanged(float progress) {

        // Scale group
        float scale = 1 - (progress * userAvatarResizeRatio);
        userAvatarContainer.setScaleY(scale);
        userAvatarContainer.setScaleX(scale);

        float translationY = (float) (-listHeaderHeight * 0.5 * progress);
        userAvatarContainer.setTranslationY(translationY);
        topHeader.setTranslationY(translationY);
        topHeaderDividerLine.setTranslationY(translationY);

        // Recalculate stroke
        if (userAvatarStrokeVisible) {
            if (progress > 0.1) {
                triggerAvatarStrokeTransformation(false);
            }
        } else {
            if (progress < 0.1) {
                triggerAvatarStrokeTransformation(true);
            }
        }

        // Final reveal effect
        if (topHeaderBackgroundVisible) {
            if (progress < 0.4) {
                topHeaderBackground.animate().scaleX(1).scaleY(1).setDuration(200);
                topHeaderBackgroundVisible = false;
            }
        } else {
            if (progress > 0.4) {
                topHeaderBackground.animate().scaleX(10).scaleY(10).setDuration(320);
                topHeaderBackgroundVisible = true;
            }
        }
    }

    private void triggerAvatarStrokeTransformation(boolean appear) {

        int lineScaleX = appear ? 1 : 10;
        topHeaderDividerLine.animate().scaleX(lineScaleX).setDuration(200);

        AnimatedVectorDrawable drawable = appear ? appearingImageStroke : disappearingImageStroke;
        imageStrokeVectorDrawable.setImageDrawable(drawable);
        drawable.start();

        userAvatarStrokeVisible = appear;
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            View header = layoutManager.findViewByPosition(0);
            float currentTopOffset;
            if (header == null) {
                currentTopOffset = listHeaderHeight;
            } else {
                currentTopOffset = -header.getTop();
            }

            updateViewsForHeaderSizeChanged(currentTopOffset / listHeaderHeight);
        }
    };

    @Override
    public void buttonClicked(View view) {

    }
}
