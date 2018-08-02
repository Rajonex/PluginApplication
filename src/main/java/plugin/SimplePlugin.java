package plugin;

import org.opencv.core.Mat;

import java.util.Locale;

public interface SimplePlugin {

    public String getButtonText(Locale locale);

    public boolean actionOnImage(Mat mat);
}
