package cm.tbg.gpchat

import io.reactivex.disposables.CompositeDisposable

interface Base{
    val disposables:CompositeDisposable
}