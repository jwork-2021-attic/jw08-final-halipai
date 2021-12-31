package com.anic.maze_generator;

import java.util.ArrayList;
import java.awt.Color;
import java.util.PriorityQueue;
import com.anic.calabashbros.*;
import java.util.concurrent.ExecutorService;

public class MazeGenerator {
    private int[][] maze;
    //private int dimension;
    private int row, column;
    public World world;

    PriorityQueue tableOne;
    int visited[][];
    int finalx, finaly;
    int path[][] = {{-1,0},{1,0},{0,1},{0,-1}};

    public MazeGenerator(int row, int column, World world) {
        maze = new int[row][column];
        for(int i = 0; i < row; i++){
            for(int j = 0; j < column; j++){
                maze[i][j] = 1;
            }
        }
        this.row = row;
        this.column = column;
        this.world = world;
    }

    public void generateMaze() {
        // int left = this.row/2-10;
        // int right = this.row/2+10;
        // int up = this.column/2-7;
        // int down = this.column/2+7;
        // for(int i = left+2; i < right-1; i++){
        //     if(i == this.row/2)
        //         continue;
        //     maze[i][up] = 0;
        //     maze[i][down] = 0;
        // }
        // for(int i = up+1; i < down; i++){
        //     if(i == this.column/2)
        //         continue;
        //     maze[left][i] = 0;
        //     maze[right][i] = 0;
        // }

        // for (int i = 0; i < row; i++) {
        //     for (int j = 0; j < column; j++) {
        //         if(maze[i][j] == 0){
        //             Wall wall = new Wall(world);
        //             world.put(wall, i ,j);
        //         }
        //     }
        // }

        // Key key = new Key(world);
        // world.put(key, this.row/2 ,this.column/2);


    }
    public ArrayList<Creature> CreateCreature(ExecutorService exec){
        ArrayList<Creature> creatures = new ArrayList<>();
        Calabash cala = new Calabash(new Color(218, 112, 214), this.world);
        world.put(cala, 1, 11);
        creatures.add(cala);
        world.addCala(cala);

        BigGoblin bigGoblin1 = new BigGoblin(this.world);
        world.put(bigGoblin1, this.row-12, 3);
        creatures.add(bigGoblin1);
        world.addBigGoblin(bigGoblin1);
        BigGoblin bigGoblin2 = new BigGoblin(this.world);
        world.put(bigGoblin2, this.row-12, 13);
        creatures.add(bigGoblin2);
        world.addBigGoblin(bigGoblin2);
        BigGoblin bigGoblin3 = new BigGoblin(this.world);
        world.put(bigGoblin3, this.row-12, 23);
        creatures.add(bigGoblin3);
        world.addBigGoblin(bigGoblin3);

        Goblin goblin11 = new Goblin(this.world);
        world.put(goblin11, 23, 28);
        creatures.add(goblin11);
        world.addGoblin(goblin11);
        Goblin goblin12 = new Goblin(this.world);
        world.put(goblin12, 7, 25);
        creatures.add(goblin12);
        world.addGoblin(goblin12);
        Goblin goblin13 = new Goblin(this.world);
        world.put(goblin13, 22, 36);
        creatures.add(goblin13);
        world.addGoblin(goblin13);
        Goblin goblin14 = new Goblin(this.world);
        world.put(goblin14, 16, 40);
        creatures.add(goblin14);
        world.addGoblin(goblin14);

        Goblin goblin21 = new Goblin(this.world, new Color(47, 79, 79));
        world.put(goblin21, this.row/2-3, this.column/2);
        creatures.add(goblin21);
        world.addGoblin(goblin21);
        Goblin goblin22 = new Goblin(this.world, new Color(47, 79, 79));
        world.put(goblin22, this.row/2, this.column/2-3);
        creatures.add(goblin22);
        world.addGoblin(goblin22);
        Goblin goblin23 = new Goblin(this.world, new Color(47, 79, 79));
        world.put(goblin23, this.row/2+3, this.column/2);
        creatures.add(goblin23);
        world.addGoblin(goblin23);
        Goblin goblin24 = new Goblin(this.world, new Color(47, 79, 79));
        world.put(goblin24, this.row/2, this.column/2+3);
        creatures.add(goblin24);
        world.addGoblin(goblin24);

        Goblin goblin31 = new Goblin(this.world, new Color(47, 79, 79));
        world.put(goblin31, this.row-20, this.column-3);
        creatures.add(goblin31);
        world.addGoblin(goblin31); 
        Goblin goblin32 = new Goblin(this.world, new Color(47, 79, 79));
        world.put(goblin32, this.row-12, this.column-16);
        creatures.add(goblin32);
        world.addGoblin(goblin32);
        Goblin goblin33 = new Goblin(this.world, new Color(47, 79, 79));
        world.put(goblin33, this.row-30, this.column-3);
        creatures.add(goblin33);
        world.addGoblin(goblin33);
        Goblin goblin34 = new Goblin(this.world, new Color(47, 79, 79));
        world.put(goblin34, this.row-32, this.column-16);
        creatures.add(goblin34);
        world.addGoblin(goblin34);

        return creatures;
    }

}
