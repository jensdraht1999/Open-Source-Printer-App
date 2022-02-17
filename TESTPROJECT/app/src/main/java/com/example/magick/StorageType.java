package magick;

/**
 * Used in MagickImage.createImage to specify the size 
 * of component.
 *
 * @author Eric Yeo
 */
public interface StorageType {

    int UndefinedPixel = 0;
    int CharPixel = 1;
    int DoublePixel = 2;
    int FloatPixel = 3;
    int IntegerPixel = 4;
    int LongPixel = 5;
    int QuantumPixel = 6;
    int ShortPixel = 7;

}
