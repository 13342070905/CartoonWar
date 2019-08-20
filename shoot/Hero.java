package CartoonWar.shoot;
import java.awt.image.BufferedImage;

/** 英雄机 :是飞行物  **/
public class Hero extends FlyingObject{
	private int life;				//命
	private int doubleFire;			//火力值
	private BufferedImage[] images;	//图片数组(两张图片)
	private int index;				//协助图片切换
	
	//构造方法
	public Hero(){

		image = ShootGame.hero0;		//图片
		width = image.getWidth();		//宽
		height = image.getHeight();		//高
		x = 150;	//x:固定的值
		y = 400;	//y:固定的值
		life = 3;	//默认3条命
		doubleFire = 0; //火力值为0,即单倍火力
		images = new BufferedImage[]{ShootGame.hero0,ShootGame.hero1};//两张英雄机的图片
		index = 0;	//协助切换
	}
	
	//重写step()
	public void step(){ //10毫秒走一次
		image = images[index++/20%2];//2控制的是图片数组的下标images.length
//		index++;
//		int a = index/10;
//		int b = a%2;
//		image = images[b];
	}
	
	//英雄机发射子弹
	public Bullet[] shoot(){
		int xStep = this.width/4; //1/4英雄机的宽
		int yStep = 20; //y坐标向上20
		if(doubleFire>0){ //双倍火力
			Bullet[] bs = new Bullet[2];
			bs[0] = new Bullet(this.x+1*xStep,this.y-yStep);
			bs[1] = new Bullet(this.x+3*xStep,this.y-yStep);
			doubleFire-=2; //发射一次双倍火力则火力值减2
			return bs;
		}else{ //单倍火力
			Bullet[] bs = new Bullet[1];
			bs[0] = new Bullet(this.x+2*xStep,this.y-yStep);
			return bs;
		}
	}
	
	//英雄机随着鼠标移动
	public void moveTo(int x,int y){
		this.x = x - this.width/2;  //英雄机的x=鼠标的x-宽/2
		this.y = y - this.height/2; //英雄机的y=鼠标的y-高/2
	}
	
	//重写outOfBounds
	public boolean outOfBounds(){
		return false; //永不越界
	}
	
	//英雄机增命
	public void addLife(){
		life++;
	}
	
	//获取命
	public int getLife(){
		return life;
	}
	
	//英雄机减命
	public void subtractLife(){
		life--;
	}
	//英雄机增火力
	public void addDoubleFire(){
		doubleFire+=40;
	}
	
	//清空火力
	public void clearDoubleFire(){
		doubleFire = 0;
	}
	
	//英雄机撞敌人
	public boolean hit(FlyingObject obj){
		int x1 = obj.x-this.width/2;
		int x2 = obj.x+obj.width+this.width/2;
		int y1 = obj.y-this.height/2;
		int y2 = obj.y+obj.height+this.height/2;
		int x = this.x+this.width/2;
		int y = this.y+this.height/2;
		return x>x1 && x<x2 && y>y1 && y<y2;
	}
}
