class ExampleException {
    static void main (String [] args) {
        staticPrintMessage(11)
        new ExampleException().printMessage(11)
        //No signature of method: static Example.printMessage() is applicable for argument types: (Integer) values: [11]
        printMessage(11)
    }

    static def staticPrintMessage(obj) {
        println(1)
    }

    def printMessage(obj) {
        println(2)
    }
}