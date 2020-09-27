package utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LinuxClient {
    private String ip;
    private int port;
    private String user;
    private String password;
    private Connection connection;

    public LinuxClient() {
    }

    public LinuxClient(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
        initConnection();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private void initConnection() {
        try {

            this.connection = new Connection(ip, port);
            this.connection.connect();
            boolean isAuthenticated = connection.authenticateWithPassword(user, password);
            if (isAuthenticated == false) {
                System.out.println("验证失败");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void commitAndPrint(String command) {
        try {
            System.out.println("----------------------------------------------------------------------------------------------------------");
            System.out.println(String.format("正在执行: %s \n", command));

            Session session = this.connection.openSession();

            session.execCommand(command);

            BufferedReader bufferedReader;
            {
                InputStream inputStream = session.getStdout();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
            }

            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) break;
                System.out.println("    " + line);
            }
            System.out.println("执行完毕 \n");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void commitAndPrint(List<String> commandList) {
        for (String command : commandList) {
            this.commitAndPrint(command);
        }
    }

    public List<String> commit(String command) {
        try {
            System.out.println("----------------------------------------------------------------------------------------------------------");
            System.out.println(String.format("正在执行: %s \n", command));

            Session session;
            {
                session = this.connection.openSession();
                session.execCommand(command);
            }

            BufferedReader bufferedReader;
            {
                InputStream inputStream = session.getStdout();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
            }

            List<String> resultList;
            {
                resultList = new ArrayList<String>();
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) break;
                    resultList.add(line);
                }
                System.out.println("执行完毕 \n");
            }

            return resultList;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        this.connection.close();
    }

}
