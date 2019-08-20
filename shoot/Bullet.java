package CartoonWar.shoot;

/** �ӵ�: �Ƿ�����  **/
public class Bullet extends FlyingObject{
	private int speed = 3; //�߲��Ĳ���
	
	//���췽��  x:����Ӣ�ۻ�λ�ü���  y:����Ӣ�ۻ�λ�ü���
	public Bullet(int x,int y){
		image = ShootGame.bullet;		//ͼƬ
		width = image.getWidth();		//��
		height = image.getHeight();		//��
		this.x = x;	//�ӵ���x
		this.y = y; //�ӵ���y
	}
	
	//��дstep()
	public void step(){
		y-=speed; //y-(����)
	}
	
	//��дoutOfBounds
	public boolean outOfBounds(){
		return this.y<=-this.height;
	}
}