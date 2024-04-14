import java.util.Random;

public class Boat {

	private int xCoordinate1, yCoordinate1, maxGridSize;

	Random rnd = new Random();

	public Boat(Grid[][] grids, int mode) {
		if (mode == 1) {
			maxGridSize = 10;
		} else if (mode == 2) {
			maxGridSize = 15;
		} else if (mode == 3) {
			maxGridSize = 20;
		}

		this.xCoordinate1 = rnd.nextInt(maxGridSize);// 9 14 19
		this.yCoordinate1 = rnd.nextInt(maxGridSize);
		if (grids[xCoordinate1][yCoordinate1].getValue() == 's') {
			this.xCoordinate1 = rnd.nextInt(maxGridSize);
			this.yCoordinate1 = rnd.nextInt(maxGridSize);
		}

		grids[xCoordinate1][yCoordinate1].setValue('s');

	}

	public boolean allHit(Grid[][] grids) {
		if (grids[xCoordinate1][yCoordinate1].isHit() == true
				&& grids[xCoordinate1][yCoordinate1].getValue() == 's') {
			System.out.println("You just sank a Boat");

			grids[xCoordinate1][yCoordinate1].setValue('x');

			return true;
		}
		return false;
	}

}
