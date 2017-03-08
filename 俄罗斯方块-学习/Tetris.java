/**
 * File: Tetris.java
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 游戏主类，继承自JFrame类，负责游戏的全局控制。
 */
public class Tetris extends JFrame {
	// 设定游戏数据配置项
	public final static int PER_LINE_SCORE = 10; // 填满一行分数
	public final static int PER_LEVEL_SCORE = PER_LINE_SCORE * 5; // 升级分数
	public final static int MAX_LEVEL = 10; // 最大级数
	public final static int DEFAULT_LEVEL = 5; // 默认级数
	private GameCanvas canvas;
	private ErsBlock block;
	private boolean playing = false;
	private SetPanel ctrlPanel;
	private JMenuBar bar = new JMenuBar(); // 设置菜单条
	private JMenu
	        mGame = new JMenu("游戏"),
			mControl = new JMenu("设置选项"),
			mWindowStyle = new JMenu("帮助");
	private JMenuItem
	        miNewGame = new JMenuItem("新游戏"),
			miTurnHarder = new JMenuItem("增加游戏难度"),
			miTurnEasier = new JMenuItem("降低游戏难度"),
			miExit = new JMenuItem("退出"),
			miPlay = new JMenuItem("开始游戏"),
			miPause = new JMenuItem("暂停游戏"),
			miResume = new JMenuItem("重置游戏"),
			miStop = new JMenuItem("停止游戏");

	/**
	 * 主游戏类的构造函数，游戏主界面大小位置布局设定
	 */
	public Tetris(String title) {
		setTitle(title);
		setSize(315, 392);
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((scrSize.width - getSize().width) / 2,
		            (scrSize.height - getSize().height) / 2);
		
		createMenu();
		// 构造一个具有指定组件横向间距为6，纵向间距为0的布局。
		Container container = getContentPane();
		container.setLayout(new BorderLayout(6, 0));
		canvas = new GameCanvas(20, 12);
		ctrlPanel = new SetPanel(this);
		container.add(canvas, BorderLayout.CENTER);
		container.add(ctrlPanel, BorderLayout.EAST);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { // 窗口关闭事件
				stopGame();
				System.exit(0);
			}
		});
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) { // 窗口缩放
				canvas.fanning();
			}
		});
		setVisible(true);
