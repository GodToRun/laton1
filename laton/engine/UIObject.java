package laton.engine;

public class UIObject {
	int x, y;
	float size;
	public boolean renderd = true;
	public UIObject(Canvas canvas, int x, int y, float size) {
		canvas.uis.add(this);
		this.x = x;
		this.y = y;
		this.size = size;
	}
	public void render() {
		if (!renderd) return;
	}
}
