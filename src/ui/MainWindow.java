package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import init.ServicesInit;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.DropMode;
import javax.swing.JTextArea;
import java.awt.Component;
import javax.swing.JScrollPane;

public class MainWindow {

	private JFrame frmEmbddedServer;
	
	private ServicesInit inicializadorServicos = new ServicesInit(8000);
	private JTextField portTexto;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmEmbddedServer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}
	
	private void updateLabelStatus(JLabel statusSocketLabel, JLabel statusBDLabel) {
		statusSocketLabel.setText( inicializadorServicos.controlador.servidor.isClosed() ? "OFF" : "ON" );
		try {
			statusBDLabel.setText( inicializadorServicos.mysql.sql.isClosed() ? "OFF" : "ON" );
		} catch (SQLException e1) {e1.printStackTrace();}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEmbddedServer = new JFrame();
		frmEmbddedServer.setResizable(false);
		frmEmbddedServer.setTitle("EmBDded Server");
		frmEmbddedServer.setBounds(100, 100, 655, 434);
		frmEmbddedServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmEmbddedServer.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(424, 0, 221, 130);
		frmEmbddedServer.getContentPane().add(tabbedPane, BorderLayout.WEST);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Serviços", null, panel, null);
		panel.setLayout(null);
		
		JLabel lblPorta = new JLabel("Porta:");
		lblPorta.setBounds(12, 12, 66, 15);
		panel.add(lblPorta);
		
		portTexto = new JTextField();
		portTexto.setHorizontalAlignment(SwingConstants.CENTER);
		portTexto.setText("8000");
		portTexto.setBounds(59, 10, 66, 19);
		panel.add(portTexto);
		portTexto.setColumns(10);
		
		JButton btnNewButton = new JButton("Iniciar");
		
		btnNewButton.setBounds(137, 7, 76, 25);
		panel.add(btnNewButton);
		
		JLabel lblBd = new JLabel("Status BD:");
		lblBd.setBounds(12, 46, 82, 15);
		panel.add(lblBd);
		
		JLabel lblStatusSocket = new JLabel("Status Socket:");
		lblStatusSocket.setBounds(12, 73, 100, 15);
		panel.add(lblStatusSocket);
		
		JLabel statusBDLabel = new JLabel("PARADO");
		statusBDLabel.setBounds(138, 46, 66, 15);
		panel.add(statusBDLabel);
		
		JLabel statusSocketLabel = new JLabel("PARADO");
		statusSocketLabel.setBounds(137, 73, 66, 15);
		panel.add(statusSocketLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 412, 397);
		frmEmbddedServer.getContentPane().add(scrollPane);
		
		JTextArea logArea = new JTextArea();
		scrollPane.setViewportView(logArea);
		logArea.setLineWrap(true);
		logArea.setEditable(false);
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand() == "Iniciar") {
					if(inicializadorServicos.start(logArea)) {
						portTexto.setEnabled(false);
						btnNewButton.setText( "Parar" );
						updateLabelStatus(statusSocketLabel, statusBDLabel);
					}
				} else {
					inicializadorServicos.parar();
					btnNewButton.setText( "Iniciar" );
					updateLabelStatus(statusSocketLabel, statusBDLabel);
					portTexto.setEnabled(true);
				}
			}
		});
	}
}
