package bd.app.mypackage.regup;


public class BloodVaultItem {

    String name, contact, group;

    public BloodVaultItem(String name, String contact, String group)
    {
        this.name = name;
        this.contact = contact;
        this.group = group;
    }

    public String getName()
    {
        return name;
    }

    public String getContact()
    {
        return contact;
    }

    public String getGroup()
    {
        return group;
    }

}
