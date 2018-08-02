package function;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import plugin.SimplePlugin;

import java.util.Locale;
import java.util.ResourceBundle;

public class RotateLeftFunction implements SimplePlugin {
    @Override
    public String getButtonText(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("MyResources", locale, this.getClass().getClassLoader());
        return bundle.getString("rotateLeft");
    }

    @Override
    public boolean actionOnImage(Mat mat) {
        Core.transpose(mat, mat);
        Core.flip(mat, mat, 0);
        return true;
    }
}
