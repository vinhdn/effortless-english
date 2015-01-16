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

    public Models getParent() {
        return parent;
    }

    public void setParent(Models parent) {
        this.parent = parent;
        this.image = parent.getImage();
    }

    private Models parent;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    private String link;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    private int image;

    public Models(){
        this.id = 0;
        this.description = "";
        this.name = "";
        this.idParMenu = 0;
        this.nameParMenu = "";
        this.type = TYPE_MODEL.NONE;
        this.image = 0;
        this.link = "";
        this.parent = null;
    }

    public Models(Models model){
        this.id = model.getId();
        this.description = model.getDescription();
        this.name = model.getName();
        this.idParMenu = model.getIdParMenu();
        this.nameParMenu = model.getNameParMenu();
        this.type = model.getType();
        this.image = model.getImage();
        this.link = model.getLink();
        this.parent = model.getParent();
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

