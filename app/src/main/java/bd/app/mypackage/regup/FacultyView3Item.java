package bd.app.mypackage.regup;

/**
 * Created by imsajib02 on 25-May-19.
 */

public class FacultyView3Item {

    String code, name, credit, type;

    public FacultyView3Item(String code, String name, String credit, String type)
    {
        this.code = code;
        this.name = name;
        this.credit = credit;
        this.type = type;
    }

    public String getCode()
    {
        return code;
    }

    public String getName()
    {
        return name;
    }

    public String getCredit()
    {
        return credit;
    }

    public String getType()
    {
        return type;
    }
}
