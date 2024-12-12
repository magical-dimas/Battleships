package com.magical_dimas;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.Color.*;

import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;

public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        AnsiConsole.systemInstall();
        int chosen;
        while (true) {
            do {
                printMenu();
                String input = in.nextLine();
                try {
                    chosen = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    chosen = -1;
                }
            } while (chosen != 1 && chosen != 2 && chosen != 3);
            switch (chosen) {
                case 1:
                    initiateGame();
                    break;
                case 2:
                    printData();
                    in.nextLine();
                    break;
                case 3:
                    return;
            }
        }
    }

    static void printMenu(){
        System.out.print(ansi().eraseScreen());
        System.out.println(ansi().fg(BLACK).bg(GREEN)
                .a("//////BATTLESHIPS\\\\\\\\\\\\").bg(DEFAULT).fg(DEFAULT).a("\n"));
        System.out.println("Enter the number to:");
        System.out.print(ansi().fg(GREEN).a("1.Start the game\n2.See data\n3.Quit\n"));
    }

    static void printData(){
        String data =
        "Battleship (also known as Battleships) is a strategy type guessing game for two players.\n" +
        "It is played on ruled grids (paper or board) on which each player's fleet of warships are marked.\n"+
        "The locations of the fleets are concealed from the other player.\n" +
        "Players alternate turns calling \"shots\" at the other player's ships, and the objective of the\n"+
        "game is to destroy the opposing player's fleet.\n" +
        "Battleship is known worldwide as a pencil and paper game which dates from World War I.\n" +
        "It was published by various companies as a pad-and-pencil game in the 1930s and was released as a\n" +
        "plastic board game by Milton Bradley in 1967. The game has spawned electronic versions, video games,\n"+
        "smart device apps and a film.\n\n";
        System.out.print(ansi().eraseScreen().fg(DEFAULT).a("DATA:\n").fg(GREEN).a(data));
        System.out.println(ansi().fg(DEFAULT).a("Press Enter to return to the menu").fg(GREEN));
    }

    static void initiateGame(){
        Side playerSide = new Side();
        Side enemySide = new Side();
        Scanner in = new Scanner(System.in);
        int chosen;
        boolean planning = true, notready = false;
        while (planning) {
            do {
                System.out.println(ansi().eraseScreen().fg(DEFAULT).a("Your forces:\n\n").fg(GREEN));
                playerSide.drawSide();
                if(notready) {
                    System.out.println(ansi().fg(RED).a("\nNeed to deploy all ships before combat"));
                    notready = false;
                }
                System.out.println(ansi().fg(DEFAULT).a("\nEnter the number to:"));
                System.out.print(ansi().fg(GREEN).a("1.Add or reconfigure ships\n2.Begin the battle\n3.Quit\n"));
                String input = in.nextLine();
                try {
                    chosen = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    chosen = -1;
                }
            } while (chosen != 1 && chosen != 2 && chosen != 3);
            switch (chosen) {
                case 1:
                    organizeShips(playerSide);
                    break;
                case 2:
                    boolean ready = true;
                    for (boolean b:playerSide.shownShips) {
                        if(!b) {
                            ready = false;
                            notready = true;
                        }
                    }
                    if(ready) {
                        planning = false;
                    }
                    break;
                case 3:
                    return;
            }
        }
        randomizeShips(enemySide);
        Arrays.fill(enemySide.shownShips, false);
        enemySide.refresh();
        boolean exit = false;
        ArrayList<Integer> lastTargets= new ArrayList<>();
        Random r = new Random();
        while (!playerSide.checkLost() && !enemySide.checkLost()){
            int shotPos = -1;
            while (!enemySide.checkLost() && !playerSide.checkLost()) {
                System.out.print(ansi().eraseScreen());
                enemySide.drawSide();
                for (int i = 0; i < 15; i++) System.out.print(ansi().fg(DEFAULT).a("-"));
                System.out.println("");
                playerSide.drawSide();
                if(shotPos == -2) System.out.println(ansi().
                        fg(RED).a("Incorrect input format, please, try again"));
                if(shotPos == -3) System.out.println(ansi().
                        fg(RED).a("This location has already been shot, try another"));
                if(shotPos == -4) System.out.println(ansi().
                        fg(DEFAULT).a("You have hit the opposing ship, shoot again"));
                if(shotPos>=0) {
                    System.out.println(ansi().fg(RED).a("\nYou have missed. It is now the enemy's turn"));
                    System.out.println(ansi().fg(DEFAULT).a("Press Enter to  continue"));
                    in.nextLine();
                    break;
                }
                System.out.println(ansi().fg(GREEN).a("\nIt is your turn now"));
                System.out.println(ansi().fg(DEFAULT).a("Enter the position you want to shoot. " +
                        "If you want to stop, enter \"quit\""));
                String input = in.nextLine();
                if (input.toLowerCase().equals("quit")) {
                    exit = true;
                    break;
                }
                shotPos = parseShot(input);
                if(shotPos>=0) {
                    if(enemySide.field[shotPos]!=0) shotPos = -3;
                    else if(enemySide.shoot(shotPos)) shotPos = -4;
                }
            }
            if (exit) break;
            while (!enemySide.checkLost() && !playerSide.checkLost()) {
                if(lastTargets.isEmpty()) {
                    do {
                        shotPos = r.nextInt(100);
                    } while (playerSide.field[shotPos] != 0);
                }
                else if(lastTargets.size()<2) shotPos = chooseNeighbour(playerSide.field, lastTargets
                        .get(r.nextInt(lastTargets.size())), r);
                else {
                    if((lastTargets.get(0)-lastTargets.get(1))%10==0) do {
                        shotPos = chooseNeighbour(playerSide.field, lastTargets
                                .get(r.nextInt(lastTargets.size())), r);
                    } while (shotPos%10!=lastTargets.get(0)%10);
                    else do {
                        shotPos = chooseNeighbour(playerSide.field, lastTargets
                                .get(r.nextInt(lastTargets.size())), r);
                    } while (shotPos/10!=lastTargets.get(0)/10);
                }
                if(playerSide.shoot(shotPos)) {
                    lastTargets.add(shotPos);
                    shotPos = -4;
                }
                ArrayList<Integer> toDelete = new ArrayList<>();
                for (int pos:lastTargets) {
                    if(chooseNeighbour(playerSide.field, pos, r)==-1) toDelete.add(pos);
                }
                for (int id:toDelete){
                    lastTargets.remove(Integer.valueOf(id));
                }
                if(shotPos>=0) break;
                System.out.print(ansi().eraseScreen());
                enemySide.drawSide();
                for (int i = 0; i < 15; i++) System.out.print(ansi().fg(DEFAULT).a("-"));
                System.out.println("");
                playerSide.drawSide();
                System.out.println(ansi().fg(RED).a("The enemy has hit your ship and gets another turn"));
                System.out.println(ansi().fg(DEFAULT).a("Press Enter to the continue"));
                in.nextLine();
            }
            if(playerSide.checkLost()){
                System.out.println(ansi().eraseScreen()
                        .fg(RED).a("You have lost the battle\n" +
                                "Press Enter to continue"));
                in.nextLine();
                break;
            }
            if(enemySide.checkLost()){
                System.out.println(ansi().eraseScreen()
                        .fg(GREEN).a("Congratulations! You have won the battle\n" +
                                "Press Enter to continue"));
                in.nextLine();
                break;
            }
        }
    }

    static void randomizeShips(Side side){
        Random r = new Random();
        for (int i = 9; i >= 0; i--){
            boolean set = false;
            while(!set){
                int pos = r.nextInt(100);
                boolean hor = r.nextBoolean();
                side.fleet[i].pos = pos;
                side.fleet[i].hor = hor;
                side.fleet[i].reallignCompartments();
                if((side.fleet[i].hor && pos%10+side.fleet[i].len>10)
                        || (!side.fleet[i].hor && pos/10+side.fleet[i].len>10)){
                    side.fleet[i].pos = -100;
                    side.fleet[i].hor = false;
                    side.fleet[i].reallignCompartments();
                }
                for (int p:side.fleet[i].compartments) {
                    if(side.occupiedPositions.containsKey(p)){
                        side.fleet[i].pos = -100;
                        side.fleet[i].hor = false;
                        side.fleet[i].reallignCompartments();
                    }
                }
                if(side.fleet[i].pos!=-100){
                    side.shownShips[i] = true;
                    side.refresh();
                    set = true;
                }
            }
        }
    }

    static void organizeShips(Side side){
        Scanner in = new Scanner(System.in);
        int chosen;
        while (true) {
            do {
                System.out.println(ansi().eraseScreen().fg(DEFAULT).a("Your forces:\n").fg(GREEN));
                side.drawSide();
                System.out.println("\nYou have the following ships in your reserves:");
                for (int i = 0; i < 10; i++) {
                    System.out.print(ansi().fg(DEFAULT).a("[").bg((side.shownShips[i])?WHITE:DEFAULT).a(" ")
                    .bg(DEFAULT).a("] "));
                }
                System.out.println(ansi().fg(GREEN).a("\n1-4 - frigates, 5-7 - destroyers," +
                        "\n8-9 - cruisers, 10 - carrier\n"));
                System.out.println(ansi().fg(DEFAULT).a("Enter the number of ship you want do deploy:\n" +
                        "(if you are done, enter 0 to exit this menu)\n" +
                        "(enter -1 to clear selection and -2 to randomize your ships)"));
                String input = in.nextLine();
                try {
                    chosen = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    chosen = -3;
                }
            } while (chosen<-2 || chosen>10);
            switch (chosen){
                case 0:
                    return;
                case -1:
                    Arrays.fill(side.shownShips, false);
                    for (Ship ship:side.fleet) {
                        ship.pos = -100;
                        ship.hor = false;
                    }
                    side.refresh();
                    break;
                case -2:
                    Arrays.fill(side.shownShips, false);
                    for (Ship ship:side.fleet) {
                        ship.pos = -100;
                        ship.hor = false;
                    }
                    side.refresh();
                    randomizeShips(side);
                    side.refresh();
                    break;
                default:
                    int pos = -1;
                    do {
                        System.out.println(ansi().eraseScreen().fg(DEFAULT).a("Your forces:\n").fg(GREEN));
                        side.drawSide();
                        if(pos == -2) System.out.println(ansi().
                                fg(RED).a("Incorrect input format, please, try again"));
                        if(pos == -3) System.out.println(ansi()
                                .fg(RED).a("Ship collisions detected, please, make sure"+
                                "\nyour ships are at least one space apart from each other"));
                        if(pos == -4) System.out.println(ansi()
                                .fg(RED).a("Out of bounds ships detected, please, make sure"+
                                        "\nstay within 10 by 10 grid"));
                        System.out.println(ansi().fg(GREEN).a("\nCurrently deploying: Ship "+chosen
                                +" len "+side.fleet[chosen-1].len));
                        System.out.println(ansi().fg(DEFAULT).a("Enter the position and orientation of the ship:" +
                                "\n(column, row and orientation, for example, G3V will mean" +
                                "\na ship with starting point on G3, which will be positioned vertically," +
                                "\nenter G3H if you want it to be horizontal, enter 0 when you are done)"));
                        String input = in.nextLine().toLowerCase();
                        if(input.equals("0")) {
                            break;
                        }
                        boolean returnFavor = false;
                        if(input.length()==3 || input.length()==4) {
                            if(side.shownShips[chosen-1]) returnFavor = true;
                            side.shownShips[chosen-1] = false;
                            side.refresh();
                            pos = parsePosition(input, side.occupiedPositions, side.fleet[chosen-1]);
                        }
                        else pos = -2;
                        if (returnFavor) {
                            side.shownShips[chosen-1] = true;
                            side.refresh();
                        }
                        if(pos!=-2 && pos!=-3 && pos!=-4) {
                            side.shownShips[chosen-1] = true;
                            side.refresh();
                        }
                    } while (true);
                    break;
            }
        }
    }

    static int parsePosition(String input, HashMap<Integer, Boolean> occupied, Ship ship){
        int pos, column = Character.getNumericValue(input.charAt(0))-10,
                row = Character.getNumericValue(input.charAt(1))-1, t_pos = ship.pos;
        char or = input.charAt(2);
        boolean hor = ship.hor;
        if (input.length() == 4){
            or = input.charAt(3);
            if(input.charAt(1)=='1' && input.charAt(2)=='0') row = 9;
            else return -2;
        }
        if(column>=0 && column <10) pos = column;
        else return -2;
        if(row>=0 && row<10) pos+=row*10;
        else return -2;
        if(or == 'h') pos*=10;
        else if(or == 'v') pos=pos*10+1;
        else return -2;
        ship.pos = pos/10;
        ship.hor = pos%10==0;
        ship.reallignCompartments();
        if((ship.hor && column+ship.len>10) || (!ship.hor && row+ship.len>10)){
            ship.pos = t_pos;
            ship.hor = hor;
            ship.reallignCompartments();
            return -4;
        }
        for (int p:ship.compartments) {
            if(occupied.containsKey(p)){
                ship.pos = t_pos;
                ship.hor = hor;
                ship.reallignCompartments();
                return -3;
            }
        }
        return pos;
    }

    static int parseShot(String input){
        if(input.length()!=2 && input.length()!=3) return -2;
        int pos, column = Character.getNumericValue(input.charAt(0))-10,
                row = Character.getNumericValue(input.charAt(1))-1;
        if(input.length()==3){
            if(input.charAt(1)=='1' && input.charAt(2)=='0') row = 9;
            else return -2;
        }
        if(column>=0 && column <10) pos = column;
        else return -2;
        if(row>=0 && row<10) pos+=row*10;
        else return -2;
        return pos;
    }

    static int chooseNeighbour(int[] field, int pos, Random rand){
        ArrayList<Integer> list = new ArrayList<>();
        if(pos%10!=9 && field[pos+1]==0) list.add(pos+1);
        if(pos%10!=0 && field[pos-1]==0) list.add(pos-1);
        if(pos/10!=9 && field[pos+10]==0) list.add(pos+10);
        if(pos/10!=0 && field[pos-10]==0) list.add(pos-10);
        if(list.isEmpty()) return -1;
        return list.get(rand.nextInt(list.size()));
    }
}
