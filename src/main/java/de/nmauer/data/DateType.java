package de.nmauer.data;

public enum DateType {


    WORKING_DAY("Arbeitstag"), HOLIDAY("Feiertag"), VACATION("Urlaub"), DISABLED("Arbeitsunfähig");
    private String title;
    DateType(String title){
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public static DateType getByTitle(String title) {
        for (DateType dateType : DateType.values()) {
            if (dateType.getTitle().equals(title)) {
                return dateType;
            }
        }
        return null;
    }
    @Override
    public String toString() {
        return title;
    }
}
