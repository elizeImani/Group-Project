import java.util.Random;

public class Submarine {

	private int xCoordinate1, yCoordinate1, xCoordinate2, yCoordinate2, xRandom, yRandom, maxGridSize;

	Random rnd = new Random();

	public Submarine(Grid[][] grids, int mode) {

		if (mode == 1) {
			maxGridSize = 9;
		} else if (mode == 2) {
			maxGridSize = 14;
		} else if (mode == 3) {
			maxGridSize = 19;
		}

		int randomValue = rnd.nextInt(2); // 0 - 1

		// Randomize x-coordinate
		xRandom = rnd.nextInt(maxGridSize);
		yRandom = rnd.nextInt(maxGridSize);

		// Determine orientation (vertical or horizontal)
		if (randomValue == 1) { // Horizontal
			// Check if there's another ship or out of bounds
			while (xRandom > maxGridSize - 1 || yRandom > maxGridSize - 1
					|| grids[xRandom][yRandom].getValue() == 's'
					|| grids[xRandom][yRandom + 1].getValue() == 's') {
				xRandom = rnd.nextInt(maxGridSize);
				yRandom = rnd.nextInt(maxGridSize);
			}

			// Assign coordinates
			this.xCoordinate1 = xRandom;
			this.yCoordinate1 = yRandom;
			this.xCoordinate2 = xRandom;
			this.yCoordinate2 = yRandom + 1;
		} else { // Vertical
			// Check if there's another ship or out of bounds
			while (xRandom > maxGridSize - 1 || yRandom > maxGridSize - 1
					|| grids[xRandom][yRandom].getValue() == 's'
					|| grids[xRandom + 1][yRandom].getValue() == 's') {
				xRandom = rnd.nextInt(maxGridSize);
				yRandom = rnd.nextInt(maxGridSize);
			}

			// Assign coordinates
			this.xCoordinate1 = xRandom;
			this.yCoordinate1 = yRandom;
			this.xCoordinate2 = xRandom + 1;
			this.yCoordinate2 = yRandom;
		}

		// Set ship values on the grid
		grids[xCoordinate1][yCoordinate1].setValue('s');
		grids[xCoordinate2][yCoordinate2].setValue('s');
	}

	public boolean allHit(Grid[][] grids) {
		if (grids[xCoordinate1][yCoordinate1].isHit() && grids[xCoordinate2][yCoordinate2].isHit()) {
			// Mark as sunk
			grids[xCoordinate1][yCoordinate1].setValue('x');
			grids[xCoordinate2][yCoordinate2].setValue('x');
			System.out.println("You just sank a Submarine");
			return true;
		}
		return false;
	}
}
