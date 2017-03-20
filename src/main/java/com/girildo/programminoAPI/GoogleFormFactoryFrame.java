package com.girildo.programminoAPI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import java.awt.Font;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JMenuItem;

public class GoogleFormFactoryFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextArea textAreaMessage;
	private JPanel panel;
	private JTextField textFieldTitolo;
	private JLabel lblNewLabel;
	private JSeparator separator;
	private JLabel lblNumeroFoto;
	private JTextField textFieldNumFoto;
	private JPopupMenu popupMenu;
	private JMenuItem mntmCopia;
	public GoogleFormFactoryFrame() {
		setResizable(false);
		setTitle("Generatore Google Forms");
		setType(Type.UTILITY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 859, 386);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JButton btnGeneraForm = new JButton("Genera form");
		btnGeneraForm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnGeneraFormOnClick();
			}
		});
		contentPane.setLayout(new MigLayout("", "[656px,grow]", "[20%,grow][10%][10px][70%,grow]"));
		
		panel = new JPanel();
		contentPane.add(panel, "cell 0 0,grow");
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{117, 422, 0};
		gbl_panel.rowHeights = new int[] {25, 25, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		lblNewLabel = new JLabel("Titolo contest:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		textFieldTitolo = new JTextField();
		GridBagConstraints gbc_textFieldTitolo = new GridBagConstraints();
		gbc_textFieldTitolo.fill = GridBagConstraints.BOTH;
		gbc_textFieldTitolo.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldTitolo.anchor = GridBagConstraints.NORTHWEST;
		gbc_textFieldTitolo.gridx = 1;
		gbc_textFieldTitolo.gridy = 0;
		panel.add(textFieldTitolo, gbc_textFieldTitolo);
		textFieldTitolo.setColumns(10);
		
		lblNumeroFoto = new JLabel("Numero foto:");
		GridBagConstraints gbc_lblNumeroFoto = new GridBagConstraints();
		gbc_lblNumeroFoto.anchor = GridBagConstraints.EAST;
		gbc_lblNumeroFoto.insets = new Insets(0, 0, 0, 5);
		gbc_lblNumeroFoto.gridx = 0;
		gbc_lblNumeroFoto.gridy = 1;
		panel.add(lblNumeroFoto, gbc_lblNumeroFoto);
		
		textFieldNumFoto = new JTextField();
		textFieldNumFoto.setColumns(10);
		GridBagConstraints gbc_textFieldNumFoto = new GridBagConstraints();
		gbc_textFieldNumFoto.fill = GridBagConstraints.BOTH;
		gbc_textFieldNumFoto.gridx = 1;
		gbc_textFieldNumFoto.gridy = 1;
		panel.add(textFieldNumFoto, gbc_textFieldNumFoto);
		contentPane.add(btnGeneraForm, "cell 0 1,grow");
		
		separator = new JSeparator();
		contentPane.add(separator, "cell 0 2");
		
		textAreaMessage = new JTextArea();
		textAreaMessage.setLineWrap(true);
		textAreaMessage.setFont(new Font("Monospaced", Font.PLAIN, 17));
		textAreaMessage.setEditable(false);
		contentPane.add(textAreaMessage, "cell 0 3,grow");
		
		popupMenu = new JPopupMenu();
		addPopup(textAreaMessage, popupMenu);
		
		mntmCopia = new JMenuItem("Copia");
		mntmCopia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				copia(textAreaMessage.getSelectedText());
			}
		});
		popupMenu.add(mntmCopia);
		/*
		try {
			GoogleAppScriptInterface.retrieveFormResponses
			("https://docs.google.com/forms/d/1Y45Kefb_-iFw2Kk_K85yYAqL2msB_9NfeSvr-NzkIOA");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	protected void btnGeneraFormOnClick()
	{
		String name = textFieldTitolo.getText().trim();
		int maxPhotos = 0;
		try{
			maxPhotos = Integer.parseInt(textFieldNumFoto.getText().trim());
		} catch (NumberFormatException e){
			textAreaMessage.setText(null);
			textAreaMessage.append("Inserisci un numero valido per il numero di foto!");
			return;
		}
		if(name.isEmpty())
		{
			textAreaMessage.setText(null);
			textAreaMessage.append("Inserisci un titolo per il contest!");
			return;
		}
		
			this.generateForm(name, maxPhotos);
		
	}
	
	private void generateForm(final String name, final int maxPhotos)
	{
		textAreaMessage.setText(null);
		textAreaMessage.append("Genero il form " + name + "...");
		final WaitDialog dial = new WaitDialog(this);
		dial.setLocationRelativeTo(this);
		dial.setVisible(true);
		
		SwingWorker<String, Void> task = new SwingWorker<String, Void>()
		{
			@Override
			protected String doInBackground() throws Exception
			{
				String result = GoogleAppScriptInterface.createGoogleForm(name, maxPhotos);
				return result;
				//GoogleFormFactory.run(name, maxPhotos);
				//return "a";
			}
			@Override
			protected void done()
			{
				dial.setVisible(false);
				dial.dispose();
				try {
					formCreationResult(this.get());
				} catch (InterruptedException e) {
					textAreaMessage.append(e.getLocalizedMessage());
				} catch (ExecutionException e) {
					textAreaMessage.append(e.getLocalizedMessage());
				}	
			}
		};
		task.execute();
	}

	protected void formCreationResult(String url)
	{
		if(url != null)
		{
			copia(url);
			textAreaMessage.append("\nForm creato con successo.");
			textAreaMessage.append("\nCopio l'url negli appunti");
			textAreaMessage.append("\n" + url);
		}
		else
			textAreaMessage.append("\n Errore!");
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	
	protected void copia(String selectedText)
	{
		if(selectedText.isEmpty())
			return;
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection s = new StringSelection(selectedText);
		clipboard.setContents(s, null);
	}
}
