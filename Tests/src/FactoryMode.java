public class FactoryMode {
    public static void main(String[] args) {
        CarFactory AF = new AFactory();
        Car a = AF.fanhui();
        a.run();
        CarFactory BF = new BFactory();
        Car b = BF.fanhui();
        b.run();
    }
}

interface Car{
    public void run();
}

interface CarFactory{
    public Car fanhui();
}

class  CarA implements Car{
    public void run(){
        System.out.println("A Car");
    }
}

class AFactory implements CarFactory{
    public Car fanhui(){
        return new CarA();
    }
}

class CarB implements Car{
    public void run(){
        System.out.println("B Car");
    }
}

class BFactory implements CarFactory{
   public Car fanhui(){
       return new CarB();
   }
}