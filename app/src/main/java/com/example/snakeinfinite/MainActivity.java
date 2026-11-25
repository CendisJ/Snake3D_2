package com.example.snakeinfinite;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.graphics.*;
import java.util.*;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle b) { super.onCreate(b); getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512 | 4096); setContentView(new GameView()); }

    class GameView extends View {
        Paint p = new Paint(3); Random random = new Random();
        ArrayDeque<PointF> trail = new ArrayDeque<>();
        ArrayList<PointF> foods = new ArrayList<>();
        float x=0,y=0, angle=0, targetAngle=0, speed=175, joystickX=0,joystickY=0;
        float worldX=0,worldY=0; long last=0; int score=0; boolean paused=false, gameOver=false, dragging=false;
        float cx,cy,scale=1; final float bodySpacing=13f; int green=Color.rgb(72,220,105);
        GameView(){ super(MainActivity.this); setLayerType(View.LAYER_TYPE_SOFTWARE,null); for(int i=0;i<5;i++) spawnFood(); for(int i=0;i<100;i++) trail.add(new PointF(-i*bodySpacing,0)); }
        void spawnFood(){ foods.add(new PointF(worldX+(random.nextFloat()-.5f)*1300, worldY+(random.nextFloat()-.5f)*900)); }
        @Override protected void onDraw(Canvas c){ super.onDraw(c); float w=getWidth(),h=getHeight(); cx=w/2;cy=h/2; scale=Math.min(w/900f,h/520f); c.drawColor(Color.rgb(13,25,31)); drawWorld(c,w,h); drawFoods(c); drawSnake(c); drawHud(c,w,h); if(paused||gameOver) drawOverlay(c,w,h); if(last==0)last=System.nanoTime(); long now=System.nanoTime(); float dt=Math.min(.04f,(now-last)/1e9f);last=now; if(!paused&&!gameOver) update(dt); postInvalidateOnAnimation(); }
        void update(float dt){ float diff=(targetAngle-angle+ (float)Math.PI*3)%(float)(Math.PI*2)-(float)Math.PI; angle+=Math.max(-3.2f*dt,Math.min(3.2f*dt,diff)); float dx=(float)Math.cos(angle)*speed*dt,dy=(float)Math.sin(angle)*speed*dt; worldX+=dx;worldY+=dy; trail.addFirst(new PointF(worldX,worldY)); while(trail.size()>500)trail.removeLast(); if(trail.size()>1){ Iterator<PointF> it=trail.iterator(); PointF head=it.next(); for(PointF f:foods){ if(Math.hypot(f.x-worldX,f.y-worldY)<27){ score++; f.set(worldX+(random.nextFloat()-.5f)*1000,worldY+(random.nextFloat()-.5f)*800); } } } }
        float sx(float wx){return cx+(wx-worldX)*scale;} float sy(float wy){return cy+(wy-worldY)*scale;}
        void drawWorld(Canvas c,float w,float h){ p.setColor(Color.rgb(34,54,58));p.setStrokeWidth(1); float step=70*scale; float ox=(((-worldX*scale)%step)+step)%step, oy=(((-worldY*scale)%step)+step)%step; for(float a=ox;a<w;a+=step)c.drawLine(a,0,a,h,p);for(float a=oy;a<h;a+=step)c.drawLine(0,a,w,a,p); }
        void drawFoods(Canvas c){ for(PointF f:foods){float a=sx(f.x),b=sy(f.y); if(a<-40||a>getWidth()+40||b<-40||b>getHeight()+40)continue; p.setShadowLayer(12,0,2,Color.rgb(255,185,55));p.setColor(Color.rgb(255,183,45));c.drawCircle(a,b,11*scale,p);p.clearShadowLayer();p.setColor(Color.WHITE);c.drawCircle(a-3*scale,b-3*scale,3*scale,p); } }
        void drawSnake(Canvas c){ ArrayList<PointF> pts=new ArrayList<>(trail); p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND); for(int i=pts.size()-1;i>0;i--){PointF a=pts.get(i),b=pts.get(i-1);p.setColor(Color.rgb(28,125,62));p.setStrokeWidth(27*scale);c.drawLine(sx(a.x),sy(a.y),sx(b.x),sy(b.y),p);p.setColor(green);p.setStrokeWidth(21*scale);c.drawLine(sx(a.x),sy(a.y),sx(b.x),sy(b.y),p);} p.setColor(Color.rgb(35,160,75));c.drawCircle(cx,cy,15*scale,p);p.setColor(green);c.drawCircle(cx,cy,12*scale,p);float ex=cx+(float)Math.cos(angle-.55f)*6*scale,ey=cy+(float)Math.sin(angle-.55f)*6*scale;p.setColor(Color.WHITE);c.drawCircle(ex,ey,3.3f*scale,p);c.drawCircle(cx+(float)Math.cos(angle+.55f)*6*scale,cy+(float)Math.sin(angle+.55f)*6*scale,3.3f*scale,p); }
        void text(Canvas c,String s,float x,float y,float size,int color){p.setColor(color);p.setTextSize(size);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));c.drawText(s,x,y,p);}
        void drawHud(Canvas c,float w,float h){text(c,"SNAKE INFINITE",24,42,22,Color.WHITE);text(c,"SKÓRE: "+score,24,76,18,Color.rgb(125,245,150));p.setColor(Color.argb(120,255,255,255));c.drawRoundRect(w-145,18,w-24,62,18,18,p);text(c,paused?"POKRAČOVAT":"PAUZA",w-130,47,15,Color.WHITE);p.setColor(Color.argb(45,255,255,255));c.drawCircle(85,h-85,57,p);p.setColor(Color.argb(110,255,255,255));c.drawCircle(85+joystickX*35,h-85+joystickY*35,21,p);text(c,"DRŽ A TAHEJ",24,h-12,12,Color.LTGRAY); }
        void drawOverlay(Canvas c,float w,float h){p.setColor(Color.argb(190,5,12,16));c.drawRect(0,0,w,h,p);text(c,gameOver?"KONEC HRY":"PAUZA",w/2-85,h/2-10,32,Color.WHITE);text(c,gameOver?"Klepni pro novou hru":"Klepni pro pokračování",w/2-130,h/2+30,17,Color.LTGRAY);}
        @Override public boolean onTouchEvent(android.view.MotionEvent e){float tx=e.getX(),ty=e.getY(); if(e.getAction()==MotionEvent.ACTION_DOWN){if(gameOver){restart();return true;}if(tx>getWidth()-160&&ty<90){paused=!paused;return true;} if(paused){paused=false;return true;} if(tx<getWidth()*.35f&&ty>getHeight()*.45f){dragging=true;setJoy(tx,ty);return true;} targetAngle=(float)Math.atan2(ty-cy,tx-cx);return true;} if(e.getAction()==MotionEvent.ACTION_MOVE){if(dragging)setJoy(tx,ty);else targetAngle=(float)Math.atan2(ty-cy,tx-cx);return true;}if(e.getAction()==MotionEvent.ACTION_UP){dragging=false;joystickX=joystickY=0;return true;}return true;}
        void setJoy(float tx,float ty){float dx=tx-85,dy=ty-(getHeight()-85),len=(float)Math.hypot(dx,dy);if(len>1){joystickX=dx/Math.max(57,len);joystickY=dy/Math.max(57,len);targetAngle=(float)Math.atan2(dy,dx);}}
        void restart(){score=0;worldX=worldY=angle=targetAngle=0;trail.clear();for(int i=0;i<100;i++)trail.add(new PointF(-i*bodySpacing,0));foods.clear();for(int i=0;i<5;i++)spawnFood();gameOver=false;paused=false;last=0;}
    }
}