//		canvas.fanning(); 
	}
	
	/**
	 * 建立并设置窗口菜单
	 */
	private void createMenu() {
		// 配置菜单选项
		bar.add(mGame);
		bar.add(mControl);
		bar.add(mWindowStyle);
		mGame.add(miNewGame);
		mGame.addSeparator(); 
		mGame.add(miTurnHarder);
		mGame.add(miTurnEasier);
		mGame.addSeparator();
		mGame.add(miExit);
		mControl.add(miPlay);
		mControl.add(miPause);
		mControl.add(miResume);
		mControl.add(miStop);
		setJMenuBar(bar);  // 添加菜单栏 

		miPause.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
		miResume.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

		miNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				stopGame();
				reset();
				setLevel(DEFAULT_LEVEL); // 设置默认级数
			}
		});
		miTurnHarder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int curLevel = getLevel();
				if (curLevel < MAX_LEVEL) setLevel(curLevel + 1);
			}
		});
		miTurnEasier.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int curLevel = getLevel();
				if (curLevel > 1) setLevel(curLevel - 1);
			}
		});
		miExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		});
		miPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				playGame();
			}
		});
		miPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				pauseGame();
			}
		});
		miResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				resumeGame();
			}
		});
		miStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				stopGame();
			}
		});
	}


	// 让游戏复位
	public void reset() {
		ctrlPanel.reset();
		canvas.reset();
	}

	// 判断游戏是否还在进行
	public boolean isPlaying() {
		return playing;
	}

	// 得到当前活动的块
	public ErsBlock getCurBlock() {
		return block;
	}

	// 得到当前画布
	public GameCanvas getCanvas() {
		return canvas;
	}

	// 开始游戏
	public void playGame() {
		play();
		ctrlPanel.setPlayButtonEnable(false);
		miPlay.setEnabled(false);
		ctrlPanel.requestFocus();
	}

	// 游戏暂停
	public void pauseGame() {
		if (block != null) block.pauseMove();
		ctrlPanel.setPauseButtonLabel(false);
		miPause.setEnabled(false);
		miResume.setEnabled(true);
	}

	// 让暂停中的游戏继续
	public void resumeGame() {
		if (block != null) block.resumeMove();
		ctrlPanel.setPauseButtonLabel(true);
		miPause.setEnabled(true);
		miResume.setEnabled(false);
		ctrlPanel.requestFocus();
	}
 
	// 用户停止游戏
	public void stopGame() {
		playing = false;
		if (block != null) block.stopMove();
		miPlay.setEnabled(true);
		miPause.setEnabled(true);
		miResume.setEnabled(false);
		ctrlPanel.setPlayButtonEnable(true); // 右侧面板可用
		ctrlPanel.setPauseButtonLabel(true);
	}

	// 得到当前游戏者设置的游戏难度，游戏难度1－MAX_LEVEL
	public int getLevel() {
		return ctrlPanel.getLevel();
	}

	// 让用户设置游戏难度，游戏难度1－MAX_LEVEL
	public void setLevel(int level) {
		if (level < 11 && level > 0) ctrlPanel.setLevel(level);
	}

	// 得到游戏积分
	public int getScore() {
		if (canvas != null) return canvas.getScore();
		return 0;
	}

	// 得到自上次升级以来的游戏积分，升级以后，此积分清零
	public int getScoreForLevelUpdate() {
		if (canvas != null) return canvas.getScoreForLevelUpdate();
		return 0;
	}

	/**
	 * 当分数累计到一定的数量时，升一次级
	 */
	public boolean levelUpdate() {
		int curLevel = getLevel();
		if (curLevel < MAX_LEVEL) {
			setLevel(curLevel + 1);
			canvas.resetScoreForLevelUpdate(); // 将分数清零
			return true;
		}
		return false;
	}

	/**
	 * 游戏开始
	 */
	private void play() {
		reset();
		playing = true;
		Thread thread = new Thread(new Runblok());
		thread.start();
	}

	/**
	 * 游戏结束报告
	 */
	private void reportGameOver() {
		JOptionPane.showMessageDialog(this, "游戏结束！玩家得分"+getScore()+"分");
	}
	
	/**
	 * 一轮游戏过程，实现了Runnable接口
	 */
	private class Runblok implements Runnable {
		public void run() {
			int col = (int) (Math.random() * (canvas.getCols() - 3)), 
			    style = ErsBlock.STYLES[(int) (Math.random() * 7)][(int) (Math.random() * 4)];

			while (playing) {
			    if (block != null) {    // 第一次循环时，block为空
					if (block.isAlive()) { 
						try {
							Thread.currentThread().sleep(100); // 使当前线程每隔100毫秒暂停执行
						} catch (InterruptedException ie) { 
							ie.printStackTrace();
						}
						continue;
					}
				}

				checkFullLine();        //检查是否有全填满的行

				if (isGameOver()) {     //检查游戏是否应该结束了
					miPlay.setEnabled(true);
					miPause.setEnabled(true);
					miResume.setEnabled(false);
					ctrlPanel.setPlayButtonEnable(true);
					ctrlPanel.setPauseButtonLabel(true);
					
					reportGameOver();
					return;
				}
				block = new ErsBlock(style, -1, 4, getLevel(), canvas);
				block.start();
				style = ErsBlock.STYLES[(int) (Math.random() * 7)][(int) (Math.random() * 4)];
				ctrlPanel.setTipStyle(style); //设定下一个方块
			}
		}

		/**
		 * 检查画布中是否有全填满的行，如果有就删除之
		 */
		public void checkFullLine() {
			for (int i = 0; i < canvas.getRows(); i++) {
				int row = -1;
				boolean fullLineColorBox = true;
				for (int j = 0; j < canvas.getCols(); j++) {
					if (!canvas.getBox(i, j).isColorBox()) {
						fullLineColorBox = false;
						break;
					}
				}
				if (fullLineColorBox) {
					row = i--;
					canvas.removeLine(row);
				}
			}
		}

		/**
		 * 根据最顶行是否被占，判断游戏是否已经结束了。
		 */
		private boolean isGameOver() {
			for (int i = 0; i < canvas.getCols(); i++) {
				ErsBox box = canvas.getBox(0, i);
				if (box.isColorBox()) return true;
			}
			return false;
		}
	}

	/**
	 * 程序主函数
	 */
	public static void main(String[] args) {
		new Tetris("俄罗斯方块");
	}
}
