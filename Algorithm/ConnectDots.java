/*
 *   I have a grid, with integer value coordinates, let¡¯s say 0-19, in each, the x and y directions. 
 *   I would like you to build a program that will connect pairs of point using only horizontal or vertical lines.
 *   
 *   For instance, if I first give you the points (4,7) and (6,9), your program should output (4,7), (4,8), (4,9), (5,9), (6,9).  
 *   Clearly, there are other solutions (for instance: (4,7), (5,7), (5,8), (6,8), (6,9)).  Your program only needs to find one 
 *   of the possible solutions.
 *   
 *   The second part of the problem is to modify the above program so I can give you a second, third, 
 *   fourth and so on pairs of points (for instance, (5,7), (2,9)) and the program will connect the two endpoints, if possible 
 *   and printing out the path between them. 
 *     
 * */

package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ConnectDots {
	class Pair{
		int x;
		int y;
		Pair(int x, int y){
			this.x=x;
			this.y=y;
		}
	};
	
	List<Pair> connect(int[][] grid, Pair p1, Pair p2){
		List<Pair> path = new ArrayList<Pair>();
		if(grid[p1.x][p1.y]==1 || grid[p2.x][p2.y]==1)
			return path;
		
		// dfs from start point p1
		dfs(grid, p1, p1 , p2, path);
		// print temporary result with point value 2 or 3
		print(grid);
		// print path
		print(path);
		// turn the value of the point in path from 2 to 1
		turnTo(grid, path, 1);
		// turn the invalid point from 3 to 1 
		reset3(grid);
		
		print(grid);
		return path;
	}
	
	/*
	 * there is a naive way without so complex judgment statement, like
	 * 
	 * boolean dfs(int[][] grid, Pair p1, Pair p2, List<Pair> path){
	 * if(grid[p1.x][p1.y] != 0) 
	 * 		return false;	
	 * path.add(p1);
	 * grid[p1.x][p1.y] = 2;
	 * if(p1.x == p2.x && p1.y == p2.y)
	 * 		return true; //success
	 * 	
	 * if (ret == false && p1.x+1 < grid.length) ret |= dfs(grid, new Pair(p1.x+1, p1.y+1), p2, path);
	 * if (ret == false && p1.x > 0) ret |= dfs(grid, p0, new Pair(p1.x-1, p1.y), p2, path);
	 * if (ret == false && p1.y+1 < grid.length) ret |= dfs(grid, new Pair(p1.x, p1.y+1), p2, path);
	 * if (ret == false && p1.y > 0) ret |= dfs(grid, p0, new Pair(p1.x, p1.y-1), p2, path);
	 * if(ret==false){
	 * 		path.remove(path.size() -1);
	 * 		grid[p1.x][p1.y] = 0;
	 * }
	 * return ret;
	 * }
	 * 
	 * 
	 * the two way have similar TIME complexity, worst condition are O(N*N), N is the length of grid, because 
	 * each point may be involved for four times. But my follow solution can get clean path.
	 * 
	 * the logic:
	 * 1. if check again p0(start point), mean the path is useless, so mark these points in path as 3, 
	 * then clear path and reset p0 to 0;
	 * 2. if target p2 is on right of current point p1, go deep right first, until blocked, then try vertical direction
	 * then try reverse direction 
	 * 3. if target p2 is on left of current point p1, go deep left first, until blocked, then try vertical direction
	 * then try reverse direction 
	 * 4. then try x (vertical direction) within similar judge statement
	 * 5. after non equal condition, then judge equal condition, don't try it first, because non equal condition done first
	 * give possibility to get equal condition
	 * 
	 * */
	boolean dfs(int[][] grid, Pair p0, Pair p1, Pair p2, List<Pair> path){
		// if check again p0(start point), mean the path is useless, so mark these as 3, and clear path
		if(p1.x == p0.x && p1.y == p0.y && grid[p1.x][p1.y] != 0){
			turnTo(grid, path, 3);
			grid[p1.x][p1.y] = 0;
			path.clear();
		}
		
		// may be 2 or 3, return false
		if(grid[p1.x][p1.y] != 0) 
			return false;		
		path.add(p1);
		grid[p1.x][p1.y] = 2;
		if(p1.x == p2.x && p1.y == p2.y)
			return true; //success
		
		boolean ret = false;
		
		// when p2 is on right of p1, try right first
		if(p2.y > p1.y){
			if (ret == false && p1.y+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x, p1.y+1), p2, path);
			// then try x(move vertically) 
			if(p2.x > p1.x){//when p2 is below of p1, try down wards first
				if (ret == false && p1.x+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x+1, p1.y), p2, path);
			}else if(p2.x < p1.x){
				if (ret == false && p1.x > 0) ret |= dfs(grid, p0, new Pair(p1.x-1, p1.y), p2, path);
			}else if(p2.x == p1.x){
				if(p2.x >= p0.x){// when x already equal, then judge from initial intention, we need continue to go forward
					if (ret == false && p1.x+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x+1, p1.y), p2, path);
				}else{
					if (ret == false && p1.x > 0) ret |= dfs(grid, p0, new Pair(p1.x-1, p1.y), p2, path);
				}
			}
			
			// then try the reverse direction
			if (ret == false && p1.y > 0) ret |= dfs(grid, p0, new Pair(p1.x, p1.y-1), p2, path);
		}else if (p2.y < p1.y){
			if (ret == false && p1.y > 0) ret |= dfs(grid, p0, new Pair(p1.x, p1.y-1), p2, path);
			// then try x(move vertically) 
			if(p2.x > p1.x){//try down wards first
				if (ret == false && p1.x+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x+1, p1.y), p2, path);
			}else if(p2.x < p1.x){
				if (ret == false && p1.x > 0) ret |= dfs(grid, p0, new Pair(p1.x-1, p1.y), p2, path);
			}else if(p2.x == p1.x){
				if(p2.x >= p0.x){// when x already equal, then judge from initial intention, we need go forward
					if (ret == false && p1.x+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x+1, p1.y), p2, path);
				}else{
					if (ret == false && p1.x > 0) ret |= dfs(grid, p0, new Pair(p1.x-1, p1.y), p2, path);
				}
			}
			
			if (ret == false && p1.y+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x, p1.y+1), p2, path);
		}
		
		// when p2 is below of p1, try down first
		if(p2.x > p1.x){
			if (ret == false && p1.x+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x+1, p1.y), p2, path);
			
			// then try y(move horizontally)
			if(p2.y > p1.y){//try down wards first
				if (ret == false && p1.y+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x, p1.y+1), p2, path);
			}else if(p2.y < p1.y){
				if (ret == false && p1.y > 0) ret |= dfs(grid, p0, new Pair(p1.x, p1.y-1), p2, path);
			}else if(p2.y == p1.y){
				if (p2.y >= p0.y){ // when x already equal, then judge from initial intention, we need go forward
					if (ret == false && p1.y+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x, p1.y+1), p2, path);
				}else if(p2.y == p1.y){
					if (ret == false && p1.y > 0) ret |= dfs(grid, p0, new Pair(p1.x, p1.y-1), p2, path);
				}
			}
			
			// then try the reverse direction
			if (ret == false && p1.x > 0) ret |= dfs(grid, p0, new Pair(p1.x-1, p1.y), p2, path);
		}else if(p2.x < p1.x){
			if (ret == false && p1.x > 0) ret |= dfs(grid, p0, new Pair(p1.x-1, p1.y), p2, path);
			// then try y(move horizontally)
			if(p2.y > p1.y){//try down wards first
				if (ret == false && p1.y+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x, p1.y+1), p2, path);
			}else if(p2.y < p1.y){
				if (ret == false && p1.y > 0) ret |= dfs(grid, p0, new Pair(p1.x, p1.y-1), p2, path);
			}else if(p2.y == p1.y){
				if (p2.y >= p0.y){ // when x already equal, then judge from initial intention, we need go forward
					if (ret == false && p1.y+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x, p1.y+1), p2, path);
				}else if(p2.y == p1.y){
					if (ret == false && p1.y > 0) ret |= dfs(grid, p0, new Pair(p1.x, p1.y-1), p2, path);
				}
			}
			
			if (ret == false && p1.x+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x+1, p1.y), p2, path);
		}
		
		//even x or y already get right value and still been blocked
		if(p2.x == p1.x){
			if(p2.x >= p0.x){// when x already equal, then judge from initial intention, we need go forward
				if (ret == false && p1.x+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x+1, p1.y), p2, path);
			}else{
				if (ret == false && p1.x > 0) ret |= dfs(grid, p0, new Pair(p1.x-1, p1.y), p2, path);
			}
		}else if(p2.y == p1.y){
			if (p2.y >= p0.y){ // when x already equal, then judge from initial intention, we need go forward
				if (ret == false && p1.y+1 < grid.length) ret |= dfs(grid, p0, new Pair(p1.x, p1.y+1), p2, path);
			}else if(p2.y == p1.y){
				if (ret == false && p1.y > 0) ret |= dfs(grid, p0, new Pair(p1.x, p1.y-1), p2, path);
			}
		}
		
		if(ret==false){
			path.remove(path.size() -1);
			grid[p1.x][p1.y] = 0;
		}
		return ret;
	}

	void turnTo(int[][] grid, List<Pair> path, int target){
		Iterator<Pair> it = path.iterator();
		while(it.hasNext()){
			Pair cur = it.next();
			grid[cur.x][cur.y] = target;
		}
	}
	
	/*
	 * reset the point marked as 3 to 0, because it is not valid path
	 * */
	void reset3(int[][] grid){
		for(int i = 0; i<grid.length; i++){
			for(int j = 0; j<grid[0].length; j++){
				if(grid[i][j] == 3){
					grid[i][j] = 0;
				}
			}
		}
	}
	
	/*
	 * print path
	 * */
	void print(List<Pair> path){
		Iterator<Pair> it = path.iterator();
		while(it.hasNext()){
			Pair cur = it.next();
			System.out.format("->(%d,%d)", cur.x, cur.y);
		}
		System.out.println();
	}
	
	/*
	 * print grid
	 * */
	void print(int[][] grid){
		for(int i = 0; i<grid.length; i++){
			for(int j = 0; j<grid[0].length; j++){
				System.out.print(grid[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		ConnectDots test = new ConnectDots();
		int[][] grid = new int[20][20];
		for(int i = 0;i<grid.length; i++){
			Arrays.fill(grid[i], 0);
		}
		
		test.connect(grid, test.new Pair(4,7), test.new Pair(6,9));
		test.connect(grid, test.new Pair(5,7), test.new Pair(2,9));
		test.connect(grid, test.new Pair(5,5), test.new Pair(2,10));
		test.connect(grid, test.new Pair(19,19), test.new Pair(1,1));
		
//		test.connect(grid, test.new Pair(6,9), test.new Pair(4,7));
//		test.connect(grid, test.new Pair(2,9), test.new Pair(5,7));
//		test.connect(grid, test.new Pair(2,10), test.new Pair(5,5));
//		test.connect(grid, test.new Pair(1,1), test.new Pair(19,19));
		
		//path will be EMPTY, because start point is surrounded by exist path
		test.connect(grid, test.new Pair(5,8), test.new Pair(19,0));
		
	}

}
