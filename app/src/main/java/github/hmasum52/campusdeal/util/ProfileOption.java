package github.hmasum52.campusdeal.util;

public class ProfileOption {

    private  int icon;
    private String title;
    private String description;

    public ProfileOption(String title,int iconId, String description) {
        this.title = title;
        this.description = description;
        this.icon = iconId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIcon() {
        return icon;
    }
}
