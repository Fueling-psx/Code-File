/**
 * ���࣬�̳����߳���Thread�����ƿ���ƶ������䡢���ε�
 */
class ErsBlock extends Thread {
	// �趨����������
	public final static int BOXES_ROWS = 4; // �������õ�����
	public final static int BOXES_COLS = 4; // �������õ�����
	public final static int REDUCE_TIME = 50; // ÿ��һ�������ٵ�ʱ��
	private final static int BLOCK_NUM = 7; // ������Ŀ
	private final static int BLOCK_STATUS_NUM = 4; // ���鷴ת��Ŀ
	private GameCanvas canvas;
	private ErsBox[][] boxes = new ErsBox[BOXES_ROWS][BOXES_COLS];
	private int style, y, x, level;
	private boolean pausing = false, moving = true;

	/**
	 * ����7��ģ�͵�28��״̬����16���Ʊ�ʾ������״
	 */
	public final static int[][] STYLES = {
		{0x0f00, 0x4444, 0x0f00, 0x4444},  // �����͵�����״̬
		{0x04e0, 0x0464, 0x00e4, 0x04c4},  // 'T'�͵�����״̬
		{0x4620, 0x6c00, 0x4620, 0x6c00},  // ��'Z'�͵�����״̬
		{0x2640, 0xc600, 0x2640, 0xc600},  // 'Z'�͵�����״̬
		{0x6220, 0x1700, 0x2230, 0x0740},  // '7'�͵�����״̬
		{0x6440, 0x0e20, 0x44c0, 0x8e00},  // ��'7'�͵�����״̬
		{0x0660, 0x0660, 0x0660, 0x0660},  // ���������״̬
	};
	
	/**
	 * ���캯��������һ���ض��Ŀ�
	 */
	public ErsBlock(int style, int y, int x, int level, GameCanvas canvas) {
		this.style = style;
		this.y = y;
		this.x = x;
		this.level = level;
		this.canvas = canvas;
		// ����Ⱦɫ
		int key = 0x8000;
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				boolean isColor = ((style & key) != 0); // ����Ϊ����Ⱦɫ
				boxes[i][j] = new ErsBox(isColor);
				key >>= 1;
			}
		}
		display();
	}

	/**
	 * �߳����run()�������ǣ�����飬ֱ���鲻��������
	 */
	public void run() {
		while (moving) {
			try {
				sleep(REDUCE_TIME
				        * (Tetris.MAX_LEVEL - level + 3));
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			//��ߵ�moving�Ǳ�ʾ�ڵȴ���100����䣬movingû���ı�
			if (!pausing) moving = (moveTo(y + 1, x) && moving);
		}
	}

	/**
	 * �����ƶ�
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
	// ���鷴ת
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

	// ��ͣ������䣬��Ӧ��Ϸ��ͣ
	public void pauseMove() {
		pausing = true;
	}

	// ����������䣬��Ӧ��Ϸ����
	public void resumeMove() {
		pausing = false;
	}

	// ֹͣ������䣬��Ӧ��Ϸֹͣ
	public void stopMove() {
		moving = false;
	}

	/**
	 * ����ǰ��ӻ����Ķ�Ӧλ���Ƴ���Ҫ�ȵ��´��ػ�����ʱ���ܷ�ӳ����
	 */
	private void earse() {
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if (boxes[i][j].isColorBox()) {
					ErsBox box = canvas.getBox(i + y, j + x);
					if (box == null) continue;
					box.setColor(false); // ��Ϊ����ɫ
				}
			}
		}
	}

	/**
	 * ��ǰλ��Ⱦɫ
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
	 * ��ǰ���ܷ��ƶ���newRow/newCol��ָ����λ��
	 */
	private boolean isMoveAble(int newRow, int newCol) {
		earse(); // �Ƴ�Ⱦɫ
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if (boxes[i][j].isColorBox()) {
					ErsBox box = canvas.getBox(newRow + i, newCol + j);
					if (box == null || (box.isColorBox())) {
						display(); // ����Ⱦɫ
						return false;
					}
				}
			}
		}
		display();
		return true;
	}

	/**
	 * ����ǰ���ƶ���newRow/newCol��ָ����λ��
	 */
	private synchronized boolean moveTo(int newRow, int newCol) {
		if (!isMoveAble(newRow, newCol) || !moving) return false;
		earse();
		y = newRow;
		x = newCol;
		display();
		canvas.repaint(); // �ػ������ˢ�£���
		return true;
	}

	/**
	 * ��ǰ���ܷ���newStyle��ָ���Ŀ���ʽ
	 * ��Ҫ��Ҫ���Ǳ߽��Լ��������鵲ס�������ƶ������
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
	 * ����ǰ����newStyle��ָ���Ŀ���ʽ
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
		canvas.repaint(); // �ػ������ˢ�»��壡��
		return true;
	}
}
