package savindev.myuniversity.notes;



public enum Priority {
    HIGH("Высокий приоритет"),
    MEDIUM("Средний приоритет"),
    LOW("Низкий приоритет");


    private String priority;

    Priority(String priority) {

        this.priority = priority;
    }

    public String getPriority(){
        return  this.priority;
    }

    public static Priority fromString(String priority)  {
        if (priority != null) {
            for (Priority b : Priority.values()) {
                if (priority.equalsIgnoreCase(b.priority)) {
                    return b;
                }
            }
        }
        return null;
    }


    public static Priority fromInt(int num)  {
        return values()[num];
    }

    @Override public String toString(){
        return priority;
    }

}
