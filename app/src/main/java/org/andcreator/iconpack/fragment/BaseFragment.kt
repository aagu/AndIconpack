package org.andcreator.iconpack.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {

    var isDestroyed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDestroyed = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isDestroyed = false
    }
}