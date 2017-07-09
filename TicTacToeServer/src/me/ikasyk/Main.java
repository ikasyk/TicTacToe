package me.ikasyk;

import me.ikasyk.play.service.PlayerService;
import me.ikasyk.play.PlayerSession;

import java.io.*;
import java.net.ServerSocket;

public class Main {
    static final int PORT = 1235;

    public static void main(String[] args) throws IOException {
        ServerSocket serverListener = new ServerSocket(PORT);
        System.out.println("Waiting for connection...");

        try {
            while (true) {
                PlayerService.Instance.addSession(new PlayerSession(serverListener.accept())).start();
            }
        } finally {
            serverListener.close();
        }
    }
}