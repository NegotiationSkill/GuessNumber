package Guess.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import Guess.Uilt.Jdbc;
import Guess.model.User;


public class Server {
	final  int PORT = 7777;  //端口
	ServerSocket serversocket;
	public static void main(String[] args) {
		
		Server server = new Server();
	}
	public Server() {
		start();
	}
	private  void start() {
		try {
			serversocket = new ServerSocket(PORT);
			System.out.println("服务器已经开启");
			while (true) {
				Socket socket = serversocket.accept();
				String userhost = socket.getInetAddress().getHostAddress();
				System.out.println(userhost + "已经连接");
				new Thread(new ClientThread(socket)).start();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}finally {
			try {
				serversocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	private class ClientThread implements Runnable{

		Socket socket = null;
		DataOutputStream netOut ;
		DataInputStream netIn;
		String username;
		public ClientThread(Socket  socket) {
			// TODO Auto-generated constructor stub
			this.socket = socket;
		}
		@Override
		public void run() {
			try {
				netOut = new DataOutputStream(socket.getOutputStream());
				netIn = new DataInputStream(socket.getInputStream());
				if(login(socket)) {
					netOut.writeUTF("登陆成功！");
					
					String opt = netIn.readUTF();
					if(opt.equals("1")) {
						gamebegin(socket);
					}
					if(opt.equals("3")) {
						rankList();
					}
				}else {
					netOut.writeUTF("登陆失败！");
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}finally {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		private void rankList() {
			ArrayList<User> rank = new ArrayList<User>(); 
			User user= null;
			int i=0;
			//连接数据库
			Jdbc jdbc = new Jdbc();
			Connection con = jdbc.getConnect();
			//查询
			PreparedStatement prepareStatement = null;
			ResultSet resultSet = null;
			String sql = "select * from userinf ORDER BY(grade)";
			try {
				 prepareStatement = con.prepareStatement(sql);
				 resultSet = prepareStatement.executeQuery();
				while(resultSet.next() && i< 10) {
					user = new User(resultSet.getString(1),resultSet.getInt(3));
					rank.add(user);
				}
				//传数据
				ObjectOutputStream oput = new ObjectOutputStream(socket.getOutputStream()); 
				oput.writeObject(rank);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					if(con != null) {
						con.close();	
					}
					if(resultSet != null) {
						resultSet.close();
					}
					if(prepareStatement != null) {
						prepareStatement.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		private void gamebegin(Socket socket) {
			Random ran = new Random();
			int number = ran.nextInt(100);
			System.out.println("随机数"+number);
			int cha,count = 1;
			try {
				netIn = new DataInputStream(socket.getInputStream());
				netOut = new DataOutputStream(socket.getOutputStream()); 
				while(true) {
					cha = netIn.readInt() - number;
					if(cha > 0) {
						netOut.writeUTF("大了");
					}
					if(cha < 0) {
						netOut.writeUTF("小了");
					}
					if(cha == 0) {
						netOut.writeUTF("猜中了，一共猜了"+count+"次");
						break;
					}
					count++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			refleshGrade( count);
		}
		private void refleshGrade( int count) {
			
			//连接数据库
			Jdbc jdbc = new Jdbc();
			Connection con = jdbc.getConnect();
			PreparedStatement prepareStatement = null;
			PreparedStatement prepareStatement2 = null;
			ResultSet re = null;
			int grade = -1;
			System.out.println(username);
			String sql = "select grade from userinf where username =?";
			try {
				 prepareStatement = con.prepareStatement(sql);
				 prepareStatement.setString(1,username );
				 re = prepareStatement.executeQuery();
				 while(re.next()) {
					grade =  re.getInt(1);
				 }
				 
				if(count < grade) {
					String sql1 = "update userinf set grade = ? where username =?";
					prepareStatement2 = con.prepareStatement(sql1);
					prepareStatement2.setInt(1, count);
					prepareStatement2.setString(2, username);
					prepareStatement2.executeUpdate();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					con.close();
					re.close();
					prepareStatement.close();
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//数据更新
			
		}
		private   boolean login(Socket socket) {
				User user = null;
				try {
					ObjectInputStream oIn = new ObjectInputStream(socket.getInputStream());
					 user = (User)oIn.readObject();
					 username = user.getUsername();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				 
				 
				//连接数据库
				Jdbc jdbc = new Jdbc();
				Connection con = jdbc.getConnect();
				PreparedStatement prepareStatement = null;
				ResultSet res = null;
				try {
					String sql = "select * from userinf  where username = ? and userpwd =?";
					prepareStatement = con.prepareStatement(sql);
					prepareStatement.setString(1, user.getUsername());
					prepareStatement.setString(2, user.getUserpwd());
					res = prepareStatement.executeQuery();
					if(res.next()) {
						return true;
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					try {
						con.close();
						res.close();
						prepareStatement.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return false;
		}
	}
	
}
