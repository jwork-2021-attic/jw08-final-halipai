package com.anic.calabashbros;
import java.awt.Color;

public class House extends Thing {

    public House(World world) {
        super(new Color(205, 92, 92), (char) 127, world);
    }

    public House(World world, char number) {
        super(new Color(139, 105, 20), (char) number, world);
    }
}