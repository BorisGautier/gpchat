package cm.tbg.gpchat.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import cm.tbg.gpchat.fragments.ImageViewFragment;
import cm.tbg.gpchat.fragments.VideoViewFragment;
import cm.tbg.gpchat.model.realms.Message;

import java.util.List;



public class FullScreenAdapter extends FragmentStatePagerAdapter {
    //media list items
    private List<Message> items;
    private Context context;
    ImageViewFragment imageViewFragment;
    VideoViewFragment videoViewFragment;
    private int mStartingPosition;

    public FullScreenAdapter(FragmentManager fm, Context context, List<Message> items, int mStartingPosition) {
        super(fm);
        this.context = context;
        this.items = items;
        this.mStartingPosition = mStartingPosition;
    }

    @Override
    public int getCount() {
        return items.size();
    }


    @Override
    public Fragment getItem(int position) {
        Message message = items.get(position);

        //if this item is a Video create VideoFragment
        if (message.isVideo())
            return VideoViewFragment.create(context, items.get(position));

        //otherwise create ImageView Fragment
        return ImageViewFragment.create(context, items.get(position), position, mStartingPosition);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (items.get(position).isVideo()) {
            videoViewFragment = (VideoViewFragment) object;
        } else {
            imageViewFragment = (ImageViewFragment) object;
        }
    }

    public View getImage() {
        if (videoViewFragment != null) {
            return null;
        }
        return imageViewFragment.getImageView();

    }


}
