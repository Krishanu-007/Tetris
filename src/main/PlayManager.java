package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;

import mino.Block;
import mino.Mino;
import mino.Mino_Bar;
import mino.Mino_L1;
import mino.Mino_L2;
import mino.Mino_Sq;
import mino.Mino_T;
import mino.Mino_Z1;
import mino.Mino_Z2;

public class PlayManager {
    final int WIDTH=360;
    final int HEIGHT=600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;
    
    //Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks=new ArrayList<>();

    //Others
    public static int dropInter=60;
    boolean gameOver;

    //Effects
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer>effectY=new ArrayList<>();

    //Scores
    int level=1;
    int lines;
    int scores;
    public PlayManager(){
        left_x=(GamePanel.WIDTH/2)-(WIDTH/2);
        right_x=left_x+WIDTH;
        top_y=50;
        bottom_y=top_y+HEIGHT;
        
        MINO_START_X=left_x+(WIDTH/2)-Block.SIZE;
        MINO_START_Y=top_y+Block.SIZE;
        
        NEXTMINO_X=right_x+175;
        NEXTMINO_Y=top_y+500;
        
        currentMino=randMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino=randMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }


    private Mino randMino(){
        Mino mino=null;
        int i=new Random().nextInt(7);
        switch(i){
            case 0: mino= new Mino_L1();break;
            case 1: mino= new Mino_L2();break;
            case 2: mino= new Mino_Bar();break;
            case 3: mino= new Mino_Sq();break;
            case 4: mino=new Mino_T();break;
            case 5: mino=new Mino_Z1();break;
            case 6: mino=new Mino_Z2();break;
        }
        return mino;
    }
    public void update(){
        if(currentMino.active==false){
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            if(currentMino.b[0].x==MINO_START_X&&currentMino.b[0].y==MINO_START_Y){
                gameOver=true;
            }
            
            currentMino.deactivating=false;

            currentMino=nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino=randMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            //check if lines can be deleted
            checkDelete();
        }
        else{
            currentMino.update();
        }
    }
    private void checkDelete(){
        int x=left_x;
        int y=top_y;
        int blockCount=0;
        int lineCount=0;

        while(x<right_x && y<bottom_y){

            for(int i=0;i<staticBlocks.size();i++){
                if(staticBlocks.get(i).x == x && staticBlocks.get(i).y ==y){
                    //increment blockcount
                    blockCount++;
                }
            }

            x+=Block.SIZE;
            if(x==right_x){
                //if 12 blocks then delete the line
                if(blockCount==12){

                    effectCounterOn=true;
                    effectY.add(y);
                    for(int i=staticBlocks.size()-1;i>-1;i--){
                        if(staticBlocks.get(i).y==y){
                            staticBlocks.remove(i);
                        }
                    }

                    lineCount++;
                    lines++;

                    //Drop speed

                    if(lines%10==0&&dropInter>1){
                        level++;
                        if(dropInter>10){
                            dropInter-=10;
                        }
                        else{
                            dropInter-=1;
                        }
                    }
                    //shift down the above line
                    for(int i=0;i<staticBlocks.size();i++){
                        if(staticBlocks.get(i).y<y){
                            staticBlocks.get(i).y+=Block.SIZE;
                        }
                    }
                }

                blockCount=0;
                x=left_x;
                y+=Block.SIZE;
            }
        }
        if(lineCount>0){
            int singleLineScore=10*level;
            scores+=singleLineScore*lineCount;
        }
    }
    public void draw(Graphics2D g2){
        //Draw Play Area Frame
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-4, top_y-4, WIDTH+8, HEIGHT+8);

        //Draw Next Mino Frame
        int x=right_x+100;
        int y=bottom_y-200;
        g2.drawRect(x, y, 200,200);
        g2.setFont(new Font("Arial",Font.ITALIC,30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT PIECE", x+15, y-10);

        //Draw Score
        g2.drawRect(x, top_y, 250, 300);
        x+=40;
        y=top_y+90;
        g2.drawString("LEVEL: "+level,x,y);y+=70;
        g2.drawString("LINES: "+lines,x,y);y+=70;
        g2.drawString("SCORE: "+scores, x, y);
        //Draw Current Mino
        if(currentMino!=null){
            currentMino.draw(g2);
        }

        //Draw Next Mino
        nextMino.draw(g2);

        //Draw Static Blocks
        for(int i=0;i<staticBlocks.size();i++){
            staticBlocks.get(i).draw(g2);
        }

        //Draw Effect
        if(effectCounterOn){
            effectCounter++;

            g2.setColor(Color.white);
            for(int i=0;i<effectY.size();i++){
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }
            if(effectCounter==5){
                effectCounterOn=false;
                effectCounter=0;
                effectY.clear();
            }
        }
        //Draw Pause
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(gameOver){
            x=left_x+25;
            y=top_y+320;
            g2.drawString("GAME OVER",x,y);
        }
        if(KeyHandler.pausePressed){
            x=left_x+70;
            y=top_y+320;
            g2.drawString("PAUSED", x, y);
        }
        x=35;
        y=top_y+320;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Times New Roman",Font.ITALIC,60));
        g2.drawString("Simple Tetris",x,y);
    }
}