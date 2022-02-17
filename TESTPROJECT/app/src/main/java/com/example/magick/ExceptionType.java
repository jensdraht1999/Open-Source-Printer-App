package magick;

/**
 * Corresponds to the ImageMagick ExceptionType enumeration.
 *
 * @author Eric Yeo
 */
public interface ExceptionType {

	int UndefinedException = 0;
	int WarningException = 300;
	int ResourceLimitWarning = 300;
	int TypeWarning = 305;
	int OptionWarning = 310;
	int DelegateWarning = 315;
	int MissingDelegateWarning = 320;
	int CorruptImageWarning = 325;
	int FileOpenWarning = 330;
	int BlobWarning = 335;
	int StreamWarning = 340;
	int CacheWarning = 345;
	int CoderWarning = 350;
	int ModuleWarning = 355;
	int DrawWarning = 360;
	int ImageWarning = 365;
	int WandWarning = 370;
	int RandomWarning = 375;
	int XServerWarning = 380;
	int MonitorWarning = 385;
	int RegistryWarning = 390;
	int ConfigureWarning = 395;
	int ErrorException = 400;
	int ResourceLimitError = 400;
	int TypeError = 405;
	int OptionError = 410;
	int DelegateError = 415;
	int MissingDelegateError = 420;
	int CorruptImageError = 425;
	int FileOpenError = 430;
	int BlobError = 435;
	int StreamError = 440;
	int CacheError = 445;
	int CoderError = 450;
	int ModuleError = 455;
	int DrawError = 460;
	int ImageError = 465;
	int WandError = 470;
	int RandomError = 475;
	int XServerError = 480;
	int MonitorError = 485;
	int RegistryError = 490;
	int ConfigureError = 495;
	int FatalErrorException = 700;
	int ResourceLimitFatalError = 700;
	int TypeFatalError = 705;
	int OptionFatalError = 710;
	int DelegateFatalError = 715;
	int MissingDelegateFatalError = 720;
	int CorruptImageFatalError = 725;
	int FileOpenFatalError = 730;
	int BlobFatalError = 735;
	int StreamFatalError = 740;
	int CacheFatalError = 745;
	int CoderFatalError = 750;
	int ModuleFatalError = 755;
	int DrawFatalError = 760;
	int ImageFatalError = 765;
	int WandFatalError = 770;
	int RandomFatalError = 775;
	int XServerFatalError = 780;
	int MonitorFatalError = 785;
	int RegistryFatalError = 790;
	int ConfigureFatalError = 795;
}
