package savindev.myuniversity.schedule;

import java.util.ArrayList;
import java.util.List;

import savindev.myuniversity.notes.NoteModel;

/**
 * Класс-объект для пары
 */

public class ScheduleModel {

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

    public static class Pair {
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
    }
}

