import java.io.*;

import java.net.*;

import java.util.concurrent.atomic.AtomicInteger;

public class CCS {

    private static AtomicInteger clientNum = new AtomicInteger(0);

    private static AtomicInteger operationNum= new AtomicInteger(0);

    private static AtomicInteger SumNum= new AtomicInteger(0);

    private static AtomicInteger SubNum= new AtomicInteger(0);

    private static AtomicInteger MulNum= new AtomicInteger(0);

    private static AtomicInteger DivNum= new AtomicInteger(0);

    private static AtomicInteger ErrNum= new AtomicInteger(0);

    private static AtomicInteger ResultSum= new AtomicInteger(0);

    private static AtomicInteger clientNumLast10sec= new AtomicInteger(0);

    private static AtomicInteger operationNumLast10sec= new AtomicInteger(0);

    private static AtomicInteger SumNumLast10sec= new AtomicInteger(0);

    private static AtomicInteger SubNumLast10sec= new AtomicInteger(0);

    private static AtomicInteger MulNumLast10sec= new AtomicInteger(0);

    private static AtomicInteger DivNumLast10sec= new AtomicInteger(0);

    private static AtomicInteger ErrNumLast10sec= new AtomicInteger(0);

    private static AtomicInteger ResultSumLast10sec= new AtomicInteger(0);

    public CCS(int port){

        new Thread(()->UDPreplier(port)).start();

        new Thread(()->TCPclientHandler(port)).start();

        new Thread(CCS::reporting).start();

    }

    private static void UDPreplier(int port)

    {

        byte[] toSend = "CCS FOUND".getBytes();

        try {

            DatagramSocket socket = new DatagramSocket(port);

            byte[] buffer = new byte[128];

            while (true) {

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                String msg = new String(packet.getData(),0, packet.getLength());

                if(msg.startsWith("CCS DISCOVER"))

                {

                    DatagramPacket packet1 = new DatagramPacket(toSend, toSend.length,packet.getAddress(), packet.getPort());

                    socket.send(packet1);

                }

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private static void TCPclientHandler(int port)

    {

        try {

            ServerSocket socket = new ServerSocket(port);

            while(true) {

                Socket client = socket.accept();

                clientNumLast10sec.incrementAndGet();

                new Thread(()->Work(client)).start();

            }

        } catch (IOException e) {

            //e.printStackTrace();

        }

    }

    private static void Work(Socket socket)

    {

        try {

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter output = new PrintWriter(socket.getOutputStream());

            String msg;

            while ((msg = input.readLine())!=null)

            {

                String[] strings = msg.split("\\s");

                if(strings.length!=3)

                {

                    ErrNumLast10sec.incrementAndGet();

                    output.println("ERROR");

                    output.flush();

                    continue;

                }

                int[] args = new int[2];

                try{

                    args[0] = Integer.parseInt(strings[1]);

                    args[1] = Integer.parseInt(strings[2]);

                }catch (NumberFormatException a)

                {

                    ErrNumLast10sec.incrementAndGet();

                    output.println("ERROR");

                    output.flush();

                    continue;

                }

                int outcome=0;

                switch (strings[0]) {

                    case "ADD":

                        outcome=args[0]+args[1];

                        SumNumLast10sec.incrementAndGet();

                        break;

                    case "SUB":

                        outcome=args[0]-args[1];

                        SubNumLast10sec.incrementAndGet();

                        break;

                    case "MUL":

                        outcome=args[0]*args[1];

                        MulNumLast10sec.incrementAndGet();

                        break;

                    case "DIV":

                        try{

                            outcome = args[0]/args[1];

                            DivNumLast10sec.incrementAndGet();

                        }catch (ArithmeticException a)

                        {

                            ErrNumLast10sec.incrementAndGet();

                            output.println("ERROR");

                            output.flush();

                            continue;

                        }

                        break;

                    default:

                        ErrNumLast10sec.incrementAndGet();

                        output.println("ERROR");

                        output.flush();

                        continue;

                }

                ResultSumLast10sec.addAndGet(outcome);

                operationNumLast10sec.incrementAndGet();

                output.println(outcome);

                output.flush();

                System.out.println(strings[0]+" "+strings[1]+" "+strings[2]+" = "+outcome);

            }

        } catch (IOException e) {

            return;

        }

    }

    private static void reporting(){

        while (true){

            try {

                Thread.sleep(10000);

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

            clientNum.addAndGet(clientNumLast10sec.get());

            operationNum.addAndGet(operationNumLast10sec.get());

            SumNum.addAndGet(SumNumLast10sec.get());

            SubNum.addAndGet(SubNumLast10sec.get());

            MulNum.addAndGet(MulNumLast10sec.get());

            DivNum.addAndGet(DivNumLast10sec.get());

            ErrNum.addAndGet(ErrNumLast10sec.get());

            ResultSum.addAndGet(ResultSumLast10sec.get());

            System.out.println("*---------statistics---------*");

            System.out.println("\t"+"\t"+"last 10 sec"+"\t"+"total");

            System.out.println("clients:"+"\t"+clientNumLast10sec.get()+"\t"+"\t"+clientNum.get());

            System.out.println("operations:"+"\t"+operationNumLast10sec.get()+"\t"+"\t"+operationNum.get());

            System.out.println("add ops:"+"\t"+SumNumLast10sec.get()+"\t"+"\t"+SumNum.get());

            System.out.println("sub ops:"+"\t"+SubNumLast10sec.get()+"\t"+"\t"+SubNum.get());

            System.out.println("mul ops:"+"\t"+MulNumLast10sec.get()+"\t"+"\t"+MulNum.get());

            System.out.println("div ops:"+"\t"+DivNumLast10sec.get()+"\t"+"\t"+DivNum.get());

            System.out.println("error num:"+"\t"+ErrNumLast10sec.get()+"\t"+"\t"+ErrNum.get());

            System.out.println("result sum:"+"\t"+ResultSumLast10sec.get()+"\t"+"\t"+ResultSum.get());

            System.out.println("*----------------------------*");

            clientNumLast10sec.set(0);

            operationNumLast10sec.set(0);

            SumNumLast10sec.set(0);

            SubNumLast10sec.set(0);

            MulNumLast10sec.set(0);

            DivNumLast10sec.set(0);

            ErrNumLast10sec.set(0);

            ResultSumLast10sec.set(0);

        }

    }

}

