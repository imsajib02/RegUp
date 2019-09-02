package bd.app.mypackage.regup;

public class UpdateSupervisionItem {

    String intake, section, shift;

    public UpdateSupervisionItem(String intake, String section, String shift)
    {
        this.intake = intake;
        this.section = section;
        this.shift = shift;
    }

    public String getIntake()
    {
        return intake;
    }

    public String getSection()
    {
        return section;
    }

    public String getShift()
    {
        return shift;
    }
}
