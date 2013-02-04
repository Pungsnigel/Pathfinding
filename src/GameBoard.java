import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;




public class GameBoard extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4800212409516801927L;
	private int rows,columns;
	JPanel gridPanel;
	private final int SLEEPTIME = 100;
	
	
	private Tiles start,stop;
	private Tiles [][] gameBoardState;
	
	
	// Approx value for square root of 2. 
	private final double DIAGONALCOST= 1.41421356;
	// This cost is added to the G-value (path-length) of a tile, if the current path would change direction to reach it.
	// The cost is small enough that it won't chose a longer path over a shorter, but will ensure that out of a
	// number of paths of the same length, the one with the fewest turns will be chosen.
	private final double TURNCOST = 0.0002;

	public GameBoard(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;

		setLayout(new BorderLayout());
		
		// Initiates and specifies the panel holding the gameboard of tiles
		gridPanel = new JPanel();
		gridPanel.setPreferredSize(new Dimension(rows * 31, columns * 31));
		gridPanel.setLayout(new GridLayout(rows, columns));
		this.add(gridPanel, BorderLayout.CENTER);

		// Create panel for controls
		JPanel controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(rows * 31, 30));
		gridPanel.setLayout(new GridLayout(rows, columns));
		this.add(controlPanel, BorderLayout.SOUTH);	
		

		// Create and place control buttons
		JButton findPath = new JButton("Find Path");
		findPath.setActionCommand("findPath");
		findPath.addActionListener(this);
		controlPanel.add(findPath);

		JButton reset = new JButton("Reset");
		reset.setActionCommand("reset");
		reset.addActionListener(this);
		controlPanel.add(reset);

		JButton exit = new JButton("Exit");
		exit.setActionCommand("exit");
		exit.addActionListener(this);
		controlPanel.add(exit);
		
		// Initiate matrix for storing buttons
		gameBoardState = new Tiles[rows][columns];
		createButtons();

		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.pack();	
	}
	
	private void createButtons() {
		for (int i = 0; i < rows; i++) {
			for (int l = 0; l < columns; l++) {
				gameBoardState[i][l] = new Tiles(this, i, l);
				gameBoardState[i][l].setActionCommand(i + "," + l);
				gridPanel.add(gameBoardState[i][l]);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("exit")) {
			System.exit(0);
		}	
		
		else if (e.getActionCommand().equals("reset")) {
			// Set all tiles to regular white
			for (int i = 0; i < rows; i++) {
				for (int l = 0; l < columns; l++) {
					gameBoardState[i][l].setBackground(Color.WHITE);
					gameBoardState[i][l].state = TileState.REGULAR;
					gameBoardState[i][l].setText(null);
				}
			} // Since all tiles are now regular, start and stop won't be on the gameboard.
			this.start = this.stop = null;
			return;
		}
		
		else if (e.getActionCommand().equals("findPath")) {
			if (this.start != null && this.stop != null){
				for (int i = 0; i < rows; i++) {
					for (int l = 0; l < columns; l++) {
						gameBoardState[i][l].setToStop(this.stop);
						gameBoardState[i][l].setOpen(false);
						gameBoardState[i][l].setClosed(false);
						gameBoardState[i][l].clearNeighbours();
					}
				}
				generateNeighbors();
				findPath();
			}
		}
		
		for (int i = 0; i < rows; i++) {
			for (int l = 0; l < columns; l++) {
				if (e.getActionCommand().equals(i + "," + l)) {
					switch (gameBoardState[i][l].getState()) {
					case REGULAR:
						gameBoardState[i][l].setBackground(Color.BLACK);
						gameBoardState[i][l].state = TileState.COLLIDABLE;
						break;
					case COLLIDABLE:
						if (this.start == null ) {
							gameBoardState[i][l].state = TileState.START;
							gameBoardState[i][l].setBackground(Color.GREEN);
							gameBoardState[i][l].setText("Start");
							this.start = gameBoardState[i][l];
						} else if (this.stop == null) {
							gameBoardState[i][l].state = TileState.STOP;
							gameBoardState[i][l].setText("stop");
							gameBoardState[i][l].setBackground(Color.BLUE);
							this.stop = gameBoardState[i][l];
						} else {
							gameBoardState[i][l].setBackground(Color.WHITE);
							gameBoardState[i][l].state = TileState.REGULAR;
							gameBoardState[i][l].setText(null);
						}
						break;
					case START:
						gameBoardState[i][l].setBackground(Color.WHITE);
						gameBoardState[i][l].state = TileState.REGULAR;
						gameBoardState[i][l].setText(null);
						this.start = null;
						break;
					case STOP:
						gameBoardState[i][l].setBackground(Color.WHITE);
						gameBoardState[i][l].state = TileState.REGULAR;
						gameBoardState[i][l].setText(null);
						this.stop = null;
						break;
						
					}
				}
			}
		}
	}
	
	// ----------------------------------------------------------------------------------
	// Pathfinding functions.
	// ----------------------------------------------------------------------------------
	
	
	public void findPath() {
		ArrayList<Tiles> openList = new ArrayList <Tiles>();
		openList.add(start);
		
		while (!openList.isEmpty()) {
			// Start by finding the tile in the open list with the best
			// F-value
			Tiles consireredTile = findBestTile(openList);
			
			// Algorithm complete and path found.
			if (consireredTile == stop) {
				consireredTile.setBackground(Color.BLUE);
				while (consireredTile != start) {
					try {
						Thread.sleep(SLEEPTIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					consireredTile.getPrevious().setBackground(Color.BLUE);
					consireredTile.getPrevious().paint(consireredTile.getPrevious().getGraphics());
					consireredTile = consireredTile.getPrevious();
					System.out.println(consireredTile.getRow() + " " + consireredTile.getColumn() );
				}
				return;
				// The goal was not found in the openList
			} else {
				for (Tiles neighbor : consireredTile.getNeighbors()) {
					if (neighbor.isClosed()) 
						continue;
					
					// if the adjacent tile is not open for consideration, make sure it is, calculate length from
					// start, and add the currently considered tile as its parent.
					if (!neighbor.isOpen()) {
						openList.add(neighbor);
						neighbor.setOpen(true);
						
						neighbor.setParent(consireredTile);
						
						neighbor.setFromStart(consireredTile.isDiagonal(neighbor) ?
								consireredTile.getFromStart() + DIAGONALCOST :
									consireredTile.getFromStart() + 1);
					}
					
					// If the path we are currently trying is shorter than the previously recorded (for this tile), 
					// set the currently considered tile to the neighbors parent, and update the length from start.
					else if (neighbor.isOpen()) {
						if (neighbor.getFromStart() > (consireredTile.isDiagonal(neighbor) ?
								consireredTile.getFromStart() + DIAGONALCOST :
									consireredTile.getFromStart() + 1)) {
						
							neighbor.setParent(consireredTile);
							neighbor.setFromStart(consireredTile.isDiagonal(neighbor) ?
									consireredTile.getFromStart() + DIAGONALCOST :
										consireredTile.getFromStart() + 1);
							
						}
					}
				}
				consireredTile.setClosed(true);
				consireredTile.setOpen(false);
				openList.remove(consireredTile);
				consireredTile.setBackground(Color.ORANGE);
				consireredTile.paint(consireredTile.getGraphics());
				try {
					Thread.sleep(SLEEPTIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}// end getPath()
	
	

	private Tiles findBestTile(ArrayList <Tiles> openList) {
		int pointer = 0;
		double currentF = openList.get(0).getTotalPath();
		for (int i = 0; i < openList.size(); i++) {
			if (openList.get(i).getTotalPath() < currentF) {
				pointer = i;
				currentF = openList.get(i).getTotalPath();
			}
		}
		return openList.get(pointer);
	}

	private void generateNeighbors() {
		for (int i = 0; i < rows; i++) {
			for (int l = 0; l < columns; l++) {
				if (gameBoardState[i][l].getState() != TileState.COLLIDABLE){
					calculateNeigbors(gameBoardState[i][l]);
				}
			}
		}
	}
	
	private void calculateNeigbors(Tiles tile) {
		int top = tile.getColumn() + 1;
		int buttom = tile.getColumn() - 1;
		int right = tile.getRow() + 1;
		int left = tile.getRow() - 1;

		if (top < columns) {
			if (isRelevant(tile, gameBoardState[tile.getRow()][top])) {
				tile.addNeighbor(gameBoardState[tile.getRow()][top]);
			}

			if (right < rows) {
				if (isRelevant(tile, gameBoardState[right][top])) {
					tile.addNeighbor(gameBoardState[right][top]);
				}
			}
			if (left >= 0) {
				if (isRelevant(tile, gameBoardState[left][top])) {
					tile.addNeighbor(gameBoardState[left][top]);
				}
			}
		}
		if (buttom >= 0) {
			if (isRelevant(tile, gameBoardState[tile.getRow()][buttom])) {
				tile.addNeighbor(gameBoardState[tile.getRow()][buttom]);
			}

			if (right < rows) {
				if (isRelevant(tile, gameBoardState[right][buttom])) {
					tile.addNeighbor(gameBoardState[right][buttom]);
				}
			}

			if (left >= 0) {
				if (isRelevant(tile, gameBoardState[left][buttom])) {
					tile.addNeighbor(gameBoardState[left][buttom]);
				}
			}
		}
		if (left >= 0) {
			if (isRelevant(tile, gameBoardState[left][tile.getColumn()])) {
				tile.addNeighbor(gameBoardState[left][tile.getColumn()]);
			}
		}
		if (right < rows) {
			if (isRelevant(tile, gameBoardState[right][tile.getColumn()])) {
				tile.addNeighbor(gameBoardState[right][tile.getColumn()]);
			}
		}
	}

	/**
	 * Checks if a certain tile should be added to the current Tiles list of
	 * neighbors
	 * @param currentTile
	 * @param consideredTile
	 * @return True if the considered tile is not a solid, and the path between
	 * the two tiles will not go through a wall.
	 */
	private boolean isRelevant(Tiles currentTile, Tiles consideredTile) {
		if (consideredTile.getState() == TileState.COLLIDABLE) 
			return false;
	
		return (gameBoardState[currentTile.getRow()][consideredTile.getColumn()].getState() != TileState.COLLIDABLE 
		         && gameBoardState[consideredTile.getRow()][currentTile.getColumn()].getState() != TileState.COLLIDABLE);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new GameBoard(20, 20);
	}
}
