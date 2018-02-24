
package serverthread;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
public class ServerThread 
{
	public static void main(String[] args) throws IOException 
        {
                CallerThread c=new CallerThread();
		Thread thread2 = new Thread(c);
		thread2.start();
	}
}
class CallerThread implements Runnable
{
    public void run()
    {
        Thread th=new Thread(new MainThread());
        th.start();
        while(true)
        {
            if(Configure.running==false)
            {
                try
                {
                    Configure.running=true;
                    th=null;
                    th=new Thread(new MainThread());
                    th.start();
                }
                catch(Exception exp)
                {}
            }
        }
    }
}
class MainThread implements Runnable
{
    public void run()
    {
        try
        {
            ServerSocket ss = new ServerSocket(9998);
            Socket clientSocket = ss.accept();
            System.out.println("Recieved connection from "+clientSocket.getInetAddress()+" on port "+clientSocket.getPort());
            //create two threads to send and recieve from client
            RecieveFromClientThread recieve = new RecieveFromClientThread(clientSocket);
            Thread thread = new Thread(recieve);
            thread.start();
            SendToClientThread send = new SendToClientThread(clientSocket);
            Thread thread2 = new Thread(send);
            thread2.start();
        }
        catch(IOException e)
        {
            System.out.println("ERROR!!______________ main thread");
            Configure.running=false;
            System.out.println(e.getMessage());
        }
    }
}
class Configure
{
    static boolean running=true;
    static String message="Hello";
}
class RecieveFromClientThread implements Runnable
{
	Socket clientSocket=null;
	BufferedReader brBufferedReader = null;
	
	public RecieveFromClientThread(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
	}//end constructor
	public void run() 
        {
		try{
		brBufferedReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));		
		
		String messageString;
		while(Configure.running==true)
                {//assign message from client to messageString
                    messageString = brBufferedReader.readLine();
                    if(messageString.equals(null)||messageString==null)
                    {
                    break;
                    }
                    Configure.message=messageString;
                        System.out.println("");
			System.out.println("Client: " + messageString);//print the message from client
			System.out.println("");
                    
		}
	}
	catch(Exception ex)
        {
            System.out.println("ERROR!!______________ receive");
            Configure.running=false;
            System.out.println(ex.getMessage());
        }
    }
}//end class RecieveFromClientThread
class SendToClientThread implements Runnable
{
	PrintWriter pwPrintWriter;
	Socket clientSock = null;
	
	public SendToClientThread(Socket clientSock)
	{
		this.clientSock = clientSock;
	}
	public void run() {
		try{
		pwPrintWriter =new PrintWriter(new OutputStreamWriter(this.clientSock.getOutputStream()));//get outputstream
		
		while(Configure.running==true)
		{
			String msgToClientString = null;
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));//get userinput
			msgToClientString = input.readLine();//get message to send to client
			pwPrintWriter.println(msgToClientString);//send message to client with PrintWriter
			pwPrintWriter.flush();//flush the PrintWriter
			
		}//end while
		}
		catch(Exception ex)
                {
                    System.out.println("ERROR!!______________ send");
                    Configure.running=false;
                    System.out.println(ex.getMessage());
                }	
	}//end run
}//end class SendToClientThread
