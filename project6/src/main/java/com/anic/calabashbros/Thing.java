package com.anic.calabashbros;

import java.awt.Color;

public class Thing {

    public World world;

    public Tile<? extends Thing> tile;

    public int getX() {
        return this.tile.getxPos();
    }

    public int getY() {
        return this.tile.getyPos();
    }

    public void setTile(Tile<? extends Thing> tile) {
        this.tile = tile;
    }

    public boolean isExistent(){
        return true;
    }

    Thing(Color color, char glyph, World world) {
        this.color = color;
        this.glyph = glyph;
        this.world = world;
    }

    public Color color;

    public Color getColor() {
        return this.color;
    }

    public char glyph;

    public char getGlyph() {
        return this.glyph;
    }

    public void moveTo(int xPos, int yPos) {
        this.world.put(this, xPos, yPos);
    }

    public boolean isOver(){
        if(this.getX() == World.WIDTH-1 && this.getY() == World.HEIGHT-1){
        //if(this.getX() > 0 && this.getY() > 0){
            return true;
        }
        return false;
    }
}
