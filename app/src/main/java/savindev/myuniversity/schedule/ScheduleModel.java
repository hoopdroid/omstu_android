package savindev.myuniversity.schedule;

/**
 * Класс-объект для пары
 */

public class ScheduleModel {

    private int idSchedule; //Напрямую из объекта в базе
    private int idPair;//Напрямую из объекта в базе
    private int idGroup;//Напрямую из объекта в базе
    private int idTeacher;//Напрямую из объекта в базе
    private int idClassroom;//Напрямую из объекта в базе
    private int subgroup;//Напрямую из объекта в базе
    private String date;  //Дата проведения пары, напрямую из объекта
    private String n;  //Номер пары можно получить по ее id из базы PAIRS
    private String startTime; //Начало пары, из той же таблички
    private String endTime; //Конец пары, из той же таблички
    private String name; //Напрямую из объекта (DISCIPLINE_NAME)
    private String teacher; //Из соответствующей таблички по id
    private String group; //Из соответствующей таблички по id
    private String classroom; //В формате корпус + "-" аудитория
    private String type;//Напрямую из объекта в базе
    private boolean isCancelled; //Напрямую из объекта в базе
    private CellType cellType;

    public ScheduleModel(int idSchedule, int idPair, int idGroup, int idTeacher, int idClassroom,
                         int subgroup, String n, String startTime, String endTime, String date, String name, String teacher,
                         String group, String classroom, String type, boolean isCancelled) {
        this.idSchedule = idSchedule;
        this.idPair = idPair;
        this.idGroup = idGroup;
        this.idTeacher = idTeacher;
        this.idClassroom = idClassroom;
        this.subgroup = subgroup;
        this.n = n;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.teacher = teacher;
        this.group = group;
        this.classroom = classroom;
        this.type = type;
        this.isCancelled = isCancelled;
        this.date = date;
        this.cellType = CellType.PAIR;
    }

    public ScheduleModel(CellType cellType, String value) { //конструктор с использованием типа ячейки, для расписания-сетки
        this.cellType = cellType;
        this.date = value;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public String getTipe() {
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

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getN() {
        return n;
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

    public String getDate() {
        return date;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public CellType getCellType() {
        return cellType;
    }
}

