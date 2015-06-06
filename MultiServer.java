import java.awt.*;
import java.io.*; //  ������� �Ͼ��.
import java.net.*; //  ��Ʈ��ũ ���α׷�.
import java.util.*; //  ArrayList ���(Ŭ���̾�Ʈ�� ��� ����)
import javax.swing.*;
import javax.swing.border.BevelBorder;

public class MultiServer extends JFrame {
	
	private ArrayList<MultiServerThread> list;
	private Socket socket;
	JTextArea ta;
	JTextField tf;
	JTextField iptf;
	public static void main(String[] args) {
		new MultiServer();
	}
	
	public MultiServer() {
		// ȭ�� ������ �ڵ�
		setTitle("ä�� ����");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ImageIcon project = new ImageIcon("null");//�̹��� �ҷ������� �����ؼ� �׳� ���

		JButton ta1 = new JButton(project);
		JButton join = new JButton("����"); 
		
		ta1.setBorder(new BevelBorder(BevelBorder.RAISED)); // ����

		add(ta1, BorderLayout.NORTH);
		add(join);

		
		ta = new JTextArea("KAU Chat by Lee&Seo\n");
		ta.setBackground(Color.WHITE);
		ta.setFont(new Font("����ü", Font.BOLD, 15));
		
		add(new JScrollPane(ta));
		tf = new JTextField();
		tf.setEditable(false);
		add(tf, BorderLayout.SOUTH);

		setBounds(500, 0, 300, 400);
		setVisible(true);
		
		// ä�� ���� �ڵ�
		list = new ArrayList<MultiServerThread>();

		try {
			ServerSocket serverSocket = new ServerSocket(5000);
			MultiServerThread mst = null;// �� ����� ����� ä�� ��ü
			boolean isStop = false; 
			tf.setText("���� ���� �������Դϴ�.\n");
			while (!isStop) {
				socket = serverSocket.accept();// Ŭ���̾�Ʈ�� ���� ����
				tf.setText("�����");
				mst = new MultiServerThread();// ä�� ��ü ����
				list.add(mst);// ArrayList�� ä�� ��ü �ϳ� ��´�.
				mst.start();// ������ ����
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	// ���⼭���� �̳� Ŭ����
	class MultiServerThread extends Thread {
		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		@Override
		public void run() {
			boolean isStop = false; // flag value(��� ��)
			try {
				tf.setText("���� ����� �� : " + list.size());
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
				String message = null; // ä�� ������ ������ ����
				while (!isStop) {
					message = (String) ois.readObject();// Ŭ���̾�Ʈ �Է� �ޱ�
					String[] str = message.split("#");// �̸�#����
					if (str[1].equals("exit")) { // �̸�#exit, ����
						broadCasting(message);// ��� ����ڿ��� ���� ����
						isStop = true; // ����
					} else {
						broadCasting(message);// ��� ����ڿ��� ä�� ���� ����
					}// else
				}// while
				list.remove(this);//�ش�Ŭ���̾�Ʈ ����

	ta.append(socket.getInetAddress() + " IP �ּ��� ����ڲ��� �����ϼ̽��ϴ�.\n");
				tf.setText("���� ����� �� : " + list.size());
			} catch (Exception e) {
				list.remove(this);

	ta.append(socket.getInetAddress() + " IP �ּ��� ����ڲ��� ������ �����ϼ̽��ϴ�.\n");
				tf.setText("���� ����� �� : " + list.size());
			}
		}

		public void broadCasting(String message) {// ��ο��� ����
			for (MultiServerThread ct : list) {
				ct.send(message);
			}
		}

		public void send(String message) { // �� ����ڿ��� ����
			try {
				oos.writeObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}// �̳�Ŭ����
}