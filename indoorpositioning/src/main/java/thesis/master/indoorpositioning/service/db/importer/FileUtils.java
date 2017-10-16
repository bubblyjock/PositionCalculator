package thesis.master.indoorpositioning.service.db.importer;


import android.content.Context;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    static InputStream getFirstNonNull(InputStream... inputStreams) {
        for (InputStream inputStream : inputStreams) {
            if (inputStream != null) {
                return inputStream;
            }
        }
        return null;
    }

    static InputStream importFromAssets(String fileName, Context context) {
        try {
            return context.getAssets().open(fileName);
        } catch (IOException e) {
            return null;
        }
    }

    static InputStream importFromInternalStorage(String fileName, Context context) {
        try {
            return context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    static InputStream importFromExternalStorage(String fileName, Context context) {
        try {
            return new FileInputStream(context.getExternalFilesDir(null).getAbsolutePath() + "/" + fileName);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static boolean fileExists(String fileName, Context context) {
        assert StringUtils.isNotBlank(fileName);

        InputStream stream = getFirstNonNull(
                importFromAssets(fileName, context),
                importFromInternalStorage(fileName, context),
                importFromExternalStorage(fileName, context)
        );
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }
}
