package com.aggeplugins.lib.EchoClient;

import com.aggeplugins.lib.EchoClient.*;
import com.aggeplugins.MessageBus.*;

/*
 * @note Basic steps to an in/out server (no matter complexity):
 * 
 *  1. Open a socket.
 *  2. Open an input stream and output stream to the socket.
 *  3. Read from and write to the stream according to the server's protocol.
 *  4. Close the streams.
 *  5. Close the socket.
 *
 * Only step 3 differs from client to client, depending on the server. The other 
 * steps remain largely the same.
 * @link https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
 */

public class EchoClient {
    public EchoClient(String hostname, int port)
    {
        this.hostname = hostname;
        this.port = port;

        //this.init();
    }

    //private void init()
    //{
    //    // try-with-resources will close resources in the reverse order they
    //    // were created. Desired because streams connected to the socket should 
    //    // be closed before the socket itself is closed.
    //    try (
    //        // xxx keep the socket alive?
    //        // create new socket object
    //        Socket echoSocket = new Socket(hostname, port);

    //        /* @note Uses Writer/Reader to be able to write unicode characters
    //         * over the socket. */
    //        // get the socket's output stream and open a PrintWriter
    //        PrintWriter out =
    //            new PrintWriter(echoSocket.getOutputStream(), true);
    //        // get the socket's input stream and open a BufferedReader
    //        BufferedReader in =
    //            new BufferedReader(
    //                new InputStreamReader(echoSocket.getInputStream()));

    //        // open a stdin stream to read from
    //        BufferedReader stdin =
    //            new BufferedReader(
    //                new InputStreamReader(System.in))
    //    ) {
    //        // xxx Your resource initialization goes here
    //    } catch (IOException e) {
    //        // xxx Handle exception
    //        e.printStackTrace();
    //    }
    //}

    //private void run()
    //{
    //    String userInput;
    //    // read from stdin one line at a time, then write the line to the socket
    //    while ((userInput == stdin.readLine()) != null) {
    //        out.println(userInput);
    //        System.out.println("echo: " + in.readLine());
    //    }
    //}

    public int getPort()
    {
        return port;
    }

    public String getHostname()
    {
        return hostname;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    // Hostname must be fully qualified.
    private String hostname;

    private int port;
}
