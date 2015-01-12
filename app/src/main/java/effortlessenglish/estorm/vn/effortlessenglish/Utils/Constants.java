package effortlessenglish.estorm.vn.effortlessenglish.Utils;

import java.io.File;

import effortlessenglish.estorm.vn.effortlessenglish.Models.Danhmuc;
import effortlessenglish.estorm.vn.effortlessenglish.Models.Lession;

/**
 * Created by Vinh on 1/9/15.
 */
public class Constants {
    public static String BASE = "http://xemphimyoutube.com/";
    public static String DANHMUC = BASE + "danhmuc/";
    public static String MENU = BASE + "menu/";
    public static String SUBMENU = BASE  + "submenu/";

    public static String EXTRA_ID = "id";
    public static String EXTRA_NAME = "name";

    public static int statusPlayer = 0;
    public static Lession lessionPlaying;
    public static int indexLession = 0;
    public static Danhmuc selectedDanhmuc = new Danhmuc();

    public static final String FOLDER_ROOT_NAME = "EfforlessEnglish";
    public static final String FOLDER_ROOT_PATH = FileManager.EXTERNAL_PATH
            + File.separator + FOLDER_ROOT_NAME;
    public static final String FOLDER_LESSION = FOLDER_ROOT_PATH + File.separator + "lessions";

    /**
     * Converts milliseconds to hh:mm:ss format.
     */
    public static String convertMillisToMinsSecs(long milliseconds) {

        int secondsValue = (int) (milliseconds / 1000) % 60 ;
        int minutesValue = (int) ((milliseconds / (1000*60)) % 60);
        int hoursValue  = (int) ((milliseconds / (1000*60*60)) % 24);

        String seconds = "";
        String minutes = "";
        String hours = "";

        if (secondsValue < 10) {
            seconds = "0" + secondsValue;
        } else {
            seconds = "" + secondsValue;
        }

        if (minutesValue < 10) {
            minutes = "0" + minutesValue;
        } else {
            minutes = "" + minutesValue;
        }

        if (hoursValue < 10) {
            hours = "0" + hoursValue;
        } else {
            hours = "" + hoursValue;
        }

        String output = "";
        if (hoursValue!=0) {
            output = hours + ":" + minutes + ":" + seconds;
        } else {
            output = minutes + ":" + seconds;
        }

        return output;
    }
}
