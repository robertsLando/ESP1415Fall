package unipd.dei.ESP1415.falldetector;

import android.content.Context;

import java.util.List;


public interface IDynamicProvider {
    public int getCount ();
    public <T> List<T> getItems ();
    @Deprecated
    public void populate ();
    public void populate (Context context);
}