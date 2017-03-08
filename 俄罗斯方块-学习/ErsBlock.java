/**
 * 块类，继承自线程类Thread，控制块的移动、下落、变形等
 */
class ErsBlock extends Thread {
	// 设定方块配置项
	public final static int BOXES_ROWS = 4; // 方块配置的行数
	public final static int BOXES_COLS = 4; // 方块配置的列数
	public final static int REDUCE_TIME = 50; // 每加一级所减少的时间
	private final static int BLOCK_NUM = 7; // 方块数目
	private final static int BLOCK_STATUS_NUM = 4; // 方块反转数目
	private GameCanvas canvas;
	private ErsBox[][] boxes = new ErsBox[BOXES_ROWS][BOXES_COLS];
	private int style, y, x, level;
	private boolean pausing = false, moving = true;

	/**
	 * 方块7种模型的28种状态，以16进制表示方块形状
	 */
	public final static int[][] STYLES = {
		{0x0f00, 0x4444, 0x0f00, 0x4444},  // 长条型的四种状态
		{0x04e0, 0x0464, 0x00e4, 0x04c4},  // 'T'型的四种状态
		{0x4620, 0x6c00, 0x4620, 0x6c00},  // 反'Z'型的四种状态
		{0x2640, 0xc600, 0x2640, 0xc600},  // 'Z'型的四种状态
		{0x6220, 0x1700, 0x2230, 0x0740},  // '7'型的四种状态
		{0x6440, 0x0e20, 0x44c0, 0x8e00},  // 反'7'型的四种状态
		{0x0660, 0x0660, 0x0660, 0x0660},  // 方块的四种状态
	};
	
	/**
	 * 构造函数，产生一个特定的块
	 */
	public ErsBlock(int style, int y, int x, int level, GameCanvas canvas) {
		this.style = style;
		this.y = y;
		this.x = x;
		this.level = level;
		this.canvas = canvas;
		// 方块染色
		int key = 0x8000;
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				boolean isColor = ((style & key) != 0); // 计算为方格染色
				boxes[i][j] = new ErsBox(isColor);
				key >>= 1;
			}
		}
		display();
	}

	/**
	 * 线程类的run()函数覆盖，下落块，直到块不能再下落
	 */
	public void run() {
		while (moving) {
			try {
				sleep(REDUCE_TIME
				        * (Tetris.MAX_LEVEL - level + 3));
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			//后边的moving是表示在等待的100毫秒间，moving没被改变
			if (!pausing) moving = (moveTo(y + 1, x) && moving);
		}
	}

	/**
	 * 方块移动
	 */
	public void moveLeft() {
		moveTo(y, x - 1);
	}
	public void moveRight() {
		moveTo(y, x + 1);
	}
	public void moveDown() {
		moveTo(y + 1, x);
	}
	// 方块反转
	public void turnNext() {
		for (int i = 0; i < BLOCK_NUM; i++) {
			for (int j = 0; j < BLOCK_STATUS_NUM; j++) {
				if (STYLES[i][j] == style) {
					int newStyle = STYLES[i][(j + 1) % BLOCK_STATUS_NUM];
					turnTo(newStyle);
					return;
				}
			}
		}
	}

	// 暂停块的下落，对应游戏暂停
	public void pauseMove() {
		pausing = true;
	}

	// 继续块的下落，对应游戏继续
	public void resumeMove() {
		pausing = false;
	}

	// 停止块的下落，对应游戏停止
	public void stopMove() {
		moving = false;
	}

	/**
	 * 将当前块从画布的对应位置移除，要等到下次重画画布时才能反映出来
	 */
	private void earse() {
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if (boxes[i][j].isColorBox()) {
					ErsBox box = canvas.getBox(i + y, j + x);
					if (box == null) continue;
					box.setColor(false); // 改为背景色
				}
			}
		}
	}

	/**
	 * 当前位置染色
	 */
	private void display() {
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if (boxes[i][j].isColorBox()) {
					ErsBox box = canvas.getBox(y + i, x + j);
					if (box == null) continue;
					box.setColor(true);
				}
			}
		}
	}

	/**
	 * 当前块能否移动到newRow/newCol所指定的位置
	 */
	private boolean isMoveAble(int newRow, int newCol) {
		earse(); // 移除染色
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if (boxes[i][j].isColorBox()) {
					ErsBox box = canvas.getBox(newRow + i, newCol + j);
					if (box == null || (box.isColorBox())) {
						display(); // 重新染色
						return false;
					}
				}
			}
		}
		display();
		return true;
	}

	/**
	 * 将当前画移动到newRow/newCol所指定的位置
	 */
	private synchronized boolean moveTo(int newRow, int newCol) {
		if (!isMoveAble(newRow, newCol) || !moving) return false;
		earse();
		y = newRow;
		x = newCol;
		display();
		canvas.repaint(); // 重绘组件，刷新！！
		return true;
	}

	/**
	 * 当前块能否变成newStyle所指定的块样式
	 * 主要是要考虑边界以及被其它块挡住、不能移动的情况
	 */
	private boolean isTurnAble(int newStyle) {
		int key = 0x8000;
		earse();
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if ((newStyle & key) != 0) {
					ErsBox box = canvas.getBox(y + i, x + j);
					if (box == null || box.isColorBox()) {
						display();
						return false;
					}
				}
				key >>= 1;
			}
		}
		display();
		return true;
	}

	/**
	 * 将当前块变成newStyle所指定的块样式
	 */
	private synchronized boolean turnTo(int newStyle) {
		if (!isTurnAble(newStyle) || !moving) return false;
		earse();
		int key = 0x8000;
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				boolean isColor = ((newStyle & key) != 0);
				boxes[i][j].setColor(isColor);
				key >>= 1;
			}
		}
		style = newStyle;
		display();
		canvas.repaint(); // 重绘组件，刷新画板！！
		return true;
	}
}
