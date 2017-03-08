import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * 画布类，继承自JPanel类。
 * ErsBlock线程类动态改变画布类的方格颜色，画布类通过检查方格颜色来体现ErsBlock块的移动情况。
 */
class GameCanvas extends JPanel {
	private Color backColor = Color.black, frontColor = Color.red;
	private int rows, cols, score = 0, scoreForLevelUpdate = 0;
	private ErsBox[][] boxes;
	private int boxWidth, boxHeight;

	// 构造函数
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
	 * 覆盖JComponent类的函数，画布。
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
	// 取得画布中方格的行数
	public int getRows() {
		return rows;
	}
	public int getCols() {
		return cols;
	}
	// 取得游戏成绩
	public int getScore() {
		return score;
	}
	// 取得自上一次升级后的积分
	public int getScoreForLevelUpdate() {
		return scoreForLevelUpdate;
	}

	// 升级后，将上一次升级以来的积分清0
	public void resetScoreForLevelUpdate() {
		scoreForLevelUpdate -= Tetris.PER_LEVEL_SCORE;
	}

	// 得到某一行某一列的方格引用。
	public ErsBox getBox(int row, int col) {
		if (row < 0 || row > boxes.length - 1 || col < 0 || col > boxes[0].length - 1)  return null;
		return (boxes[row][col]);
	}
	// 根据窗口的大小，自动调整方格的尺寸
	public void fanning() {
		boxWidth = getSize().width / cols;
		boxHeight = getSize().height / rows;
	}

	// 将此行清除，并为游戏者加分
	public synchronized void removeLine(int row) {
		for (int i = row; i > 0; i--) {
			for (int j = 0; j < cols; j++)
				boxes[i][j] = (ErsBox) boxes[i - 1][j].clone();
		}
		score += Tetris.PER_LINE_SCORE;
		scoreForLevelUpdate += Tetris.PER_LINE_SCORE;
		repaint();
	}

	// 重置画布，置积分为0
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
