package cuxchat.utils;

import cuxchat.ChatClient;
import cuxchat.utils.CuxLogger;
import java.util.Scanner;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;

public class Processor {

    private ProcessBuilder builder;
    public Process process;
    private BufferedReader reader;
    private BufferedWriter writer;
    private BufferedReader error;
    private Scanner scan;
    private ChatClient client;

    Thread T;  // thread pt. citirea stdout/stderr a procesului lansat
    Thread T2; // thread pt. scrierea in stdin a procesului lansat

    // pipe-urile standard ale procesului
    OutputStream stdin;
    InputStream stderr;
    InputStream stdout;

    // flag pt. a seta aplicatia
    static int isRunning = 0;

    // afiseaza pe ecran un mesaj
    private void print(String s) {
        System.out.print(s);
    }

    // trimite date procesului lansat [prin stdin]
    public int writeToStream(String message) {
        if (stdin == null) {
            return -1;
        }
        try {
            stdin.write((message + "\n").getBytes());
            stdin.flush();
            return 0;
        } catch (IOException ioe) {
            CuxLogger.getInstance().logException2(ioe);
            isRunning = 0;
            return -1;
        }
    }

    // citeste date de la procesul lansat [prin stdout/stderr]
    public void readStream() {
        T = new Thread("streamReader") {
            @Override
            public void run() {

                byte[] buffer;
                int x;

                try {
                    while (true) {
                        x = stdout.available();
                        if (x <= 0) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ie) {
                                CuxLogger.getInstance().logException2(ie);
                            }
                            continue;
                        } else {
                            buffer = new byte[x];
                            stdout.read(buffer);
                            print(new String(buffer));
                            client.out.out.writeUTF(new String(buffer));
                        }
                    }
                } catch (IOException ioe) {
                    CuxLogger.getInstance().logException2(ioe);
                }
            }
        };
        T.start();
    }

    // citeste date de la procesul lansat [prin stdout/stderr] - pentru compilarea programelor
    public void readStream2() {

        T = new Thread("streamReader") {
            @Override
            public void run() {

                byte[] buffer;
                int x;

                int sum = 0;

                try {
                    while (true) {
                        x = stdout.available();
                        if (x <= 0) {
                            sum += 100;
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ie) {
                                CuxLogger.getInstance().logException2(ie);
                            }

                            continue;
                        } else {
                            sum = 0;
                            buffer = new byte[x];
                            stdout.read(buffer);
                            print(new String(buffer));
                            client.out.out.writeUTF(new String(buffer));
                        }
                    }
                } catch (IOException ioe) {
                    CuxLogger.getInstance().logException2(ioe);
                }
            }
        };
        T.start();

    }

    // porneste controlul asupra procesului
    protected void runProcess() {
        T2 = new Thread("runProcess") {
            @Override
            public void run() {
                String t = "";
                while (true) {
                    t = scan.nextLine();
                    //Messenger.getInstance().popUpError(t, "you typed : ");
                    if (writeToStream(t) < 0) {
                        print("Process ended with return value : " + process.exitValue() + "\n");
                        //System.exit(0);
                        isRunning = 0;
                    }
                }
            }
        };
        readStream();
        T2.start();
    }

    // constructor - lanseaza in executie procesul si preia pipe-urile [std-in/out/err]
    public Processor(String cmd, ChatClient client) {
        isRunning = 1;
        this.client = client;
        scan = new Scanner(System.in);
        try {

            ArrayList<String> t = new ArrayList<String>();
            String[] cmds = cmd.split(" ");
            for (String c : cmds) {
                t.add(c);
            }

            builder = new ProcessBuilder(t);
            builder.redirectErrorStream(true); // concateneaza stdout cu stderr
            builder = builder.redirectErrorStream(true);

            process = builder.start();

            stdin = process.getOutputStream();
            stdout = process.getInputStream();
            //stderr = process.getErrorStream();
       
        } catch (IOException ioe) {
            // eroare in lansarea procesului
            CuxLogger.getInstance().logException2(ioe);
        }
    }

    /*
     public static void main(String[] args) {
     Processor p = new Processor("cmd.exe");
     //p.writeToStream("C:");
     //p.writeToStream("cd \"Documents and Settings\\mihai\\Desktop\\\"");
     //p.writeToStream("test.exe");
     // asta e alt proces :P
     //Processor p = new Processor("D:\\Programe\\Dev-Cpp\\bin\\gcc.exe \"C:\\Documents and Settings\\mihai\\Desktop\\test.c\"");
     p.runProcess();
     }
     */
}
