package kz.bmukhtar.quotas.domain.model.observable


open class DefaultTypedObservable<T : Any?> :
    MutableTypedObservable<T> {

    private val observers = mutableSetOf<TypedObserver<T>>()

    override fun notifyObservers(data: T) {
        val observersLocal: Set<TypedObserver<T>>

        synchronized(this) {
            /* We don't want the Observer doing callbacks into
             * arbitrary code while holding its own Monitor.
             * The code where we extract each Observable from
             * the Vector and store the state of the Observer
             * needs synchronization, but notifying observers
             * does not (should not).  The worst result of any
             * potential race-condition here is that:
             * 1) a newly-added Observer will miss a
             *   notification in progress
             * 2) a recently unregistered Observer will be
             *   wrongly notified when it doesn't care
             */
            observersLocal = observers.toSet()
        }
        observersLocal.forEach { observer ->
            observer.update(data)
        }
    }

    override fun addObserver(observer: TypedObserver<T>) {
        observers.add(observer)
    }

    override fun deleteObserver(observer: TypedObserver<T>) {
        observers.remove(observer)
    }
}