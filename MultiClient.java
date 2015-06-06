import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class MultiClient implements ActionListener, ItemListener, Runnable {
	// ��� ���� ����
	private Socket socket; // ����
	private ObjectInputStream ois; // �Է�
	private ObjectOutputStream oos; // ���
	private JFrame jframe; // ������â
	private JTextField jtf; // ä�� �Է¶�
	private JTextArea jta; // ä�� ���� �����ִ� ��ü
	private JLabel jlb1, jlb2; // ��
	private JPanel jp1, jp2; // �ǳ�
	private String ip; // IP �ּҸ� ������ ����
	private static String id; // �г���(��ȭ��) ������ ����
	private JButton jbtn; // ���� ��ư �غ�
	private JPanel style; // ��Ʈ ����
	private JRadioButton Plain;
	private JRadioButton Bold;
	private ButtonGroup bg;
	private JTextField name;

	public MultiClient(String argIp, String argId) {
		ip = argIp; // IP �ּ�
		jframe = new JFrame("KAU-Chat");
		// �Ʒ��� �ٴ� �ǳ� �ڵ�

		jp1 = new JPanel();// �Ʒ� �ٴ� �ǳ�
		jp1.setLayout(new BorderLayout());// ���������߾� ���̾ƿ�
		jtf = new JTextField(30); // 30 ����
		jbtn = new JButton("����");// ���� ��ư ����

		style = new JPanel();
		style.setLayout(new FlowLayout());
		bg = new ButtonGroup();
		Plain = new JRadioButton("����", true);
		Bold = new JRadioButton("����", false);
		name = new JTextField(10);

		Plain.setFont(new Font("Helvetica", Font.PLAIN, 15));
		Bold.setFont(new Font("Helvetica", Font.BOLD, 15));

		bg.add(Plain);
		bg.add(Bold);

		style.add(Plain);
		style.add(Bold);
		jp1.add(style, BorderLayout.WEST);
		jp1.add(jbtn, BorderLayout.EAST);
		jp1.add(jtf, BorderLayout.CENTER);
		jp1.add(name, BorderLayout.SOUTH);
		// ���ʿ� �ٴ� �ǳ� �ڵ�
		jp2 = new JPanel();// ���ʿ� �ٴ� �ǳ�
		jp2.setLayout(new BorderLayout());
		jlb1 = new JLabel("��ȭ�� : " + id);// ��ȭ��
		jlb2 = new JLabel("IP �ּ� : " + ip);// IP �ּ� : 127.0.0.1
	

		jp2.add(jlb1, BorderLayout.CENTER);
		jp2.add(jlb2, BorderLayout.EAST);
		// �����ӿ� ���̴� �ڵ�

		jta = new JTextArea("", 10, 50); // �ʱⰪ, ��(����), ����(��)
		jta.setBackground(Color.YELLOW);// ���

		JScrollPane jsp = new JScrollPane(jta,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jframe.add(jp1, BorderLayout.SOUTH);
		//jframe.add(jp2, BorderLayout.NORTH);//�ʿ��� ����.
		jframe.add(jsp, BorderLayout.CENTER);

		
		name.addActionListener(this);
		jtf.addActionListener(this);
		jbtn.addActionListener(this);
		Plain.addItemListener(this);
		Bold.addItemListener(this);

		// X Ŭ���� ó���ϴ� �ڵ� �� ����
		jframe.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					oos.writeObject(id + "#exit");// ä�� ����
				} catch (Exception ee) {
					ee.printStackTrace();
				}// catch
				System.exit(0); // ���α׷� ����
			}// windowClosing

			@Override
			public void windowOpened(WindowEvent e) {
				jtf.requestFocus(); // jtf �� ��Ŀ���� ���´�.
			}// windowOpened
		});// ������ �̺�Ʈ ó�� ��
		jta.setEditable(false);// ���� X, ä�� ���� �����ֱ⸸ ����
		// ũ�� ���� �ڵ�
		jframe.setSize(400,600);// �ڵ� ũ�� ����
		jframe.setResizable(false);// â ũ�� ���� X
		jframe.setVisible(true);// ���̱�
		
		
		
	}// ������

	@Override
	public void actionPerformed(ActionEvent e) { // �̺�Ʈ ó��

		Object obj = e.getSource(); // ����Ʈ �߻� ��ġ ���
		String msg = jtf.getText(); // ä�� ���� �Է� �ޱ�
		String nameinput = name.getText();
		if(obj == name){
			id=nameinput;
		}

		if (obj == jtf) { // �Է¶����� ���͸� ģ ���

			if (msg == null || msg.length() == 0) {// ������ ���� ���
				// ���â �����ֱ�
				JOptionPane.showMessageDialog(jframe, "�ƹ��͵� �Էµ��� ����", "���",
						JOptionPane.WARNING_MESSAGE);
			}

			else { // ������ �Է��ϰ� ������ ���
				try {
					oos.writeObject(id + "#" + msg); // �Է½���
				} catch (Exception ee) {
					ee.printStackTrace();
				}// catch

				jtf.setText(""); // jtf �� �����.
			}
		}

		else if (obj == jbtn) { // ���� ��ư�� Ŭ���� ���

			try {
				oos.writeObject(id + "#exit");
			}
			catch (Exception ee) {
				ee.printStackTrace();
			}// catch

			System.exit(0);

		}

	}

	// itemListener
	public void itemStateChanged(ItemEvent event) {
		int style = 0;

		if (Plain.isSelected())
			style = Font.PLAIN;

		else if (Bold.isSelected())
			style += Font.BOLD;

		jtf.setFont(new Font("Helvetica", style, 15));
		jta.setFont(new Font("Helvetica", style, 15));
	}

	public void init() {
		try {
			socket = new Socket(ip, 5000);
			System.out.println("������ ���ӵǾ����ϴ�.....");
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			Thread t = new Thread(this);
			t.start(); // ������ ����

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
	
		JFrame.setDefaultLookAndFeelDecorated(true);//
		MultiClient cc = new MultiClient("127.0.0.1", id); // <- ���� ������ ���.
		
	
		
		cc.init(); // ������ ���� �ڵ� ����
	}

	@Override
	public void run() {
		String message = null;
		String[] receiveMsg = null;
		boolean isStop = false;
		while (!isStop) {

			try {
				message = (String) ois.readObject();// ä�ó���
				receiveMsg = message.split("#");// �̸�#����
			} catch (Exception e) {
				e.printStackTrace();
				isStop = true; // �ݺ��� ����� ����
			}// catch

			System.out.println(receiveMsg[0] + ":" + receiveMsg[1]);
			// ��) ȫ�浿 : �ȳ� ���

			if (receiveMsg[1].equals("exit")) { // ä�� ����

				if (receiveMsg[0].equals(id)) { // �ش� �����
					System.exit(0);
				} else { // �� ���� �����
					jta.append(receiveMsg[0] + " ���� �����߽��ϴ�\n");
				}// else : �� �� �����
			} else { // exit �� �ƴ� ���
						// ä�� ���� �����ֱ�
				jta.append(receiveMsg[0] + " : " + receiveMsg[1] + "\n");
			}
		}
	}
}

