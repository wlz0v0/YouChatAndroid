package chat.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileUtil {
    //todo
    public static String ROOT_DIR;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    static {
        try {
            Process p = Runtime.getRuntime().exec("reg query \"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v " +
                    "personal");
            p.waitFor();

            InputStream in = p.getInputStream();
            byte[] b = new byte[in.available()];
            in.read(b);
            in.close();

            String result = new String(b);
            ROOT_DIR = result.split("\\s\\s+")[4] + "\\YouChat\\";
            File file = new File(ROOT_DIR);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String createTimestamp() {
        return dateFormat.format(new Date());
    }

    public static String createImageName() {
        return ROOT_DIR + "IMG_" + createTimestamp() + ".jpg";
    }

    public static String createVideoName() {
        return ROOT_DIR + "VIDEO_" + createTimestamp() + ".mp4";
    }

    public static String createAudioName() {
        return ROOT_DIR + "AUDIO_" + createTimestamp() + ".wav";
    }
}
