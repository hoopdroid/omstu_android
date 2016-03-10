package savindev.myuniversity.performance

import android.net.Uri

class PointModel(var idGroup: Int, var name: String?, var iD_PROGRESS_RAITNG_FILE: Int) {
    var isDeleted: Boolean = false

    var fileUri: Uri? = null
}
