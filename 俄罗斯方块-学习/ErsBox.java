
import java.awt.*;

/**
 * �����࣬����ɿ�Ļ���Ԫ�أ����Լ�����ɫ����ʾ������
 */
class ErsBox implements Cloneable {
	private boolean isColor;
	private Dimension size = new Dimension();

	// ������Ĺ��캯����false�ñ���ɫ
	public ErsBox(boolean isColor) {
		this.isColor = isColor;
	}
	// �˷����ǲ�����ǰ��ɫ����
	public boolean isColorBox() {
		return isColor;
	}
	// ���÷������ɫ
	public void setColor(boolean isColor) {
		this.isColor = isColor;
	}
	// �õ��˷���ĳߴ�
	public Dimension getSize() {
		return size;
	}
	// ���÷���ĳߴ�
	public void setSize(Dimension size) {
		this.size = size;
	}
	// ����Object��Object clone()��ʵ�ֿ�¡
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
