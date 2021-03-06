package org.educationalProject.surfacePathfinder.visualization;

import org.educationalProject.surfacePathfinder.Point;

/**
 * Created by merlin on 17.05.17.
 */
public class ColorLevels {
    public static Point getColor1(double level){
        if (level < 0.0833)
            return new Point(0.482353, 0.768627, 0.549020);
        if (level < 0.1667)
            return new Point(0.694118, 0.882353, 0.592157);
        if (level < 0.2500)
            return new Point(0.909804, 0.984314, 0.733333);
        if (level < 0.3333)
            return new Point(0.980392, 0.956863, 0.694118);
        if (level < 0.4167)
            return new Point(0.992157, 0.894118, 0.572549);
        if (level < 0.5000)
            return new Point(0.976471, 0.854902, 0.474510);
        if (level < 0.5833)
            return new Point(0.984314, 0.752941, 0.388235);
        if (level < 0.6667)
            return new Point(0.992157, 0.694118, 0.364706);
        if (level < 0.7500)
            return new Point(0.972549, 0.596078, 0.298039);
        if (level < 0.8333)
            return new Point(0.835294, 0.505882, 0.266667);
        if (level < 0.9167)
            return new Point(0.823529, 0.411765, 0.219608);
        return new Point(0.803922, 0.333333, 0.200000);
    }
    public static Point getColor(double level){
        if (level > 0.9375)
            return new Point(1.000000, 0.635294, 0.282353);
        if (level > 0.8750)
            return new Point(1.000000, 0.862745, 0.498039);
        if (level > 0.8125)
            return new Point(1.000000, 0.968627, 0.619608);
        if (level > 0.7500)
            return new Point(0.992157, 0.964706, 0.631373);
        if (level > 0.6875)
            return new Point(0.960784, 0.992157, 0.827451);
        if (level > 0.6250)
            return new Point(0.870588, 1.000000, 0.662745);
        if (level > 0.5625)
            return new Point(0.792157, 0.992157, 0.592157);
        if (level > 0.5000)
            return new Point(0.635294, 0.803922, 0.478431);
        if (level > 0.4375)
            return new Point(0.784314, 1.000000, 0.976471);
        if (level > 0.3750)
            return new Point(0.658824, 1.000000, 0.988235);
        if (level > 0.3125)
            return new Point(0.529412, 0.988235, 1.000000);
        if (level > 0.2500)
            return new Point(0.392157, 0.960784, 0.996078);
        if (level > 0.1875)
            return new Point(0.435294, 0.882353, 1.000000);
        if (level > 0.1250)
            return new Point(0.317647, 0.768627, 0.988235);
        if (level > 0.0625)
            return new Point(0.286275, 0.643137, 0.980392);
        return new Point(0.192157, 0.513725, 0.984314);
    }
}
