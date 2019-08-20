package CartoonWar.shoot;
import java.awt.image.BufferedImage;

/** Ӣ�ۻ� :�Ƿ�����  **/
public class Hero extends FlyingObject{
	private int life;				//��
	private int doubleFire;			//����ֵ
	private BufferedImage[] images;	//ͼƬ����(����ͼƬ)
	private int index;				//Э��ͼƬ�л�
	
	//���췽��
	public Hero(){

		image = ShootGame.hero0;		//ͼƬ
		width = image.getWidth();		//��
		height = image.getHeight();		//��
		x = 150;	//x:�̶���ֵ
		y = 400;	//y:�̶���ֵ
		life = 3;	//Ĭ��3����
		doubleFire = 0; //����ֵΪ0,����������
		images = new BufferedImage[]{ShootGame.hero0,ShootGame.hero1};//����Ӣ�ۻ���ͼƬ
		index = 0;	//Э���л�
	}
	
	//��дstep()
	public void step(){ //10������һ��
		image = images[index++/20%2];//2���Ƶ���ͼƬ������±�images.length
//		index++;
//		int a = index/10;
//		int b = a%2;
//		image = images[b];
	}
	
	//Ӣ�ۻ������ӵ�
	public Bullet[] shoot(){
		int xStep = this.width/4; //1/4Ӣ�ۻ��Ŀ�
		int yStep = 20; //y��������20
		if(doubleFire>0){ //˫������
			Bullet[] bs = new Bullet[2];
			bs[0] = new Bullet(this.x+1*xStep,this.y-yStep);
			bs[1] = new Bullet(this.x+3*xStep,this.y-yStep);
			doubleFire-=2; //����һ��˫�����������ֵ��2
			return bs;
		}else{ //��������
			Bullet[] bs = new Bullet[1];
			bs[0] = new Bullet(this.x+2*xStep,this.y-yStep);
			return bs;
		}
	}
	
	//Ӣ�ۻ���������ƶ�
	public void moveTo(int x,int y){
		this.x = x - this.width/2;  //Ӣ�ۻ���x=����x-��/2
		this.y = y - this.height/2; //Ӣ�ۻ���y=����y-��/2
	}
	
	//��дoutOfBounds
	public boolean outOfBounds(){
		return false; //����Խ��
	}
	
	//Ӣ�ۻ�����
	public void addLife(){
		life++;
	}
	
	//��ȡ��
	public int getLife(){
		return life;
	}
	
	//Ӣ�ۻ�����
	public void subtractLife(){
		life--;
	}
	//Ӣ�ۻ�������
	public void addDoubleFire(){
		doubleFire+=40;
	}
	
	//��ջ���
	public void clearDoubleFire(){
		doubleFire = 0;
	}
	
	//Ӣ�ۻ�ײ����
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
