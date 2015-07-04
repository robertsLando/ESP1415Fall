package unipd.dei.ESP1415.falldetector.preferences;

import java.util.List;

import android.content.Context;


public interface IDynamicProvider {
    public int getCount ();
    public <T> List<T> getItems ();
    @Deprecated
    public void populate ();
    public void populate (Context context);
}