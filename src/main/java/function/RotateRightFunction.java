package function;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import plugin.SimplePlugin;

import java.util.Locale;
import java.util.ResourceBundle;

public class RotateRightFunction implements SimplePlugin{

    @Override
    public String getButtonText(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("MyResources", locale, this.getClass().getClassLoader());
        return bundle.getString("rotateRight");
    }

    @Override
    public boolean actionOnImage(Mat mat) {
        Core.transpose(mat, mat);
        Core.flip(mat, mat, 1);
        return true;
    }
}
