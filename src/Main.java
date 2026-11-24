public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("give port as argument");
            return;
        }
        int port=0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException a)
        {
            System.out.println("provide number as port");
            return;
        }
        CCS a = new CCS(port);

    }
}