import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ��������࣬�̳���JPanel.�ϱ߰���Ԥ�Դ��ڡ��ȼ����÷֡����ư�ť����Ҫ����������Ϸ������
 */
class SetPanel extends JPanel {
	// �Ҳ�������ò�����ѡ��
	private JTextField
	        tfLevel = new JTextField(""+Tetris.DEFAULT_LEVEL), // �ء���
	        tfScore = new JTextField("0");  // ����
	private JButton
	        btPlay = new JButton("��ʼ��Ϸ"),
	        btPause = new JButton("��ͣ"),
	        btStop = new JButton("ֹͣ��Ϸ"),
	        btTurnLevelUp = new JButton("���ӵȼ�"),
	        btTurnLevelDown = new JButton("���͵ȼ�");
	private JPanel plTip = new JPanel(new BorderLayout());
	private Nextblok plTipBlock = new Nextblok();
	private JPanel plInfo = new JPanel(new GridLayout(4, 1));
	private JPanel plButton = new JPanel(new GridLayout(5, 1));
	private Timer timer;
	private Tetris Runblok;
	/**
	 * ���������Ĺ��캯��
	 * ����ֱ�ӿ���ErsBoxesGame�����Ϊ��
	 */
	public SetPanel(final Tetris game) {
		setLayout(new GridLayout(3, 1, 0, 4));
		this.Runblok = game;
		plTip.add(new JLabel("��һ������"), BorderLayout.NORTH);
		plTip.add(plTipBlock);
		plInfo.add(new JLabel("�ؿ��ȼ�"));
		plInfo.add(tfLevel);
		plInfo.add(new JLabel("��ҵ÷�"));
		plInfo.add(tfScore);
		tfLevel.setEditable(false);
		tfScore.setEditable(false);
		plButton.add(btPlay);
		plButton.add(btPause);
		plButton.add(btStop);
		plButton.add(btTurnLevelUp);
		plButton.add(btTurnLevelDown);
		add(plTip);
		add(plInfo);
		add(plButton);
		addKeyListener(new ControlKeyListener()); // ���̼���
		btPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Runblok.playGame();
			}
		});
		btPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (btPause.getText().equals(new String("��ͣ"))) {
					Runblok.pauseGame();
				} else {
					Runblok.resumeGame();
				}
			}
		});
		btStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Runblok.stopGame();
			}
		});
		btTurnLevelUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					int level = Integer.parseInt(tfLevel.getText());
					if (level < Tetris.MAX_LEVEL)
						tfLevel.setText("" + (level + 1));
				} 
				catch (NumberFormatException e) {}
			}
		});
		btTurnLevelDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					int level = Integer.parseInt(tfLevel.getText()); // �����ζ���ת��Ϊint��
					if (level > 1)
						tfLevel.setText("" + (level - 1));
				} catch (NumberFormatException e) {
				}
			}
		});

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				plTipBlock.fanning();
			}
		});
		// �趨ÿ0.5��
		timer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				tfScore.setText("" + Runblok.getScore());
				int scoreForLevelUpdate = Runblok.getScoreForLevelUpdate();
				if (scoreForLevelUpdate >= Tetris.PER_LEVEL_SCORE && scoreForLevelUpdate > 0)
					Runblok.levelUpdate();
			}
		});
		timer.start();
	}

	/**
	 * ����Ԥ�Դ��ڵ���ʽ��
	 */
	public void setTipStyle(int style) {
		plTipBlock.setStyle(style);
	}

	/**
	 * ȡ���û����õ���Ϸ�ȼ���
	 */
	public int getLevel() {
		int level = 0;
		try {
			level = Integer.parseInt(tfLevel.getText());
		} catch (NumberFormatException e) {
		}
		return level;
	}

	/**
	 * ���û��޸���Ϸ�Ѷȵȼ���
	 */
	public void setLevel(int level) {
		if (level > 0 && level < 11) tfLevel.setText("" + level);
	}

	/**
	 * ����"��ʼ"��ť��״̬��
	 */
	public void setPlayButtonEnable(boolean enable) {
		btPlay.setEnabled(enable);
	}

	public void setPauseButtonLabel(boolean pause) {
		btPause.setText(pause ? "��ͣ" : "����");
	}

	/**
	 * ���ÿ������
	 */
	public void reset() {
		tfScore.setText("0");
		plTipBlock.setStyle(0);
	}

	/**
	 * ���¼���TipPanel���boxes[][]���С��Ĵ�С
	 */
	public void fanning() {
		plTipBlock.fanning();
	}

	private class ControlKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent ke) {
			if (!Runblok.isPlaying()) return;

			ErsBlock block = Runblok.getCurBlock();
			switch (ke.getKeyCode()) {
				case KeyEvent.VK_DOWN:
					block.moveDown();
					break;
				case KeyEvent.VK_LEFT:
					block.moveLeft();
					break;
				case KeyEvent.VK_RIGHT:
					block.moveRight();
					break;
				case KeyEvent.VK_UP:
					block.turnNext();
					break;
				default:
					break;
			}
		}
	}
	/**
	 * Ԥ�Դ��ڵ�ʵ��ϸ���ࣺNextblok
	 */
	private class Nextblok extends JPanel {
		private Color backColor = Color.gray, frontColor = Color.darkGray;
		private ErsBox[][] boxes = new ErsBox[ErsBlock.BOXES_ROWS][ErsBlock.BOXES_COLS];
		private int style, boxWidth, boxHeight;
		//private boolean isTiled = false;

		/**
		 * Ԥ�Դ����๹�캯��
		 */
		public Nextblok() {
			for (int i = 0; i < boxes.length; i++) {
				for (int j = 0; j < boxes[i].length; j++)
					boxes[i][j] = new ErsBox(false);
			}
		}
		public Nextblok(Color backColor, Color frontColor) {
			this();
			this.backColor = backColor;
			this.frontColor = frontColor;
		}

		/**
		 * ����Ԥ�Դ��ڵķ�����ʽ
		 */
		public void setStyle(int style) {
			this.style = style;
			repaint(); // �ػ������ˢ�»��壡��
		}

		/**
		 * ����JComponent��ĺ������������
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			//if (!isTiled) fanning();
			int key = 0x8000;
			for (int i = 0; i < boxes.length; i++) {
				for (int j = 0; j < boxes[i].length; j++) {
					Color color = (((key & style) != 0) ? frontColor : backColor);
					g.setColor(color);
					g.fill3DRect(j * boxWidth, i * boxHeight,
					        boxWidth, boxHeight, true); // ������
					key >>= 1;
				}
			}
		}

		/**
		 * ���ݴ��ڵĴ�С���Զ���������ĳߴ�
		 */
		public void fanning() {
			boxWidth = getSize().width / ErsBlock.BOXES_COLS;
			boxHeight = getSize().height / ErsBlock.BOXES_ROWS;
			//isTiled = true;
		}
	}

}
