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
		//��¼
		while(!login()) {
			System.out.println("����������");
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
		System.out.println("�Ͽ�����");
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
			//�������
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
		System.out.println("������һ��1��100֮�����");
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
		}while(res.equals("����")||(res.equals("С��")));
	}
	private String option(Socket socket) {
		Scanner input  = new Scanner(System.in);
		System.out.println(); 
		System.out.println("1����ʼ��Ϸ"); 
		System.out.println("2���� ��"); 
		System.out.println("3���� �� ��"); 
		System.out.println("4���� ��"); 
		System.out.println("5���� ��"); 
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
		//�û������Լ�����Ϣ
		Scanner input = new Scanner(System.in);
		System.out.println("�������û���");
		String username = input.nextLine();
		System.out.println("������������");
		String userpwd = input.nextLine();
		User user = new User(username,userpwd);
		//���ӷ���������������û���Ϣ
		try {
			oput = new ObjectOutputStream(socket.getOutputStream());
			oput.writeObject(user);
			netIn = new DataInputStream(socket.getInputStream());
			String res = netIn.readUTF();
			System.out.println(res);
			if(res.equals("��½�ɹ���")) {
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
