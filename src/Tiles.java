import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.border.LineBorder;



public class Tiles extends JButton {
	
	private double fromStart, toStop;
	TileState state;
	private Tiles parent;
	private int row, column;
	private ArrayList <Tiles> neighbors;
	private boolean open, closed;
	
	public Tiles( ActionListener listener, int row, int column) {
		this.row = row;
		this.column = column;
		this.state = TileState.REGULAR;
		
		setBackground(Color.WHITE);
		this.setBorder(new LineBorder(Color.RED, 1));
		this.setOpaque(true);
		this.addActionListener(listener);
		this.setPreferredSize(new Dimension(30,30));
		
		neighbors = new ArrayList <Tiles>();
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getColumn() {
		return this.column;
	}
	
	public void setState (TileState newState) {
		this.state = newState;
	}
	
	public TileState getState () {
		return this.state;
	}
	
	public Tiles getPrevious () {
		return this.parent;
	}
	
	public void setParent (Tiles parent) {
		this.parent = parent;
	}

	public double getFromStart() {
		return fromStart;
	}

	public void setFromStart(double fromStart) {
		this.fromStart = fromStart;
	}

	public double getToStop() {
		return toStop;
	}

	/**
	 * Set the H-value of this tile. The value simply represents a "guess" of
	 * the distance between this tile and the specified tile (usually the
	 * player). That is, how long the path would be from this tile, if there
	 * were no walls in the gameboard. Not "needed" for pathfinding, but will often
	 * increase the speed of the algorithm.
	 * 
	 * @param stop
	 */
	public void setToStop(Tiles stop) {
		this.toStop = (Math.abs(this.row - stop.getRow()) + Math.abs(this.column
				- stop.getColumn()));
	}

	public double getTotalPath() {
		return this.toStop + this.fromStart;
	}

	public ArrayList <Tiles> getNeighbors() {
		return neighbors;
	}

	public void addNeighbor (Tiles neighbor) {
		this.neighbors.add(neighbor);
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	public void clearNeighbours () {
		this.neighbors.clear();
	}
	
	/**
	 * Checks whether this tile is diagonal to the specified tile.
	 * 
	 * @param tile
	 * @return
	 */
	public boolean isDiagonal(Tiles tile) {
		return (this.getRow() != tile.getRow() && this.getColumn() != tile
				.getColumn());
	}
}
