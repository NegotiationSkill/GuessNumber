package Guess.Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import Guess.model.User;
public class Client {
	Socket socket;
	ObjectOutputStream oput;
	ObjectInputStream oIn;
	DataInputStream netIn;
	DataOutputStream netOut;
	String operation = "";
	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}
	public Client() {
		
	}
	private void start() {
		try {
			socket = new Socket("127.0.0.1",7777);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//登录
		while(!login()) {
			System.out.println("请重新输入");
		}
		
		while(true) {
			if("5".equals(operation)) {
				break;
			}
			operation = option(socket);
			if("1".equals(operation)) {
				begin();
			}
			if("2".equals(operation)) {
							
			}
			if("3".equals(operation)) {
				rank();
			}
			if("4".equals(operation)) {
				
			}
		}
		System.out.println("断开连接");
		try {
				socket.close();
				netIn.close();
				netOut.close();
				oput.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void rank() {
		try {
			oIn = new ObjectInputStream(socket.getInputStream());
			ArrayList<User> rank=(ArrayList<User>) oIn.readObject();
			//遍历输出
			for(User i : rank) {
				System.out.println(i.getUsername()+"-------"+i.getGrade());
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void begin() {
		Scanner input  = new Scanner(System.in);
		int guessnumber;
		System.out.println("请您猜一个1到100之间的数");
		String res = null;
		do {
			guessnumber = input.nextInt();
			try {
				netOut = new DataOutputStream(socket.getOutputStream());
				netOut.writeInt(guessnumber);
				res = netIn.readUTF();
				System.out.println(res);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(res.equals("大了")||(res.equals("小了")));
	}
	private String option(Socket socket) {
		Scanner input  = new Scanner(System.in);
		System.out.println(); 
		System.out.println("1、开始游戏"); 
		System.out.println("2、关 卡"); 
		System.out.println("3、排 行 榜"); 
		System.out.println("4、关 于"); 
		System.out.println("5、退 出"); 
		String opt = input.nextLine();
		try {
			netOut = new DataOutputStream(socket.getOutputStream());
			netOut.writeUTF(opt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return opt;
	}
	private  boolean login() {
		//用户输入自己的信息
		Scanner input = new Scanner(System.in);
		System.out.println("请输入用户名");
		String username = input.nextLine();
		System.out.println("请输入用密码");
		String userpwd = input.nextLine();
		User user = new User(username,userpwd);
		//连接服务向服务器发送用户信息
		try {
			oput = new ObjectOutputStream(socket.getOutputStream());
			oput.writeObject(user);
			netIn = new DataInputStream(socket.getInputStream());
			String res = netIn.readUTF();
			System.out.println(res);
			if(res.equals("登陆成功！")) {
				return true;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		return false;
	}

}
