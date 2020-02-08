package org.andcreator.iconpack.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * 懒加载实例化一个全局的主线程Handler，用于线程同步
 */
val mainHandler: Handler by lazy {
    Handler(Looper.getMainLooper())
}

/**
 * 懒加载实例化一个基于缓存的线程池
 */
private val threadPool: Executor by lazy {
    Executors.newCachedThreadPool()
}

/**
 * 开启异步任务的方法，error：这是发生异常时的回调，设置了默认值为null，可以省略
 * run：这是子线程的回调函数，它会在子线程执行
 */
fun <T> T.doAsyncTask(error: ((Throwable) -> Unit)? = null,
            run: T.() -> Unit) {
    threadPool.execute(SimpleRunnable(this, error, run))
}

/**
 * 扩展一个Runnable的方法，为它提供一个叫onUI的扩展方法，传入回调会被主线程Handler通过post执行
 */
inline fun <reified T> T.onUI(crossinline run: T.() -> Unit) {
    mainHandler.post { run.invoke(this) }
}

/**
 * 简单的包装Runnable，只是做了简单的try，然后把异常回调回去
 */
private class SimpleRunnable<T>(private val target: T,
                                private val error: ((Throwable) -> Unit)?,
                                private val run: T.() -> Unit): Runnable {
    override fun run() {
        try {
            run.invoke(target)
        } catch (e: Throwable) {
            error?.invoke(e)
        }
    }

}