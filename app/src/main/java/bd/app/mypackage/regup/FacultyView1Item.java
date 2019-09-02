package bd.app.mypackage.regup;

/**
 * Created by imsajib02 on 23-May-19.
 */

public class FacultyView1Item {

    String intake, section, shift;

    public FacultyView1Item(String intake, String section, String shift)
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
