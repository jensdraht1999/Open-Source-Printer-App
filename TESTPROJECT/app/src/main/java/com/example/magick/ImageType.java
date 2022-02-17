package magick;

/**
 * Corresponds to the ImageMagick enumerated type of the same name.
 *
 * @author Eric Yeo
 */
public interface ImageType {

    int UndefinedType = 0;
    int BilevelType = 1;
    int GrayscaleType = 2;
    int GrayscaleMatteType = 3;
    int PaletteType = 4;
    int PaletteMatteType = 5;
    int TrueColorType = 6;
    int TrueColorMatteType = 7;
    int ColorSeparationType = 8;
    int ColorSeparationMatteType = 9;
    int OptimizeType = 10;

}
