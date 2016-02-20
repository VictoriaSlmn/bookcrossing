package victoriaslmn.bookcrossing

import android.support.v4.app.Fragment
import java.util.concurrent.atomic.AtomicInteger

class BaseFragment : Fragment {
    private object LastFragmentId : AtomicInteger(0){
        override fun toByte(): Byte {
            throw UnsupportedOperationException()
        }

        override fun toShort(): Short {
            throw UnsupportedOperationException()
        }

    }

    private val fragmentId: Int

    constructor() {
        fragmentId = LastFragmentId.incrementAndGet()
    }

    fun getFragmentName() : String {
        return "$fragmentId"
    }
}