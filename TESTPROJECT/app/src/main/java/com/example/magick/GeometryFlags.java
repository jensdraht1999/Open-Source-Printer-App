package magick;

public interface GeometryFlags {

    int NoValue = 0x0000;
    int PsiValue = 0x0001;
    int XValue = 0x0001;
    int XiValue = 0x0002;
    int YValue = 0x0002;
    int RhoValue = 0x0004;
    int WidthValue = 0x0004;
    int SigmaValue = 0x0008;
    int HeightValue = 0x0008;
    int ChiValue = 0x0010;
    int XiNegative = 0x0020;
    int XNegative = 0x0020;
    int PsiNegative = 0x0040;
    int YNegative = 0x0040;
    int ChiNegative = 0x0080;
    int PercentValue = 0x1000;
    int AspectValue = 0x2000;
    int LessValue = 0x4000;
    int GreaterValue = 0x8000;
    int MinimumValue = 0x10000;
    int AreaValue = 0x20000;
    int DecimalValue = 0x40000;
    int AllValues = 0x7fffffff;

}
