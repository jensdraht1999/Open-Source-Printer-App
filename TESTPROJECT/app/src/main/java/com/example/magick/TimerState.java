package magick;

/**
 * Used in MagickImage.createImage to specify the size 
 * of component.
 *
 * @author Eric Yeo
 */
public interface TimerState {

    int UndefinedTimerState = 0;
    int StoppedTimerState = 1;
    int RunningTimerState = 2;

}
