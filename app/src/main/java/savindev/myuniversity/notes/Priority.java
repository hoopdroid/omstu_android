package savindev.myuniversity.notes;



public enum Priority {
    HIGH("Высокий приоритет"),
    MEDIUM("Средний приоритет"),
    LOW("Низкий приоритет");

    private static final String[] names = {"Высокий приоритет",
            "Средний приоритет","Низкий приоритет"};
    private final String priority;

    Priority(String priority) {
        this.priority = priority;
    }

    public static Priority fromString(String priorityColor)  {
        return valueOf(priorityColor);
    }

    public static Priority fromInt(int num)  {
        return fromString(names[num]);
    }

    @Override public String toString(){
        return priority;
    }

}
