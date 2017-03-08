
import java.awt.*;

/**
 * 方格类，是组成块的基本元素，用自己的颜色来表示块的外观
 */
class ErsBox implements Cloneable {
	private boolean isColor;
	private Dimension size = new Dimension();

	// 方格类的构造函数，false用背景色
	public ErsBox(boolean isColor) {
		this.isColor = isColor;
	}
	// 此方格是不是用前景色表现
	public boolean isColorBox() {
		return isColor;
	}
	// 设置方格的颜色
	public void setColor(boolean isColor) {
		this.isColor = isColor;
	}
	// 得到此方格的尺寸
	public Dimension getSize() {
		return size;
	}
	// 设置方格的尺寸
	public void setSize(Dimension size) {
		this.size = size;
	}
	// 覆盖Object的Object clone()，实现克隆
	public Object clone() {
		Object cloned = null;
		try {
			cloned = super.clone();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return cloned;
	}
}
