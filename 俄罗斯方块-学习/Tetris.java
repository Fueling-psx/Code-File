/**
 * File: Tetris.java
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ��Ϸ���࣬�̳���JFrame�࣬������Ϸ��ȫ�ֿ��ơ�
 */
public class Tetris extends JFrame {
	// �趨��Ϸ����������
	public final static int PER_LINE_SCORE = 10; // ����һ�з���
	public final static int PER_LEVEL_SCORE = PER_LINE_SCORE * 5; // ��������
	public final static int MAX_LEVEL = 10; // �����
	public final static int DEFAULT_LEVEL = 5; // Ĭ�ϼ���
	private GameCanvas canvas;
	private ErsBlock block;
	private boolean playing = false;
	private SetPanel ctrlPanel;
	private JMenuBar bar = new JMenuBar(); // ���ò˵���
	private JMenu
	        mGame = new JMenu("��Ϸ"),
			mControl = new JMenu("����ѡ��"),
			mWindowStyle = new JMenu("����");
	private JMenuItem
	        miNewGame = new JMenuItem("����Ϸ"),
			miTurnHarder = new JMenuItem("������Ϸ�Ѷ�"),
			miTurnEasier = new JMenuItem("������Ϸ�Ѷ�"),
			miExit = new JMenuItem("�˳�"),
			miPlay = new JMenuItem("��ʼ��Ϸ"),
			miPause = new JMenuItem("��ͣ��Ϸ"),
			miResume = new JMenuItem("������Ϸ"),
			miStop = new JMenuItem("ֹͣ��Ϸ");

	/**
	 * ����Ϸ��Ĺ��캯������Ϸ�������Сλ�ò����趨
	 */
	public Tetris(String title) {
		setTitle(title);
		setSize(315, 392);
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((scrSize.width - getSize().width) / 2,
		            (scrSize.height - getSize().height) / 2);
		
		createMenu();
		// ����һ������ָ�����������Ϊ6��������Ϊ0�Ĳ��֡�
		Container container = getContentPane();
		container.setLayout(new BorderLayout(6, 0));
		canvas = new GameCanvas(20, 12);
		ctrlPanel = new SetPanel(this);
		container.add(canvas, BorderLayout.CENTER);
		container.add(ctrlPanel, BorderLayout.EAST);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { // ���ڹر��¼�
				stopGame();
				System.exit(0);
			}
		});
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) { // ��������
				canvas.fanning();
			}
		});
		setVisible(true);
//		canvas.fanning(); 
	}
	
	/**
	 * ���������ô��ڲ˵�
	 */
	private void createMenu() {
		// ���ò˵�ѡ��
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
		setJMenuBar(bar);  // ��Ӳ˵��� 

		miPause.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
		miResume.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

		miNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				stopGame();
				reset();
				setLevel(DEFAULT_LEVEL); // ����Ĭ�ϼ���
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


	// ����Ϸ��λ
	public void reset() {
		ctrlPanel.reset();
		canvas.reset();
	}

	// �ж���Ϸ�Ƿ��ڽ���
	public boolean isPlaying() {
		return playing;
	}

	// �õ���ǰ��Ŀ�
	public ErsBlock getCurBlock() {
		return block;
	}

	// �õ���ǰ����
	public GameCanvas getCanvas() {
		return canvas;
	}

	// ��ʼ��Ϸ
	public void playGame() {
		play();
		ctrlPanel.setPlayButtonEnable(false);
		miPlay.setEnabled(false);
		ctrlPanel.requestFocus();
	}

	// ��Ϸ��ͣ
	public void pauseGame() {
		if (block != null) block.pauseMove();
		ctrlPanel.setPauseButtonLabel(false);
		miPause.setEnabled(false);
		miResume.setEnabled(true);
	}

	// ����ͣ�е���Ϸ����
	public void resumeGame() {
		if (block != null) block.resumeMove();
		ctrlPanel.setPauseButtonLabel(true);
		miPause.setEnabled(true);
		miResume.setEnabled(false);
		ctrlPanel.requestFocus();
	}
 
	// �û�ֹͣ��Ϸ
	public void stopGame() {
		playing = false;
		if (block != null) block.stopMove();
		miPlay.setEnabled(true);
		miPause.setEnabled(true);
		miResume.setEnabled(false);
		ctrlPanel.setPlayButtonEnable(true); // �Ҳ�������
		ctrlPanel.setPauseButtonLabel(true);
	}

	// �õ���ǰ��Ϸ�����õ���Ϸ�Ѷȣ���Ϸ�Ѷ�1��MAX_LEVEL
	public int getLevel() {
		return ctrlPanel.getLevel();
	}

	// ���û�������Ϸ�Ѷȣ���Ϸ�Ѷ�1��MAX_LEVEL
	public void setLevel(int level) {
		if (level < 11 && level > 0) ctrlPanel.setLevel(level);
	}

	// �õ���Ϸ����
	public int getScore() {
		if (canvas != null) return canvas.getScore();
		return 0;
	}

	// �õ����ϴ�������������Ϸ���֣������Ժ󣬴˻�������
	public int getScoreForLevelUpdate() {
		if (canvas != null) return canvas.getScoreForLevelUpdate();
		return 0;
	}

	/**
	 * �������ۼƵ�һ��������ʱ����һ�μ�
	 */
	public boolean levelUpdate() {
		int curLevel = getLevel();
		if (curLevel < MAX_LEVEL) {
			setLevel(curLevel + 1);
			canvas.resetScoreForLevelUpdate(); // ����������
			return true;
		}
		return false;
	}

	/**
	 * ��Ϸ��ʼ
	 */
	private void play() {
		reset();
		playing = true;
		Thread thread = new Thread(new Runblok());
		thread.start();
	}

	/**
	 * ��Ϸ��������
	 */
	private void reportGameOver() {
		JOptionPane.showMessageDialog(this, "��Ϸ��������ҵ÷�"+getScore()+"��");
	}
	
	/**
	 * һ����Ϸ���̣�ʵ����Runnable�ӿ�
	 */
	private class Runblok implements Runnable {
		public void run() {
			int col = (int) (Math.random() * (canvas.getCols() - 3)), 
			    style = ErsBlock.STYLES[(int) (Math.random() * 7)][(int) (Math.random() * 4)];

			while (playing) {
			    if (block != null) {    // ��һ��ѭ��ʱ��blockΪ��
					if (block.isAlive()) { 
						try {
							Thread.currentThread().sleep(100); // ʹ��ǰ�߳�ÿ��100������ִͣ��
						} catch (InterruptedException ie) { 
							ie.printStackTrace();
						}
						continue;
					}
				}

				checkFullLine();        //����Ƿ���ȫ��������

				if (isGameOver()) {     //�����Ϸ�Ƿ�Ӧ�ý�����
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
				ctrlPanel.setTipStyle(style); //�趨��һ������
			}
		}

		/**
		 * ��黭�����Ƿ���ȫ�������У�����о�ɾ��֮
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
		 * ��������Ƿ�ռ���ж���Ϸ�Ƿ��Ѿ������ˡ�
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
	 * ����������
	 */
	public static void main(String[] args) {
		new Tetris("����˹����");
	}
}
