package magick;

/**
 * Corresponds to the ImageMagick enumerated type of the same name.
 *
 * @author Eric Yeo
 */
public interface InterlaceType {

    int UndefinedInterlace = 0;
    int NoInterlace = 1;
    int LineInterlace = 2;
    int PlaneInterlace = 3;
    int PartitionInterlace = 4;

}
