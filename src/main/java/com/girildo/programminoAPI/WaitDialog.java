package com.girildo.programminoAPI;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

public class WaitDialog extends JDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public WaitDialog(JFrame frame)
	{
		super(frame);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) 
			{
				//dispose();
			}
		});
		setResizable(false);
		setModal(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 448, 98);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JLabel lblUnAttimo = new JLabel("Un attimo...");
			contentPanel.add(lblUnAttimo, BorderLayout.NORTH);
		}
		{
			JProgressBar progressBar = new JProgressBar();
			progressBar.setIndeterminate(true);
			contentPanel.add(progressBar, BorderLayout.CENTER);
		}
	}

}
