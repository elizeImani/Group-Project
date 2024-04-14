import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Battleship {

	public static Grid[][] grids = new Grid[20][20];

	public static void createGrid(int gridSize) { // initially creates grids with all cells empty
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				grids[i][j] = new Grid('-'); // empty cell representation
			}
		}
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		int level = -1;

		boolean loginMenu = true;
		boolean continueGame = true;
		String username = "";

		while (loginMenu) { // login menu
			System.out.println("GAME MENU");
			System.out.println("1.Login \n2.Create new user\n3.Exit");
			int userType = scanner.nextInt();

			if (userType == 1) {
				System.out.print("Enter Username:");
				username = scanner.next();
				level = readFromFile(username);
				if (level >= 0) {
					loginMenu = false; // exit from the menu
				} else {
					System.out.println("Wrong username. Try again.");
				}
			} else if (userType == 2) {
				System.out.print("New Username:");
				username = scanner.next();
				boolean registrationSuccessful = saveUserToFile(username);
				level = 0; // starting level for a new user
				if (registrationSuccessful) {
					loginMenu = false;
				}
			} else { // exit
				break;
			}
		}

		while (level >= 0 && level < 2) { // game easy mode
			createGrid(10);
			Boat boat = new Boat(grids, 1); // 1 = easy mode
			Destroyer destroyer = new Destroyer(grids, 1);
			Submarine submarine = new Submarine(grids, 1);

			int totalGunCountInLevel = 7, totalRocketCountInLevel = 1, totalBombCountInLevel = 2;
			int usedGunCount = 0, usedRocketCount = 0, usedBombCount = 0;
			int jMinusOne, iMinusOne, jPlusOne, iPlusOne;

			boolean allBoatsHit = false;
			boolean allSubmarinesHit = false;
			boolean allDestroyersHit = false;

			while (continueGame) { // level

				displayGameBoard(10);

				if (usedRocketCount >= totalRocketCountInLevel
						&& usedGunCount >= totalGunCountInLevel
						&& usedBombCount >= totalBombCountInLevel) { // game over if no shots left

					if (!allBoatsHit || !allSubmarinesHit || !allDestroyersHit) {
						continueGame = false;

						System.out.println("No shot left. Game over");

						saveToFile(username, level);
						level = -1;

						break;
					}
				}
				System.out.println("0. Gun Shot: " + (totalGunCountInLevel - usedGunCount));
				System.out.println("1. Hand Bomb: " + (totalBombCountInLevel - usedBombCount));
				System.out.println("2. Rocket: " + (totalRocketCountInLevel - usedRocketCount));
				System.out.print("Weapon and x,y coordinate:");
				int weaponName = scanner.nextInt();
				int iCoordinate = scanner.nextInt();
				int jCoordinate = scanner.nextInt();

				if (jCoordinate > 0) {
					jMinusOne = jCoordinate - 1;
				} else {
					jMinusOne = jCoordinate;
				}
				if (jCoordinate < 9) {
					jPlusOne = jCoordinate + 1;
				} else {
					jPlusOne = jCoordinate;
				}
				if (iCoordinate > 0) {
					iMinusOne = iCoordinate - 1;
				} else {
					iMinusOne = iCoordinate;
				}
				if (iCoordinate < 9) {
					iPlusOne = iCoordinate + 1;
				} else {
					iPlusOne = iCoordinate;
				}
				if (weaponName == 0) { // gun

					if (usedGunCount < totalGunCountInLevel) { // check if there are guns available
						grids[iCoordinate][jCoordinate].setHit(true);
						usedGunCount++;

						if (grids[iCoordinate][jCoordinate].getValue() == 's') {
							System.out.println("Hit!");
						} else {
							System.out.println("Invalid Hit");
						}
					} else {
						System.out.println("Gun is over.");
					}

				} else if (weaponName == 1) { // bomb
					if (usedBombCount < totalBombCountInLevel) {
						grids[iCoordinate][jMinusOne].setHit(true);
						grids[iCoordinate][jCoordinate].setHit(true);
						grids[iCoordinate][jPlusOne].setHit(true);
						usedBombCount++;

						if (grids[iCoordinate][jCoordinate].getValue() == 's'
								|| grids[iCoordinate][jMinusOne].getValue() == 's'
								|| grids[iCoordinate][jPlusOne].getValue() == 's') {
							System.out.println("Hit!");
						} else {
							System.out.println("Invalid Hit");
						}
					} else {
						System.out.println("The bomb is over.");
					}

				} else if (weaponName == 2) { // rocket
					if (usedRocketCount < totalRocketCountInLevel) {
						grids[iCoordinate][jCoordinate].setHit(true);
						grids[iCoordinate][jMinusOne].setHit(true);
						grids[iCoordinate][jPlusOne].setHit(true);
						grids[iPlusOne][jCoordinate].setHit(true);
						grids[iMinusOne][jCoordinate].setHit(true);

						usedRocketCount++;

						if (grids[iCoordinate][jCoordinate].getValue() == 's'
								|| grids[iCoordinate][jMinusOne].getValue() == 's'
								|| grids[iCoordinate][jPlusOne].getValue() == 's'
								|| grids[iPlusOne][jCoordinate].getValue() == 's'
								|| grids[iMinusOne][jCoordinate].getValue() == 's') {
							System.out.println("Hit!");
						} else {
							System.out.println("Invalid Hit");
						}
					} else {
						System.out.println("The Rocket is over.");
					}
				}

				if (!allBoatsHit) {
					allBoatsHit = boat.allHit(grids); // check if all boats are hit
				}
				if (!allSubmarinesHit) {
					allSubmarinesHit = submarine.allHit(grids);
				}
				if (!allDestroyersHit) {
					allDestroyersHit = destroyer.allHit(grids);
				}

				if (allBoatsHit && allSubmarinesHit && allDestroyersHit) {
					System.out.println("Congratulations. The level has increased.");
					displayGameBoard(10);
					level++;
					saveToFile(username, level);
					System.out.println("c.Continue level \nq.exit :");
					String continueOption = scanner.next();
					if (continueOption.equals("q")) {
						level = -1;
					}
					break;
				}

			}

		}
		while (level >= 2 && level < 5) {// normal mode
			createGrid(15);
			Boat boat = new Boat(grids, 2);
			Boat boat2 = new Boat(grids, 2);
			Destroyer destroyer = new Destroyer(grids, 2);
			Submarine submarine = new Submarine(grids, 2);
			Battleship battleship = new Battleship(grids, 2);
			int totalGunCountInLevel = 10, totalRocketCountInLevel = 1, totalBombCountInLevel = 3;
			int usedGunCount = 0, usedRocketCount = 0, usedBombCount = 0;

			int jMinusOne, iMinusOne, jPlusOne, iPlusOne;

			boolean allBoatsHit = false;
			boolean allBoats2Hit = false;
			boolean allSubmarinesHit = false;
			boolean allDestroyersHit = false;
			boolean allBattleshipsHit = false;

			while (continueGame) {

				displayGameBoard(15);

				if (usedRocketCount >= totalRocketCountInLevel
						&& usedGunCount >= totalGunCountInLevel
						&& usedBombCount >= totalBombCountInLevel) {// game over

					if (!allBoatsHit || !allBoats2Hit || !allSubmarinesHit
							|| !allDestroyersHit || !allBattleshipsHit) {

						continueGame = false;
						System.out.println("No shot left. Game over");

						saveToFile(username, level);// save to file
						level = -1;// game over
						break;
					}
				}
				System.out.println("0.Gun Shot: " + (totalGunCountInLevel - usedGunCount));
				System.out.println("1.Hand Bomb: " + (totalBombCountInLevel - usedBombCount));
				System.out.println("2.Rocket: " + (totalRocketCountInLevel - usedRocketCount));
				System.out.print("Weapon and x,y coordinate:");
				int weaponName = scanner.nextInt();
				int iCoordinate = scanner.nextInt();
				int jCoordinate = scanner.nextInt();

				if (jCoordinate > 0) {
					jMinusOne = jCoordinate - 1;
				} else {
					jMinusOne = jCoordinate;
				}
				if (jCoordinate < 14) {
					jPlusOne = jCoordinate + 1;
				} else {
					jPlusOne = jCoordinate;
				}
				if (iCoordinate > 0) {
					iMinusOne = iCoordinate - 1;
				} else {
					iMinusOne = iCoordinate;
				}
				if (iCoordinate < 14) {
					iPlusOne = iCoordinate + 1;
				} else {
					iPlusOne = iCoordinate;
				}

				if (weaponName == 0) {
					if (usedGunCount < totalGunCountInLevel) {
						grids[iCoordinate][jCoordinate].setHit(true);
						usedGunCount++;

						if (grids[iCoordinate][jCoordinate].getValue() == 's') {
							System.out.println("Hit!");
						} else {
							System.out.println("Invalid Hit");
						}
					} else {
						System.out.println("The Gun is over..");
					}
				} else if (weaponName == 1) {
					if (usedBombCount < totalBombCountInLevel) {
						grids[iCoordinate][jMinusOne].setHit(true);
						grids[iCoordinate][jCoordinate].setHit(true);
						grids[iCoordinate][jPlusOne].setHit(true);
						usedBombCount++;

						if (grids[iCoordinate][jCoordinate].getValue() == 's'
								|| grids[iCoordinate][jMinusOne].getValue() == 's'
								|| grids[iCoordinate][jPlusOne].getValue() == 's') {
							System.out.println("Hit!");
						} else {
							System.out.println("Invalid Hit");
						}
					} else {
						System.out.println("The bomb is over.");
					}

				} else if (weaponName == 2) {
					if (usedRocketCount < totalRocketCountInLevel) {
						grids[iCoordinate][jCoordinate].setHit(true);
						grids[iCoordinate][jMinusOne].setHit(true);
						grids[iCoordinate][jPlusOne].setHit(true);
						grids[iPlusOne][jCoordinate].setHit(true);
						grids[iMinusOne][jCoordinate].setHit(true);

						usedRocketCount++;

						if (grids[iCoordinate][jCoordinate].getValue() == 's'
								|| grids[iCoordinate][jMinusOne].getValue() == 's'
								|| grids[iCoordinate][jPlusOne].getValue() == 's'
								|| grids[iPlusOne][jCoordinate].getValue() == 's'
								|| grids[iMinusOne][jCoordinate].getValue() == 's') {
							System.out.println("Hit!");
						} else {
							System.out.println("Invalid Hit");
						}
					} else {
						System.out.println("The Rocket is over.");
					}
				}
				if (boatHepsiVuruldu == false) {
					boatHepsiVuruldu = boat.allHit(gridler);// If boat is hit, mark as 'x'.
				}
				if (boat2HepsiVuruldu == false) {
					boat2HepsiVuruldu = boat2.allHit(gridler);// If boat is hit, mark as 'x'.
				}
				if (submarineHepsiVuruldu == false) {
					submarineHepsiVuruldu = sub.allHit(gridler);
				}
				if (destroyerHepsiVuruldu == false) {
					destroyerHepsiVuruldu = des.allHit(gridler);
				}
				if (battleshipHepsiVuruldu == false) {
					battleshipHepsiVuruldu = bir.allHit(gridler);
				}
				if (boatHepsiVuruldu == true && boat2HepsiVuruldu == true && submarineHepsiVuruldu == true
						&& destroyerHepsiVuruldu == true && battleshipHepsiVuruldu == true) {

					System.out.println("Congratulations. the level has increased.");
					displayGameBoard(15);
					level = level + 1;

					saveToFile(username, level);// Save
					System.out.println("c.Continue level \nq.exit :");
					String continueOption = scanner.next();
					if (continueOption.equals("q")) {
						level = -1;
					}
					break;
				}
			}

		}
		while (level >= 5) { // hard level
			createGrid(20);
			Boat boat = new Boat(gridler, 3);
			Boat boat2 = new Boat(gridler, 3);
			Destroyer destroyer = new Destroyer(gridler, 3);
			Submarine submarine = new Submarine(gridler, 3);
			Battleship battleship = new Battleship(gridler, 3);

			int totalGunCountInLevel = 12, totalRocketCountInLevel = 1, totalBombCountInLevel = 4;
			int usedGunCount = 0, usedRocketCount = 0, usedBombCount = 0;
			int jMinusOne, iMinusOne, jPlusOne, iPlusOne;

			boolean allBoatsHit = false;
			boolean allBoats2Hit = false;
			boolean allSubmarinesHit = false;
			boolean allDestroyersHit = false;
			boolean allBattleshipsHit = false;

			while (continueGame) {

				displayGameBoard(20);

				if (usedRocketCount >= totalRocketCountInLevel
						&& usedGunCount >= totalGunCountInLevel
						&& usedBombCount >= totalBombCountInLevel) {// game over

					if (!allBoatsHit || !allBoats2Hit || !allSubmarinesHit
							|| !allDestroyersHit || !allBattleshipsHit) {

						continueGame = false;
						System.out.println("No shot left. Game over");

						saveToFile(username, level);// save to file
						level = -1;// game over
						break;
					}
				}

				System.out.println("0.Gun Shot: " + (totalGunCountInLevel - usedGunCount));
				System.out.println("1.Hand Bomb: " + (totalBombCountInLevel - usedBombCount));
				System.out.println("2.Rocket: " + (totalRocketCountInLevel - usedRocketCount));
				System.out.print("Weapon and x,y coordinate:");
// Receive input for weapon type, i-coordinate, and j-coordinate
				int weaponType = scanner.nextInt();
				int iCoordinate = scanner.nextInt();
				int jCoordinate = scanner.nextInt();

// Adjust neighboring coordinates
				int jMinusOne, jPlusOne, iMinusOne, iPlusOne;
				if (jCoordinate > 0) {
					jMinusOne = jCoordinate - 1;
				} else {
					jMinusOne = jCoordinate;
				}
				if (jCoordinate < 9) {
					jPlusOne = jCoordinate + 1;
				} else {
					jPlusOne = jCoordinate;
				}
				if (iCoordinate > 0) {
					iMinusOne = iCoordinate - 1;
				} else {
					iMinusOne = iCoordinate;
				}
				if (iCoordinate < 9) {
					iPlusOne = iCoordinate + 1;
				} else {
					iPlusOne = iCoordinate;
				}

// Handle different weapon types
				if (weaponType == 0) {
					// Gun shot
					if (usedGunCount < totalGunCountInLevel) {
						gridArray[iCoordinate][jCoordinate].setHit(true);
						usedGunCount++;
						if (gridArray[iCoordinate][jCoordinate].getValue() == 's') {
							System.out.println("Hit!");
						} else {
							System.out.println("Invalid Hit");
						}
					} else {
						System.out.println("The Gun is over..");
					}
				} else if (weaponType == 1) {
					// Hand bomb
					if (usedBombCount < totalBombCountInLevel) {
						gridArray[iCoordinate][jMinusOne].setHit(true);
						gridArray[iCoordinate][jCoordinate].setHit(true);
						gridArray[iCoordinate][jPlusOne].setHit(true);
						usedBombCount++;
						if (gridArray[iCoordinate][jCoordinate].getValue() == 's'
								|| gridArray[iCoordinate][jMinusOne].getValue() == 's'
								|| gridArray[iCoordinate][jPlusOne].getValue() == 's') {
							System.out.println("Hit!");
						} else {
							System.out.println("Invalid Hit");
						}
					} else {
						System.out.println("No bomb left");
					}
				} else if (weaponType == 2) {
					// Rocket
					if (usedRocketCount < totalRocketCountInLevel) {
						gridArray[iCoordinate][jCoordinate].setHit(true);
						gridArray[iCoordinate][jMinusOne].setHit(true);
						gridArray[iCoordinate][jPlusOne].setHit(true);
						gridArray[iMinusOne][jCoordinate].setHit(true);
						gridArray[iPlusOne][jCoordinate].setHit(true);

						usedRocketCount++;

						if (gridArray[iCoordinate][jCoordinate].getValue() == 's'
								|| gridArray[iCoordinate][jMinusOne].getValue() == 's'
								|| gridArray[iCoordinate][jPlusOne].getValue() == 's'
								|| gridArray[iMinusOne][jCoordinate].getValue() == 's'
								|| gridArray[iPlusOne][jCoordinate].getValue() == 's') {
							System.out.println("Hit!");
						} else {
							System.out.println("Invalid Hit");
						}
					} else {
						System.out.println("The Rocket is over.");
					}
				}

// Check if all boats are hit
				if (!allBoatsHit) {
					allBoatsHit = boat.allDestroyed(gridArray);
				}

// Check if all submarines are hit
				if (!allSubmarinesHit) {
					allSubmarinesHit = submarine.allDestroyed(gridArray);
				}

// Check if all destroyers are hit
				if (!allDestroyersHit) {
					allDestroyersHit = destroyer.allDestroyed(gridArray);
				}

// Check if all targets are hit to advance to the next level
				if (allBoatsHit && allSubmarinesHit && allDestroyersHit) {
					System.out.println("Congratulations. the level has increased.");
					displayGameBoard(20);
					level++;
					saveToFile(username, level);
					System.out.println("c.Continue level \nq.exit :");
					String continueOption = scanner.next();
					if (continueOption.equals("q")) {
						level = -1;
					}
					break;
				}
				public static void saveToFile (String username,int level){
					// Initialize variables
					int levelToWrite = -1;
					String mode = "Error";

					// Define the file path
					Path path = Paths.get("", "AmiralBattiInfo.txt");

					// Define the character set
					Charset charset = Charset.forName("UTF-8");

					try {
						// Read all lines from the file
						List<String> lines = Files.readAllLines(path, charset);

						// Determine the mode and level to write based on the current level
						if (level >= 0 && level < 2) {
							mode = "Easy";
							levelToWrite = level;
						} else if (level >= 2 && level < 5) {
							mode = "Normal";
							levelToWrite = (level % 5) - 2;
						} else if (level >= 5) {
							mode = "Hard";
							levelToWrite = (level % 5);
						}

						// Update the appropriate line with the new username, mode, and level
						for (int i = 0; i < lines.size(); i++) {
							String[] array = lines.get(i).split(" ");
							if (array[0].equals(username)) {
								System.out.println("Saving...");
								lines.set(i, username + " " + mode + "(" + levelToWrite + ")");
								break;
							}
						}

						// Write the updated lines back to the file
						Path file = Paths.get("AmiralBattiInfo.txt");
						Files.write(file, lines, Charset.forName("UTF-8"));
					} catch (IOException e) {
						System.out.println(e);
					}
				}
				public static void saveToFile (String username,int level){// saving to file

					int levelWriting = -1;

					String mode = "Error";

					Path path = Paths.get("", "AmiralBattiInfo.txt");

					Charset charset = Charset.forName("UTF-8");
					try {
						List<String> lines = Files.readAllLines(path, charset);

						/*
						 * for (String line : lines) { System.out.println(line);
						 *
						 * }
						 */
						if (level >= 0 && level < 2) {
							mode = "Easy";
							levelWriting = level;
						} else if (level >= 2 && level < 5) {
							mode = "Normal";
							levelWriting = (level % 5) - 2;
						} else if (level >= 5) {
							mode = "Hard";
							levelWriting = (level % 5);
						}

						for (int i = 0; i < lines.size(); i++) {
							String[] array = lines.get(i).split(" ");
							if (array[0].equals(username)) {

								System.out.println("Saving...");

								lines.set(i, username + " " + mode + "(" + levelWriting + ")");
								break;
							}
						}
						Path file = Paths.get("AmiralBattiInfo.txt");

						Files.write(file, lines, Charset.forName("UTF-8"));
					} catch (IOException e) {
						System.out.println(e);
					}

				}
				public static boolean saveNewUserToFile (String newUsername){// creating a new user

					if (readFromFile(newUsername) != -1) {
						System.out.println("Already exists");
						return false;
					}
					int level = 0;
					String mode = "Easy";
					FileWriter writer = null;

					try {
						writer = new FileWriter("AmiralBattiInfo.txt", true);

						writer.write(newUsername + " " + mode + "(");
						if (level > 9) {
							System.out.println("level cannot be greater than 9.");
						}

						writer.write(Integer.toString(level) + ")\n");

					} catch (IOException ex) {
						System.out.println("IOException occurred while opening the file...");
					} finally {

						if (writer != null) {
							try {
								writer.close();
							} catch (IOException ex) {
								System.out.println("An error occurred while closing the file...");
							}

						}
					}
					return true;
				}

				public static void printGameBoard ( int size){// printing to the screen
					System.out.print("   ");
					for (int i = 0; i < size; i++) {
						if (i < 9) {
							System.out.print(i + "  ");
						} else {
							System.out.print(i + " ");
						}
					}
					System.out.println("");
					for (int i = 0; i < size; i++) {
						if (i < 10) {
							System.out.print(i + "  ");
						} else {
							System.out.print(i + " ");
						}

						for (int j = 0; j < size; j++) {
							// gridler[i][j].isVuruldu()==
							if (gridler[i][j].isHit() == true) {

								System.out.print(gridler[i][j].getValue() + "  ");

							} else {
								System.out.print(".  ");

							}
						}
						System.out.println("");
					}
				}
			}
		}
	}
}