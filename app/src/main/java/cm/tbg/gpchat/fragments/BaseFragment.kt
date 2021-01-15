package cm.tbg.gpchat.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import cm.tbg.gpchat.Base
import cm.tbg.gpchat.interfaces.FragmentCallback
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment : Fragment(),Base {
    @JvmField
    var fragmentCallback: FragmentCallback? = null

    abstract fun showAds(): Boolean

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override val disposables = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentCallback = context as FragmentCallback
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener.  */
        }
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    open fun onQueryTextChange(newText: String?) {}
    open fun onSearchClose() {}
}