import java.util.Random;

public class Destroyer {
	private int xCoordinate1, yCoordinate1, xCoordinate2, yCoordinate2, xCoordinate3, yCoordinate3, xCoordinate1Random,
			yCoordinate1Random, maxGridSize;
	Random rnd = new Random();

	public Destroyer(Grid[][] grids, int mode) {

		if (mode == 1) {
			maxGridSize = 8;
		} else if (mode == 2) {
			maxGridSize = 13;
		} else if (mode == 3) {
			maxGridSize = 18;
		}

		int randomValue = rnd.nextInt(2);

		xCoordinate1Random = rnd.nextInt(maxGridSize);
		yCoordinate1Random = rnd.nextInt(maxGridSize);

		// To be below or to the side, get a random x value
		if (randomValue == 1) {
			// If there is another ship at that point, get another random value
			while (xCoordinate1Random > maxGridSize - 1 || yCoordinate1Random > maxGridSize - 1
					|| grids[xCoordinate1Random][yCoordinate1Random].getValue() == 's'
					|| grids[xCoordinate1Random][yCoordinate1Random + 2].getValue() == 's'
					|| grids[xCoordinate1Random][yCoordinate1Random + 1].getValue() == 's') {
				xCoordinate1Random = rnd.nextInt(maxGridSize);
				yCoordinate1Random = rnd.nextInt(maxGridSize);
			}

			this.xCoordinate1 = xCoordinate1Random;
			this.yCoordinate1 = yCoordinate1Random;
			this.xCoordinate2 = xCoordinate1Random;
			this.yCoordinate2 = yCoordinate1Random + 1;
			this.xCoordinate3 = xCoordinate1Random;
			this.yCoordinate3 = yCoordinate1Random + 2;
		} else {
			while (xCoordinate1Random > maxGridSize - 1 || yCoordinate1Random > maxGridSize - 1
					|| grids[xCoordinate1Random][yCoordinate1Random].getValue() == 's'
					|| grids[xCoordinate1Random + 2][yCoordinate1Random].getValue() == 's'
					|| grids[xCoordinate1Random + 1][yCoordinate1Random].getValue() == 's') {
				xCoordinate1Random = rnd.nextInt(maxGridSize);
				yCoordinate1Random = rnd.nextInt(maxGridSize);
			}
			this.xCoordinate1 = xCoordinate1Random;
			this.yCoordinate1 = yCoordinate1Random;
			this.xCoordinate2 = xCoordinate1Random + 1;
			this.yCoordinate2 = yCoordinate1Random;
			this.xCoordinate3 = xCoordinate1Random + 2;
			this.yCoordinate3 = yCoordinate1Random;
		}

		grids[xCoordinate1][yCoordinate1].setValue('s');
		grids[xCoordinate2][yCoordinate2].setValue('s');
		grids[xCoordinate3][yCoordinate3].setValue('s');

	}

	public boolean allHit(Grid[][] grids) {
		if (grids[xCoordinate1][yCoordinate1].isHit() == true
				&& grids[xCoordinate2][yCoordinate2].isHit() == true
				&& grids[xCoordinate3][yCoordinate3].isHit() == true) {
			grids[xCoordinate1][yCoordinate1].setValue('x');
			grids[xCoordinate2][yCoordinate2].setValue('x');
			grids[xCoordinate3][yCoordinate3].setValue('x');
			System.out.println("You just sank a Destroyer");
			return true;
		}
		return false;
	}

}
