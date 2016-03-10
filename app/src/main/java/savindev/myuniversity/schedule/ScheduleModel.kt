package savindev.myuniversity.schedule

import android.os.Parcel
import android.os.Parcelable
import savindev.myuniversity.notes.NoteModel
import java.util.*

/**
 * Класс-объект для пары
 */

class ScheduleModel : Parcelable {

    var date: String? = null
        private set  //Дата проведения пары, напрямую из объекта
    var n: String? = null
        private set  //Номер пары можно получить по ее id из базы PAIRS
    var startTime: String? = null
        private set //Начало пары, из той же таблички
    var endTime: String? = null
        private set //Конец пары, из той же таблички
    var cellType: CellType? = null
    private var isCancelled: Boolean = false
    private var pairs: MutableList<Pair>? = null
    var notes: ArrayList<NoteModel>? = null


    constructor(n: String, startTime: String, endTime: String, date: String, isCancelled: Boolean, pairs: MutableList<Pair>, notes: ArrayList<NoteModel>) {
        this.n = n
        this.startTime = startTime
        this.endTime = endTime
        this.date = date
        this.cellType = CellType.PAIR
        this.pairs = pairs
        this.isCancelled = isCancelled
        this.notes = notes


    }

    constructor(cellType: CellType, value: String) {
        //конструктор с использованием типа ячейки, для расписания-сетки
        this.cellType = cellType
        this.date = value
    }


    fun addListItem(pair: List<Pair>) {
        pairs!!.addAll(pair)
    }


    fun getPairs(): List<Pair> {
        return pairs!!
    }

    private constructor(`in`: Parcel) {
        readFromParcel(`in`)
    }

    fun readFromParcel(`in`: Parcel) {

        date = `in`.readString()
        n = `in`.readString()
        startTime = `in`.readString()
        endTime = `in`.readString()
        isCancelled = `in`.readByte().toInt() != 0
        if (pairs == null)
            pairs = ArrayList<Pair>()
        if (notes == null)
            notes = ArrayList<NoteModel>()
        `in`.readTypedList(pairs, Pair.CREATOR)
        `in`.readTypedList(notes, NoteModel.CREATOR)
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(date)
        dest.writeString(n)
        dest.writeString(startTime)
        dest.writeString(endTime)
        dest.writeByte((if (isCancelled) 1 else 0).toByte())
        dest.writeTypedList(pairs)
        dest.writeTypedList(notes)
    }

    class Pair : Parcelable {
        var idSchedule: Int = 0
            private set //Напрямую из объекта в базе
        var idPair: Int = 0
            private set//Напрямую из объекта в базе
        var idGroup: Int = 0
            private set//Напрямую из объекта в базе
        var idTeacher: Int = 0
            private set//Напрямую из объекта в базе
        var idClassroom: Int = 0
            private set//Напрямую из объекта в базе
        var subgroup: Int = 0
            private set//Напрямую из объекта в базе
        var name: String? = null
            private set //Напрямую из объекта (DISCIPLINE_NAME)
        var teacher: String? = null //Из соответствующей таблички по id
        var group: String? = null
            private set //Из соответствующей таблички по id
        var classroom: String? = null
            private set //В формате корпус + "-" аудитория
        var type: String? = null
            private set//Напрямую из объекта в базе
        var isCancelled: Boolean = false
            private set //Напрямую из объекта в базе

        constructor(idSchedule: Int, idPair: Int, idGroup: Int, idTeacher: Int, idClassroom: Int,
                    subgroup: Int, name: String, teacher: String,
                    group: String, classroom: String, type: String, isCancelled: Boolean) {
            this.idSchedule = idSchedule
            this.idPair = idPair
            this.idGroup = idGroup
            this.idTeacher = idTeacher
            this.idClassroom = idClassroom
            this.subgroup = subgroup
            this.name = name
            this.teacher = teacher
            this.group = group
            this.classroom = classroom
            this.type = type
            this.isCancelled = isCancelled
        }

        protected constructor(`in`: Parcel) {
            idSchedule = `in`.readInt()
            idPair = `in`.readInt()
            idGroup = `in`.readInt()
            idTeacher = `in`.readInt()
            idClassroom = `in`.readInt()
            subgroup = `in`.readInt()
            name = `in`.readString()
            teacher = `in`.readString()
            group = `in`.readString()
            classroom = `in`.readString()
            type = `in`.readString()
            isCancelled = `in`.readByte().toInt() != 0
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {

            dest.writeInt(idSchedule)
            dest.writeInt(idPair)
            dest.writeInt(idGroup)
            dest.writeInt(idTeacher)
            dest.writeInt(idClassroom)
            dest.writeInt(subgroup)
            dest.writeString(name)
            dest.writeString(teacher)
            dest.writeString(group)
            dest.writeString(classroom)
            dest.writeString(type)
            dest.writeByte((if (isCancelled) 1 else 0).toByte())
        }

        companion object {

            val CREATOR: Parcelable.Creator<Pair> = object : Parcelable.Creator<Pair> {
                override fun createFromParcel(`in`: Parcel): Pair {
                    return Pair(`in`)
                }

                override fun newArray(size: Int): Array<Pair?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {

        val CREATOR: Parcelable.Creator<ScheduleModel> = object : Parcelable.Creator<ScheduleModel> {
            override fun createFromParcel(`in`: Parcel): ScheduleModel {
                return ScheduleModel(`in`)
            }

            override fun newArray(size: Int): Array<ScheduleModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}

