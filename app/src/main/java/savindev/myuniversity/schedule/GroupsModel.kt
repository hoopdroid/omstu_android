package savindev.myuniversity.schedule

/**
 * Класс-объект для используемых групп
 */

class GroupsModel {

    var id: Int = 0
    var isGroup: Boolean = false
    var name: String? = null
    var lastRefresh: String? = null
    var isFileSchedule: Boolean = false
    var isSelected: Boolean = false

    constructor(name: String, id: Int, isGroup: Boolean, lastRefresh: String) {
        this.name = name
        this.id = id
        this.isGroup = isGroup
        this.lastRefresh = lastRefresh
    }

    constructor(name: String, id: Int, isFileSchedule: Boolean, isGroup: Boolean) {
        this.name = name
        this.id = id
        this.isFileSchedule = isFileSchedule
        this.isGroup = isGroup
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as GroupsModel?
        return id == that!!.id && isGroup == that.isGroup && isSelected == that.isSelected && name == that.name && lastRefresh == that.lastRefresh
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + if (isGroup) 1 else 0
        result = 31 * result + name!!.hashCode()
        result = 31 * result + lastRefresh!!.hashCode()
        result = 31 * result + if (isSelected) 1 else 0
        return result
    }
}
