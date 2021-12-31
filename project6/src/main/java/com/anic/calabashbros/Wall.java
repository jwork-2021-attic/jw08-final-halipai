package com.anic.calabashbros;
import java.awt.Color;

public class Wall extends Flint {

    public Wall(World world) {
        super(new Color(105, 105, 105), (char) 8, world);
    }

    public Wall(World world, Color color){
        //super(new Color(205, 145, 158), (char) 7, world);
        super(color, (char) 178, world);
    }
}
