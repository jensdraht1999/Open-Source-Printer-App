package magick;

/**
 * Corresponds to the ImageMagick enumerated type of the same name.
 *
 * @author Eric Yeo
 */
public interface GravityType {

    int UndefinedGravity = 0;
    int ForgetGravity = 0;
    int NorthWestGravity = 1;
    int NorthGravity = 2;
    int NorthEastGravity = 3;
    int WestGravity = 4;
    int CenterGravity = 5;
    int EastGravity = 6;
    int SouthWestGravity = 7;
    int SouthGravity = 8;
    int SouthEastGravity = 9;
    int StaticGravity = 10;

}
