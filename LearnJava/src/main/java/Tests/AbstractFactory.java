package Tests;

public class AbstractFactory {
    public static void main(String[] args)
    {
        Enterprise e1 = new E1();
        Enterprise e2 = new E2();
        e1.createDB();
        e2.createDB();
        e1.createToolKit();
        e2.createToolKit();
    }
}
interface DB
{}

interface Enterprise //企业
{
    ToolKit createToolKit();
    DB createDB();
}

interface ToolKit
{}


class E1 implements Enterprise
{
    public ToolKit createToolKit()
    {
        return new VS();
    }

    public DB createDB()
    {
        return new MySQL();
    }
}
class E2 implements Enterprise
{
    public ToolKit createToolKit()
    {
        return new WSS();
    }

    public DB createDB()
    {
        return new MariaDB();
    }
}

class MariaDB implements DB
{
    public MariaDB()
    {
        System.out.println("MariaDB");
    }
}

class MySQL implements DB
{
    public MySQL()
    {
        System.out.println("MySQL");
    }
}

class VS implements ToolKit
{
    public VS()
    {
        System.out.println("VS");
    }
}
class WSS implements ToolKit
{
    public WSS()
    {
        System.out.println("WSS");
    }
}


