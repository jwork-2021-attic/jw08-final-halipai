package com.anic.calabashbros;

import java.awt.Color;
import java.lang.Math;
import java.util.ArrayList;


public class World {
    public static final int OUTWIDTH = 110;
    public static final int OUTHEIGHT = 60;

    public static final int WIDTH = 106;
    public static final int HEIGHT = 50;

    public Tile<Thing>[][] tiles;
    public ArrayList<Calabash> calas;
    public ArrayList<Goblin> goblins;
    public ArrayList<BigGoblin> biggoblins;
    public ArrayList<Marbles> allMarbles;

    public void addCala(Calabash cala){
        calas.add(cala);
    }

    public void addGoblin(Goblin goblin){
        goblins.add(goblin);
    }

    public void addBigGoblin(BigGoblin biggoblin){
        biggoblins.add(biggoblin);
    }

    public void addMarbles(Marbles marbles){
        allMarbles.add(marbles);
    }

    public ArrayList<Calabash> getCalabash(){
        return calas;
    }

    public ArrayList<Goblin> getGoblin(){
        return goblins;
    }

    public ArrayList<BigGoblin> getBigGoblin(){
        return biggoblins;
    }

    public ArrayList<Marbles> getMarbles(){
        return allMarbles;
    }

    public World(boolean load){
        calas = new ArrayList<>();
        goblins = new ArrayList<>();
        biggoblins = new ArrayList<>();
        allMarbles = new ArrayList<>();

        if (tiles == null) {
            tiles = new Tile[WIDTH][HEIGHT];
        }

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                tiles[i][j] = new Tile<>(i, j);
                tiles[i][j].setThing(new Floor(this));
            }
        }
    }

    public World() {
        calas = new ArrayList<>();
        goblins = new ArrayList<>();
        biggoblins = new ArrayList<>();
        allMarbles = new ArrayList<>();

        if (tiles == null) {
            tiles = new Tile[WIDTH][HEIGHT];
        }

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                tiles[i][j] = new Tile<>(i, j);
                tiles[i][j].setThing(new Floor(this));
            }
        }

        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 7-i; j++){
                tiles[i][j].setThing(new Tree(this));
            }
        }
        for(int j = 0; j < 8; j++){
            for(int k = 0; k < 5; k++){
                int i = 7-j+3*k;
                tiles[i][j].setThing(new Water(this));
                tiles[i+1][j].setThing(new Water(this));
                tiles[i+2][j].setThing(new Grass(this));
            }
        }

        for(int i = 0; i < 13; i++){
            tiles[i][8].setThing(new Water(this));
        }

        for(int i = 0; i < 4; i++){
            for(int j = 10; j < 14; j++){
                tiles[i][j].setThing(new House(this));
            }
        }

        int tempj = 9, tempi = 12;
        while(tempj < HEIGHT){
            tiles[tempi][tempj].setThing(new Water(this));
            if(Math.random() > 0.5) tempi += 1;
            tiles[tempi][tempj].setThing(new Water(this));
            if(Math.random() > 0.5) tempj += 1;
        }

        boolean flag = false;
        int num = 0;
        for(int j = 15; j < HEIGHT; j++){
            for(int i = 0; i < WIDTH; i++){
                if(tiles[i][j].getThing() instanceof Water) break;
                if((i == 23 && j == 28) || (i == 7 && j == 25) || (i == 22 && j ==36) || (i == 16 && j == 40))
                    continue;
                else if(Math.random() < 0.15 && !(tiles[i][j].getThing() instanceof Portal))
                    tiles[i][j].setThing(new Tree(this));
                else if(!flag && j > 40 && Math.random() < 0.05){
                    tiles[i][j].setThing(new Boat(this));
                    flag = true;
                }
                else if(num < 3 && j > 30 && Math.random() < 0.01){
                    tiles[i][j].setThing(new EnergyFood(this));
                    num += 1;
                }
            }
        }

        int left = WIDTH/2-10;
        int right = WIDTH/2+10;
        int up = HEIGHT/2-7;
        int down = HEIGHT/2+7;
        for(int i = left+2; i < right-1; i++){
            if(i == WIDTH/2)
                continue;
            tiles[i][up].setThing(new Wall(this));
            tiles[i][down].setThing(new Wall(this));
        }
        for(int j = up+1; j < down; j++){
            if(j == HEIGHT/2)
                continue;
            tiles[left][j].setThing(new Wall(this));
            tiles[right][j].setThing(new Wall(this));
        }
        for(int i = WIDTH/2-1; i < WIDTH/2+2; i++){
            for(int j = HEIGHT/2-1; j < HEIGHT/2+2; j++){
                tiles[i][j].setThing(new Portal(this));
            }
        }


        for(int i = WIDTH/2-7; i < WIDTH/2+8; i ++){
            tiles[i][0].setThing(new Wall(this, new Color(255, 193, 37)));
        }
        for(int i = WIDTH/2-7; i < WIDTH/2+8; i ++){
            if(i == WIDTH/2)  continue;
            tiles[i][6].setThing(new Wall(this, new Color(255, 193, 37)));
        }
        for(int j = 0; j < 7; j++){
            tiles[WIDTH/2-8][j].setThing(new Wall(this, new Color(255, 193, 37)));
            tiles[WIDTH/2+8][j].setThing(new Wall(this, new Color(255, 193, 37)));
        }
        tiles[WIDTH/2][3].setThing(new Key(this));
        tiles[WIDTH/2][1].setThing(new EnergyFood(this));
        tiles[WIDTH/2][5].setThing(new EnergyFood(this));
        tiles[WIDTH/2-2][3].setThing(new EnergyFood(this));
        tiles[WIDTH/2+2][3].setThing(new EnergyFood(this));


        for(int j = 0; j < HEIGHT; j++){
            if(j >= 40 && j < 45){
                tiles[right+5][j].setThing(new House(this));
                continue;
            }
            tiles[right+5][j].setThing(new Wall(this));
        }
        tempi = right+4;
        while(tiles[tempi][34].getThing() instanceof Floor){
            tiles[tempi][34].setThing(new Wall(this));
            tiles[tempi][33].setThing(new Tree(this));
            tempi -= 1;
        }
        tempi += 1;
        for(int j = 33; j > 7; j--){
            tiles[tempi][j].setThing(new Wall(this));
        }
        tempi -= 1;
        int tempi2 = tempi;
        while(tiles[tempi][8].getThing() instanceof Floor){
            tiles[tempi][8].setThing(new Wall(this));;
            tempi -= 1;
        }


        for(int j = 9; j < 34; j++){
            int i = tempi2;
            while(tiles[i][j].getThing() instanceof Floor){
                tiles[i][j].setThing(new Grass(this));;
                i -= 1;
            }
        }

        for(int i = right+7; i < WIDTH-2; i++){
            tiles[i][0].setThing(new Wall(this));
            tiles[i][10].setThing(new Wall(this));
            tiles[i][20].setThing(new Wall(this));
            tiles[i][30].setThing(new Wall(this));
            tiles[i][HEIGHT-1].setThing(new Wall(this));
        }
        for(int j = 0; j < HEIGHT;j++){
            tiles[WIDTH-1][j].setThing(new Wall(this));
        }
        tiles[right+6][0].setThing(new Wall(this));
        tiles[right+6][10].setThing(new Wall(this));
        tiles[right+6][20].setThing(new Carpet(this));
        tiles[right+6][30].setThing(new Wall(this));
        tiles[right+6][HEIGHT-1].setThing(new Wall(this));
        tiles[WIDTH-2][0].setThing(new Wall(this));
        tiles[WIDTH-2][10].setThing(new Carpet(this));
        tiles[WIDTH-2][20].setThing(new Wall(this));
        tiles[WIDTH-2][30].setThing(new Carpet(this));
        tiles[WIDTH-2][HEIGHT-1].setThing(new Wall(this));

        
        tiles[(right+WIDTH+4)/2][40].setThing(new Cane(this));
        for(int k = 1; k <= 3; k++){
            int i = (right+WIDTH+4)/2;
            tiles[i-1][40+k].setThing(new Wall(this));
            tiles[i-1][40-k].setThing(new Wall(this));
            tiles[i+1][40+k].setThing(new Wall(this));
            tiles[i+1][40-k].setThing(new Wall(this));
            tiles[i+k][40-1].setThing(new Wall(this));
            tiles[i-k][40-1].setThing(new Wall(this));
            tiles[i+k][40+1].setThing(new Wall(this));
            tiles[i-k][40+1].setThing(new Wall(this));
        }

        for(int i = right+6; i < WIDTH-1; i++){
            for(int j = 1; j < HEIGHT-1; j++){
                if((i == WIDTH-20 && j == HEIGHT-3) || (i == WIDTH-12 && j == HEIGHT-16)) continue;
                if((i == WIDTH-12 && j == 3))   continue;
                if((i == WIDTH-12 && j == 13))   continue;
                if((i == WIDTH-12 && j == 23))   continue;
                if(tiles[i][j].getThing() instanceof Floor && Math.random() < 0.05)
                    tiles[i][j].setThing(new Mine(this));
            }
        }
        tiles[right+5][5].setThing(new Carpet(this));
    }

    public Thing get(int x, int y) {
        return this.tiles[x][y].getThing();
    }

    public void put(Thing t, int x, int y) {
        if(x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT){
            this.tiles[x][y].setThing(t);
        }
    }

    public boolean inTheWorld(int x, int y){
        if(x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT){
            return true;
        }
        return false;
    }
}
