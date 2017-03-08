import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 控制面板类，继承自JPanel.上边安放预显窗口、等级、得分、控制按钮，主要用来配置游戏参数。
 */
class SetPanel extends JPanel {
	// 右侧面板配置参数、选项
	private JTextField
	        tfLevel = new JTextField(""+Tetris.DEFAULT_LEVEL), // 关、级
	        tfScore = new JTextField("0");  // 分数
	private JButton
	        btPlay = new JButton("开始游戏"),
	        btPause = new JButton("暂停"),
	        btStop = new JButton("停止游戏"),
	        btTurnLevelUp = new JButton("增加等级"),
	        btTurnLevelDown = new JButton("降低等级");
	private JPanel plTip = new JPanel(new BorderLayout());
	private Nextblok plTipBlock = new Nextblok();
	private JPanel plInfo = new JPanel(new GridLayout(4, 1));
	private JPanel plButton = new JPanel(new GridLayout(5, 1));
	private Timer timer;
	private Tetris Runblok;
	/**
	 * 控制面板类的构造函数
	 * 方便直接控制ErsBoxesGame类的行为。
	 */
	public SetPanel(final Tetris game) {
		setLayout(new GridLayout(3, 1, 0, 4));
		this.Runblok = game;
		plTip.add(new JLabel("下一个方块"), BorderLayout.NORTH);
		plTip.add(plTipBlock);
		plInfo.add(new JLabel("关卡等级"));
		plInfo.add(tfLevel);
		plInfo.add(new JLabel("玩家得分"));
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
		addKeyListener(new ControlKeyListener()); // 键盘监听
		btPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Runblok.playGame();
			}
		});
		btPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (btPause.getText().equals(new String("暂停"))) {
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
					int level = Integer.parseInt(tfLevel.getText()); // 把整形对象转换为int型
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
		// 设定每0.5秒
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
	 * 设置预显窗口的样式，
	 */
	public void setTipStyle(int style) {
		plTipBlock.setStyle(style);
	}

	/**
	 * 取得用户设置的游戏等级。
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
	 * 让用户修改游戏难度等级。
	 */
	public void setLevel(int level) {
		if (level > 0 && level < 11) tfLevel.setText("" + level);
	}

	/**
	 * 设置"开始"按钮的状态。
	 */
	public void setPlayButtonEnable(boolean enable) {
		btPlay.setEnabled(enable);
	}

	public void setPauseButtonLabel(boolean pause) {
		btPause.setText(pause ? "暂停" : "继续");
	}

	/**
	 * 重置控制面板
	 */
	public void reset() {
		tfScore.setText("0");
		plTipBlock.setStyle(0);
	}

	/**
	 * 重新计算TipPanel里的boxes[][]里的小框的大小
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
	 * 预显窗口的实现细节类：Nextblok
	 */
	private class Nextblok extends JPanel {
		private Color backColor = Color.gray, frontColor = Color.darkGray;
		private ErsBox[][] boxes = new ErsBox[ErsBlock.BOXES_ROWS][ErsBlock.BOXES_COLS];
		private int style, boxWidth, boxHeight;
		//private boolean isTiled = false;

		/**
		 * 预显窗口类构造函数
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
		 * 设置预显窗口的方块样式
		 */
		public void setStyle(int style) {
			this.style = style;
			repaint(); // 重绘组件，刷新画板！！
		}

		/**
		 * 覆盖JComponent类的函数，画组件。
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
					        boxWidth, boxHeight, true); // 画矩形
					key >>= 1;
				}
			}
		}

		/**
		 * 根据窗口的大小，自动调整方格的尺寸
		 */
		public void fanning() {
			boxWidth = getSize().width / ErsBlock.BOXES_COLS;
			boxHeight = getSize().height / ErsBlock.BOXES_ROWS;
			//isTiled = true;
		}
	}

}
