package magick;

/**
 * Corresponds to the ImageMagick enumerated type of the same name.
 *
 * @author Eric Yeo
 */
public interface PrimitiveType {

    int UndefinedPrimitive = 0;
    int PointPrimitive = 1;
    int LinePrimitive = 2;
    int RectanglePrimitive = 3;
    int RoundRectanglePrimitive = 4;
    int ArcPrimitive = 5;
    int EllipsePrimitive = 6;
    int CirclePrimitive = 7;
    int PolylinePrimitive = 8;
    int PolygonPrimitive = 9;
    int BezierPrimitive = 10;
    int ColorPrimitive = 11;
    int MattePrimitive = 12;
    int TextPrimitive = 13;
    int ImagePrimitive = 14;
    int PathPrimitive = 15;

}
