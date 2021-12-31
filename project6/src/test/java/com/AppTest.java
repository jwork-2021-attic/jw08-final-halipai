package com;
import com.anic.calabashbros.*;
import com.anic.screen.*;

import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.*;

public class AppTest{

    @Test
    public void testBigGoblin() throws Exception{
        World world = new World();
        BigGoblin a = new BigGoblin(world);
        assertEquals(3, a.getLiveValue());
        assertTrue(a.isExistent());
        assertEquals(new Color(255, 165, 0), a.color);
        assertEquals((char)64, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testBoat() throws Exception{
        World world = new World();
        Boat a = new Boat(world);
        assertEquals(new Color(255, 140, 0), a.color);
        assertEquals((char)29, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testCalabash1() throws Exception{
        World world = new World();
        Color color = new Color(255, 140, 0);
        Calabash a = new Calabash(color, world);
        assertEquals(3, a.getLiveValue());
        assertTrue(a.isExistent());
        assertTrue(!a.isSuccess());
        assertEquals(48, a.getDetail());
        assertEquals(color, a.color);
        assertEquals((char)12, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testCalabash2() throws Exception{
        World world = new World();
        Color color = new Color(255, 140, 0);
        int detail = 48;
        Calabash a = new Calabash(color, world, detail, 1);
        assertEquals(1, a.getLiveValue());
        assertTrue(a.isExistent());
        assertTrue(!a.isSuccess());
        assertEquals(detail, a.getDetail());
        assertEquals(color, a.color);
        assertEquals((char)12, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testCane() throws Exception{
        World world = new World();
        Cane a = new Cane(world);
        assertEquals(new Color(0, 0, 0), a.color);
        assertEquals((char)244, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testCarpet() throws Exception{
        World world = new World();
        Carpet a = new Carpet(world);
        assertEquals(new Color(137, 104, 205), a.color);
        assertEquals((char)36, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testEnergyFood() throws Exception{
        World world = new World();
        EnergyFood a = new EnergyFood(world);
        assertEquals(new Color(165, 42, 42), a.color);
        assertEquals((char)3, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testFloor() throws Exception{
        World world = new World();
        Floor a = new Floor(world);
        assertEquals(new Color(238, 220, 130), a.color);
        assertEquals((char)0, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testGoblin1() throws Exception{
        World world = new World();
        Goblin a = new Goblin(world);
        assertTrue(a.isExistent());
        assertEquals(new Color(105, 105, 105), a.color);
        assertEquals((char)2, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testGoblin2() throws Exception{
        World world = new World();
        Color color = new Color(105, 105, 105);
        Goblin a = new Goblin(world, color);
        assertTrue(a.isExistent());
        assertEquals(new Color(105, 105, 105), a.color);
        assertEquals((char)2, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testGrass() throws Exception{
        World world = new World();
        Grass a = new Grass(world);
        assertEquals(new Color(143, 188, 143), a.color);
        assertEquals((char)208, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testHouse1() throws Exception{
        World world = new World();
        House a = new House(world);
        assertEquals(new Color(205, 92, 92), a.color);
        assertEquals((char)127, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testHouse2() throws Exception{
        World world = new World();
        char number = 8;
        House a = new House(world, number);
        assertEquals(new Color(139, 105, 20), a.color);
        assertEquals(number, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testKey() throws Exception{
        World world = new World();
        Key a = new Key(world);
        assertEquals(new Color(171, 130, 255), a.color);
        assertEquals((char)33, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testMarbles1() throws Exception{
        World world = new World();
        Color color = new Color(105, 105, 105);
        Marbles a = new Marbles(world, color, new Calabash(color, world));
        assertTrue(a.isExistent());
        assertEquals(color, a.color);
        assertEquals((char)7, a.glyph);
        assertEquals(world, a.world);
        assertEquals("", a.direction);
    }

    @Test
    public void testMarbles2() throws Exception{
        assertEquals("attackleft", new Marbles(new World(), new Color(105, 105, 105), 1).direction);
        assertEquals("attackup", new Marbles(new World(), new Color(105, 105, 105), 2).direction);
        assertEquals("attackright", new Marbles(new World(), new Color(105, 105, 105), 3).direction);
        assertEquals("attackdown", new Marbles(new World(), new Color(105, 105, 105), 4).direction);
    }

    @Test
    public void testMine() throws Exception{
        World world = new World();
        Mine a = new Mine(world);
        assertEquals(new Color(0, 0, 0), a.color);
        assertEquals((char)147, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testPortal() throws Exception{
        World world = new World();
        Portal a = new Portal(world);
        assertEquals(new Color(255, 193, 37), a.color);
        assertEquals((char)15, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testTree() throws Exception{
        World world = new World();
        Tree a = new Tree(world);
        assertEquals(new Color(0, 238, 0), a.color);
        assertEquals((char)30, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testWall1() throws Exception{
        World world = new World();
        Wall a = new Wall(world);
        assertEquals(new Color(105, 105, 105), a.color);
        assertEquals((char)8, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testWall2() throws Exception{
        World world = new World();
        Color color = new Color(105, 105, 105);
        Wall a = new Wall(world, color);
        assertEquals(color, a.color);
        assertEquals((char)178, a.glyph);
        assertEquals(world, a.world);
    }

    @Test
    public void testWater() throws Exception{
        World world = new World();
        Water a = new Water(world);
        assertEquals(new Color(0, 0, 255), a.color);
        assertEquals((char)240, a.glyph);
        assertEquals(world, a.world);
    }








    @Test
    public void testWorldScreen() throws Exception {
        assertEquals(true, new WorldScreen().running());
    }
}