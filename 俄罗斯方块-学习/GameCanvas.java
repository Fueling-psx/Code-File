import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * �����࣬�̳���JPanel�ࡣ
 * ErsBlock�߳��ද̬�ı仭����ķ�����ɫ��������ͨ����鷽����ɫ������ErsBlock����ƶ������
 */
class GameCanvas extends JPanel {
	private Color backColor = Color.black, frontColor = Color.red;
	private int rows, cols, score = 0, scoreForLevelUpdate = 0;
	private ErsBox[][] boxes;
	private int boxWidth, boxHeight;

	// ���캯��
	public GameCanvas(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		boxes = new ErsBox[rows][cols];
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				boxes[i][j] = new ErsBox(false);
			}
		}
	}

	/**
	 * ����JComponent��ĺ�����������
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(frontColor);
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				g.setColor(boxes[i][j].isColorBox() ? frontColor : backColor);
				g.fill3DRect(j * boxWidth, i * boxHeight,
				        boxWidth, boxHeight, true);
			}
		}
	}
	// ȡ�û����з��������
	public int getRows() {
		return rows;
	}
	public int getCols() {
		return cols;
	}
	// ȡ����Ϸ�ɼ�
	public int getScore() {
		return score;
	}
	// ȡ������һ��������Ļ���
	public int getScoreForLevelUpdate() {
		return scoreForLevelUpdate;
	}

	// �����󣬽���һ�����������Ļ�����0
	public void resetScoreForLevelUpdate() {
		scoreForLevelUpdate -= Tetris.PER_LEVEL_SCORE;
	}

	// �õ�ĳһ��ĳһ�еķ������á�
	public ErsBox getBox(int row, int col) {
		if (row < 0 || row > boxes.length - 1 || col < 0 || col > boxes[0].length - 1)  return null;
		return (boxes[row][col]);
	}
	// ���ݴ��ڵĴ�С���Զ���������ĳߴ�
	public void fanning() {
		boxWidth = getSize().width / cols;
		boxHeight = getSize().height / rows;
	}

	// �������������Ϊ��Ϸ�߼ӷ�
	public synchronized void removeLine(int row) {
		for (int i = row; i > 0; i--) {
			for (int j = 0; j < cols; j++)
				boxes[i][j] = (ErsBox) boxes[i - 1][j].clone();
		}
		score += Tetris.PER_LINE_SCORE;
		scoreForLevelUpdate += Tetris.PER_LINE_SCORE;
		repaint();
	}

	// ���û������û���Ϊ0
	public void reset() {
		score = 0;
		scoreForLevelUpdate = 0;
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++)
				boxes[i][j].setColor(false);
		}

		repaint();
	}
}
