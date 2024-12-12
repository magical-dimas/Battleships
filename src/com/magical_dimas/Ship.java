package com.magical_dimas;

public class Ship {
    int pos, len;
    int[] compartments;
    boolean[] damages;
    boolean hor, dead;

    Ship(int p, boolean h, int l){
        this.pos = p;
        this.len = l;
        this.hor = h;
        this.dead = false;
        this.damages = new boolean[l];
        this.compartments = new int[l];
        for (int i = 0; i < l; i++) {
            compartments[i] = p+((h)?1:10)*(l-i-1);
        }
    }

    public int checkLanded(int lanpos){
        for (int i = 0; i < len; i++) {
            if(lanpos==compartments[i]) return i;
        }
        return  -1;
    }
    public boolean damage(int i){
        damages[i] = true;
        dead = true;
        for (int j = 0; j < len; j++) if (!damages[j]) dead = false;
        return dead;
    }

    public void reallignCompartments(){
        for (int i = 0; i < len; i++) {
            compartments[i] = pos+((hor)?1:10)*(len-i-1);
        }
    }
}
