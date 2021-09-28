package de.mcstangl.projectplanner.enums;

public enum DefaultMilestone {

    TEXTERSTELLUNG("Texterstellung"),
    REDAKTIONSFREIGABE("Redaktionsfreigabe"),
    KUNDENKORREKTUR("Kundenkorrektur"),
    MOTIONGRAFIK("Vergabe an Motion Grafik"),
    GRAFIK("Grafik"),
    ABNAHME("Abnahme"),
    INHOUSE("In House"),
    EINSPIELUNG("Einspielung"),
    HAUSINTERNERPROZESS("hausinterner Prozess");

    public final String title;

    DefaultMilestone(String title) {
        this.title = title;
    }

    public int getDueTime() {
        return switch (this) {
            case TEXTERSTELLUNG, KUNDENKORREKTUR -> 10;
            case REDAKTIONSFREIGABE, ABNAHME -> 2;
            case MOTIONGRAFIK, INHOUSE, EINSPIELUNG, HAUSINTERNERPROZESS -> 1;
            case GRAFIK -> 4;
        };
    }
}