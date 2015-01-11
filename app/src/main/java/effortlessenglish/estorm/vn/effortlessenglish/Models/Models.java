package effortlessenglish.estorm.vn.effortlessenglish.Models;

/**
 * Created by Vinh on 1/9/15.
 */
public class Models{

    private String name;
    private String description;
    private int idParMenu;
    private String nameParMenu;
    private TYPE_MODEL type;
    private int likes;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;

    public Models(){
        this.id = 0;
        this.description = "";
        this.name = "";
        this.idParMenu = 0;
        this.nameParMenu = "";
        this.type = TYPE_MODEL.NONE;
        this.image = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIdParMenu() {
        return idParMenu;
    }

    public void setIdParMenu(int idParMenu) {
        this.idParMenu = idParMenu;
    }

    public String getNameParMenu() {
        return nameParMenu;
    }

    public void setNameParMenu(String nameParMenu) {
        this.nameParMenu = nameParMenu;
    }

    public TYPE_MODEL getType() {
        return type;
    }

    public void setType(TYPE_MODEL type) {
        this.type = type;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public enum TYPE_MODEL{
        NONE(-1),
        DANHMUC(0),
        MENU(1),
        SUBMENU(2),
        LESSION(3);

        private int value;

        TYPE_MODEL(int value) {
            this.value = value;
        }

        public int getValue(){
            return this.value;
        }
    }
}

