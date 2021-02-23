package com.ypq;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.squareup.okhttp.OkHttpClient;

public class SpiderWindow extends JFrame {

	/**
	 * Ϊ�˷�ֹwarning
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private OkHttpClient client;
	private GetData getData;			//��װ��http��get����,��ȡ������
	private TimerTask1s timerTask1s;	//ָ����ʱ����������
	private Timer timer1s;				//һ�붨ʱ��,���ڶ�ʱ����ĳЩ�¼�
	private JTextField textRaceType;
	private ArrayList<MultipleAxis> multipleAxisList;		//Ԫ����ͼ��list,������14��ͼ(1-14ֻ��)
	private final int X = 10;		//��һ��ͼ�����Ͻ�X����
	private final int Y = 40;		//�ڶ���ͼ�����Ͻ�Y����
	private int width = 320;		//ÿһ��ͼ�Ŀ��
	private int height = 230;		//ÿһ��ͼ�ĸ߶�
	private int rowCount = 4;		//��һ�����ж��ٸ�ͼ
	private JTextField textDate;
	private JTextField textWidth;
	private JTextField textHeight;
	private JTextField textRowCount;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SpiderWindow frame = new SpiderWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * �趨������ͼ����ĳ���
	 * @param race
	 */
	public void setMultipleAxisRace(int race) {
		for(MultipleAxis e : multipleAxisList) {
			e.setRace(race);
		}
	}
	
	/**
	 * ���±���14ֻ���ͼ
	 */
	public void resize() {
		rowCount = Integer.parseInt(textRowCount.getText());
		width = Integer.parseInt(textWidth.getText());
		height = Integer.parseInt(textHeight.getText());
		for(int i = 0; i < multipleAxisList.size(); i++) {
			multipleAxisList.get(i).getChartpanel().setBounds((i % rowCount) * width + X, (i / rowCount) * height + Y, width, height);
		}
	}

	/**
	 * Create the frame.
	 */
	public SpiderWindow() {
		setTitle("spider");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 930, 793);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		client = new OkHttpClient();
		contentPane.setLayout(null);
		
		textDate = new JTextField();
		textDate.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				MultipleAxis.getDataSet().setFilePath(getData.getDate() + "_" + getData.getRaceType() + ".txt");

				getData.setDate(textDate.getText());
			}
		});
		textDate.setBounds(44, 10, 93, 21);
		contentPane.add(textDate);
		textDate.setPreferredSize(new Dimension(90, 21));
		textDate.setColumns(10);
		
		JLabel labelDate = new JLabel("\u65E5\u671F");
		labelDate.setBounds(10, 13, 31, 15);
		contentPane.add(labelDate);
		
		textRaceType = new JTextField();
		textRaceType.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				MultipleAxis.getDataSet().setFilePath(getData.getDate() + "_" + getData.getRaceType() + ".txt");
				getData.setRaceType(textRaceType.getText());
			}
		});
		textRaceType.setBounds(181, 10, 31, 21);
		contentPane.add(textRaceType);
		textRaceType.setColumns(10);
		
		JLabel labelRaceType = new JLabel("\u7C7B\u578B");
		labelRaceType.setBounds(147, 13, 31, 15);
		contentPane.add(labelRaceType);
		
		JLabel label = new JLabel("\u573A\u6B21");
		label.setBounds(222, 13, 31, 15);
		contentPane.add(label);
		
		final JButton btnStart = new JButton("\u5F00\u59CB");
		btnStart.setBounds(804, 9, 76, 23);
		contentPane.add(btnStart);
		
		textWidth = new JTextField();
		textWidth.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				resize();
			}
		});
		textWidth.setBounds(435, 10, 66, 21);
		contentPane.add(textWidth);
		textWidth.setColumns(10);
		
		JLabel lblWin = new JLabel("WIN");
		lblWin.setForeground(Color.RED);
		lblWin.setBounds(639, 13, 44, 15);
		contentPane.add(lblWin);
		
		JLabel lblPlace = new JLabel("PLACE");
		lblPlace.setForeground(Color.GREEN);
		lblPlace.setBounds(674, 13, 54, 15);
		contentPane.add(lblPlace);
		
		JLabel lblWinplace = new JLabel("WIN_PLACE");
		lblWinplace.setForeground(Color.BLUE);
		lblWinplace.setBounds(722, 13, 76, 15);
		contentPane.add(lblWinplace);
		
		final JSpinner spinnerRace = new JSpinner();
		spinnerRace.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setMultipleAxisRace((Short)(spinnerRace.getValue()));
				getData.setRace((Short)(spinnerRace.getValue()));
			}
		});
		spinnerRace.setModel(new SpinnerNumberModel(new Short((short) 1), new Short((short) 1), new Short((short) 14), new Short((short) 1)));
		spinnerRace.setBounds(253, 10, 44, 22);
		contentPane.add(spinnerRace);
		
		JLabel labelWidth = new JLabel("\u5BBD\u5EA6");
		labelWidth.setBounds(404, 13, 31, 15);
		contentPane.add(labelWidth);
		
		JLabel labelHeight = new JLabel("\u9AD8\u5EA6");
		labelHeight.setBounds(520, 13, 31, 15);
		contentPane.add(labelHeight);
		
		textHeight = new JTextField();
		textHeight.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				resize();
			}
		});
		textHeight.setColumns(10);
		textHeight.setBounds(551, 10, 66, 21);
		contentPane.add(textHeight);
		
		JLabel labelRowCount = new JLabel("\u6BCF\u884C");
		labelRowCount.setBounds(336, 13, 31, 15);
		contentPane.add(labelRowCount);
		
		textRowCount = new JTextField();
		textRowCount.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				resize();
			}
		});
		textRowCount.setColumns(10);
		textRowCount.setBounds(370, 10, 24, 21);
		contentPane.add(textRowCount);
		
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					//��ʼ������,���ô󲿷��ı���,��ȡ��������(����еĻ�),������ʱ��ȡ���ݺͶ�ʱ��ͼ
					if(btnStart.isEnabled()) {
						btnStart.setEnabled(false);
						textDate.setEditable(false);
						textRaceType.setEditable(false);
						DataSet.getInstance().readFile();
						timer1s.schedule(timerTask1s, 0, 1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
		//-----------------���´���Ϊ�Լ����,�Ǳ༭���Զ�����-----------------------//
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);		//�������
		//�����ı���ĳ�ʼֵ
		textDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		textRaceType.setText("3H");
		textRowCount.setText(String.valueOf(rowCount));
		textWidth.setText(String.valueOf(width));
		textHeight.setText(String.valueOf(height));
		
		//������ͼ�������,���Ұ�Ĭ�ϲ�������14ֻ��
		multipleAxisList = new ArrayList<MultipleAxis>();
		for(int horse = 1; horse <= 14; horse++) {		//����ǿ�Ƽ���ֻ��14ֻ��
			MultipleAxis ma = new MultipleAxis(horse);
			contentPane.add(ma.getChartpanel());
			multipleAxisList.add(ma);
		}
		resize();
		//�½���ȡ����,��ʱ��,��ʱ�������
		getData = new GetData(textDate.getText(), textRaceType.getText());
		timer1s = new Timer();
		timerTask1s = new TimerTask1s(getData, multipleAxisList);
		
//		new SpiderLogin(client).testCode();
	}
}
