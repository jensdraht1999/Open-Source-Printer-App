package magick;

/**
 * Corresponds to the ImageMagick enumerated type of the same name.
 *
 * @author Eric Yeo
 */
public interface RenderingIntent {

    int UndefinedIntent = 0;
    int SaturationIntent = 1;
    int PerceptualIntent = 2;
    int AbsoluteIntent = 3;
    int RelativeIntent = 4;

}
