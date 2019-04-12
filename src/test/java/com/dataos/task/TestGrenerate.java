package com.dataos.task;

import ch.ethz.ssh2.Connection;
import com.dataos.task.util.ShellCommand;

public class TestGrenerate {



	public static void main(String[] args) {

            long currentTimeMillis = System.currentTimeMillis();
            String ip = "10.10.1.199";
            String username = "jenkins";
            String password = "jenkins";
            String cmd = "cd /usr/local && pwd";
            Connection connection = ShellCommand.login(ip, username, password);
            String execmd = ShellCommand.execmd(connection, cmd);
            System.out.println(execmd);
            long currentTimeMillis1 = System.currentTimeMillis();
            System.out.println("ganymed-ssh2方式" + (currentTimeMillis1 - currentTimeMillis));

	}

}
