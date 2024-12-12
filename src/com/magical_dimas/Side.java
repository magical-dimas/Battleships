package com.magical_dimas;

import org.fusesource.jansi.Ansi;

import java.util.HashMap;

import static org.fusesource.jansi.Ansi.ansi;

public class Side {
    Ship[] fleet;
    boolean[] shownShips;
    HashMap<Integer, Boolean> shownPositions, occupiedPositions;
    int[] field;

    Side(){
        field = new int[100];
        fleet = new Ship[10];
        shownShips = new boolean[10];
        shownPositions = new HashMap<>();
        occupiedPositions = new HashMap<>();
        fleet[0] = new Ship(-100, false, 1);
        fleet[1] = new Ship(-100, false, 1);
        fleet[2] = new Ship(-100, false, 1);
        fleet[3] = new Ship(-100, false, 1);
        fleet[4] = new Ship(-100, false, 2);
        fleet[5] = new Ship(-100, false, 2);
        fleet[6] = new Ship(-100, false, 2);
        fleet[7] = new Ship(-100, false, 3);
        fleet[8] = new Ship(-100, false, 3);
        fleet[9] = new Ship(-100, false, 4);
    }

    public void refresh(){
        shownPositions.clear();
        for(int i = 0; i < 10; i++){
            if(shownShips[i]) for (int pos:fleet[i].compartments) {
                    shownPositions.put(pos, true);
            }
        }
        occupiedPositions.clear();
        for(int i = 0; i < 10; i++){
            if(shownShips[i]) for (int pos:fleet[i].compartments) {
                occupiedPositions.put(pos, true);
                if(pos>9) occupiedPositions.put(pos-10, true);
                if(pos<90) occupiedPositions.put(pos+10, true);
                if(pos%10!=0) occupiedPositions.put(pos-1, true);
                if(pos%10!=9) occupiedPositions.put(pos+1, true);
                if(pos>9 && pos%10!=0) occupiedPositions.put(pos-11, true);
                if(pos>9 && pos%10!=9) occupiedPositions.put(pos-9, true);
                if(pos<90 && pos%10!=0) occupiedPositions.put(pos+9, true);
                if(pos<90 && pos%10!=9) occupiedPositions.put(pos+11, true);
            }
        }
    }

    public boolean shoot(int pos){
        for (Ship s: fleet) {
            if (s.checkLanded(pos) != -1){
                field[pos] = 2;
                if(s.damage(s.checkLanded(pos))) {
                    fillShipBorders(s);
                }
                return true;
            }
        }
        field[pos] = 1;
        return false;
    }

    public void fillShipBorders(Ship s){
        for (int pos:s.compartments) {
            if(pos>9) field[pos-10] = 1;
            if(pos<90) field[pos+10] = 1;
            if(pos%10!=0) field[pos-1] = 1;
            if(pos%10!=9) field[pos+1] = 1;
            if(pos>9 && pos%10!=0) field[pos-11] = 1;
            if(pos>9 && pos%10!=9) field[pos-9] = 1;
            if(pos<90 && pos%10!=0) field[pos+9] = 1;
            if(pos<90 && pos%10!=9) field[pos+11] = 1;
        }
        for (int pos:s.compartments) {
            field[pos] = 2;
        }
    }

    public boolean checkLost(){
        boolean lost = true;
        for (Ship s: fleet) {
            if(!s.dead) lost = false;
        }
        return  lost;
    }

    public void drawSide(){
        System.out.println(ansi().fg(Ansi.Color.GREEN).a("    A B C D E F G H I J"));
        for (int i = 0; i < 10; i++) {
            System.out.print(i+1+((i>=9)?"":" ")+"| ");
            for (int j = 0; j < 10; j++) {
                int pos = i*10+j;
                if (shownPositions.containsKey(pos)) System.out.print(ansi().bg(Ansi.Color.WHITE));
                switch (field[pos]){
                    case 1:
                        System.out.print(ansi().fg(Ansi.Color.WHITE).a("O ").fg(Ansi.Color.GREEN));
                        break;
                    case 2:
                        System.out.print(ansi().fg(Ansi.Color.RED).a("X ").fg(Ansi.Color.GREEN));
                        break;
                    default:
                        System.out.print("  ");
                }
                System.out.print(ansi().bg(Ansi.Color.DEFAULT));
            }
            System.out.print("|\n");
        }
    }
}
