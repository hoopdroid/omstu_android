package savindev.myuniversity.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import savindev.myuniversity.notes.NoteModel;
import savindev.myuniversity.serverTasks.Schedule;

/**
 * Класс-объект для пары
 */

public class ScheduleModel implements Parcelable {

    private String date;  //Дата проведения пары, напрямую из объекта
    private String n;  //Номер пары можно получить по ее id из базы PAIRS
    private String startTime; //Начало пары, из той же таблички
    private String endTime; //Конец пары, из той же таблички
    private CellType cellType;
    private boolean isCancelled;
    private List<Pair> pairs;
    private ArrayList<NoteModel> notes;



    public ScheduleModel(String n, String startTime, String endTime, String date, boolean isCancelled, List<Pair> pairs, ArrayList<NoteModel> notes) {
        this.n = n;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.cellType = CellType.PAIR;
        this.pairs = pairs;
        this.isCancelled = isCancelled;
        this.notes = notes;


    }

    public ScheduleModel(CellType cellType, String value) { //конструктор с использованием типа ячейки, для расписания-сетки
        this.cellType = cellType;
        this.date = value;
    }



    public void addListItem(List<Pair> pair) {
        pairs.addAll(pair);
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getN() {
        return n;
    }

    public String getDate() {
        return date;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public CellType getCellType() {
        return cellType;
    }


    public List<Pair> getPairs() {
        return pairs;
    }

    public ArrayList<NoteModel> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<NoteModel> notes) {
        this.notes = notes;
    }

    private ScheduleModel(Parcel in) { readFromParcel(in); }

    public void readFromParcel(Parcel in) {

        date = in.readString();
        n = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        isCancelled = in.readByte() != 0;
        if(pairs==null)
            pairs = new ArrayList<Pair>();
        if(notes==null)
            notes = new ArrayList<NoteModel>();
        in.readTypedList(pairs,Pair.CREATOR);
        in.readTypedList(notes,NoteModel.CREATOR);
    }

    public static final Creator<ScheduleModel> CREATOR = new Creator<ScheduleModel>() {
        @Override
        public ScheduleModel createFromParcel(Parcel in) {
            return new ScheduleModel(in);
        }

        @Override
        public ScheduleModel[] newArray(int size) {
            return new ScheduleModel[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(n);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeByte((byte) (isCancelled ? 1 : 0));
        dest.writeTypedList(pairs);
        dest.writeTypedList(notes);
    }

    public static class Pair implements Parcelable {
        private int idSchedule; //Напрямую из объекта в базе
        private int idPair;//Напрямую из объекта в базе
        private int idGroup;//Напрямую из объекта в базе
        private int idTeacher;//Напрямую из объекта в базе
        private int idClassroom;//Напрямую из объекта в базе
        private int subgroup;//Напрямую из объекта в базе
        private String name; //Напрямую из объекта (DISCIPLINE_NAME)
        private String teacher; //Из соответствующей таблички по id
        private String group; //Из соответствующей таблички по id
        private String classroom; //В формате корпус + "-" аудитория
        private String type;//Напрямую из объекта в базе
        private boolean isCancelled; //Напрямую из объекта в базе

        public Pair(int idSchedule, int idPair, int idGroup, int idTeacher, int idClassroom,
                    int subgroup, String name, String teacher,
                    String group, String classroom, String type, boolean isCancelled) {
            this.idSchedule = idSchedule;
            this.idPair = idPair;
            this.idGroup = idGroup;
            this.idTeacher = idTeacher;
            this.idClassroom = idClassroom;
            this.subgroup = subgroup;
            this.name = name;
            this.teacher = teacher;
            this.group = group;
            this.classroom = classroom;
            this.type = type;
            this.isCancelled = isCancelled;
        }

        protected Pair(Parcel in) {
            idSchedule = in.readInt();
            idPair = in.readInt();
            idGroup = in.readInt();
            idTeacher = in.readInt();
            idClassroom = in.readInt();
            subgroup = in.readInt();
            name = in.readString();
            teacher = in.readString();
            group = in.readString();
            classroom = in.readString();
            type = in.readString();
            isCancelled = in.readByte() != 0;
        }

        public static final Creator<Pair> CREATOR = new Creator<Pair>() {
            @Override
            public Pair createFromParcel(Parcel in) {
                return new Pair(in);
            }

            @Override
            public Pair[] newArray(int size) {
                return new Pair[size];
            }
        };

        public int getSubgroup() {
            return subgroup;
        }

        public int getIdClassroom() {
            return idClassroom;
        }

        public int getIdTeacher() {
            return idTeacher;
        }

        public int getIdGroup() {
            return idGroup;
        }

        public int getIdPair() {
            return idPair;
        }

        public int getIdSchedule() {
            return idSchedule;
        }

        public boolean isCancelled() {
            return isCancelled;
        }

        public String getType() {
            return type;
        }

        public String getClassroom() {
            return classroom;
        }

        public String getGroup() {
            return group;
        }

        public String getTeacher() {
            return teacher;
        }

        public void setTeacher(String teacher) {
            this.teacher = teacher;
        }

        public String getName() {
            return name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeInt(idSchedule);
            dest.writeInt(idPair);
            dest.writeInt(idGroup);
            dest.writeInt(idTeacher);
            dest.writeInt(idClassroom);
            dest.writeInt(subgroup);
            dest.writeString(name);
            dest.writeString(teacher);
            dest.writeString(group);
            dest.writeString(classroom);
            dest.writeString(type);
            dest.writeByte((byte) (isCancelled ? 1 : 0));
        }
    }
}

