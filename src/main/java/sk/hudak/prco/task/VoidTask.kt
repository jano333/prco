package sk.hudak.prco.task

import java.util.concurrent.Callable

//TODO zbavit sa exception
abstract class VoidTask : Callable<Unit> {

//    override fun call() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

    @Throws(Exception::class)
    override fun call() {
        doInTask()
    }

    @Throws(Exception::class)
    protected abstract fun doInTask()


}
