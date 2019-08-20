package CartoonWar.shoot;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Font;
/** �������� **/
public class ShootGame extends JPanel{
	public static final int WIDTH = 400;	//���ڿ�
	public static final int HEIGHT = 654;	//���ڸ�
	
	public static BufferedImage background;	//����ͼƬ
	public static BufferedImage start;		//����ͼ
	public static BufferedImage	pause;		//��ͣͼ
	public static BufferedImage gameover;	//��Ϸ����ͼ
	public static BufferedImage airplane;	//�л�ͼ
	public static BufferedImage bee;		//С�۷�ͼ
	public static BufferedImage bullet;		//�ӵ�ͼ
	public static BufferedImage hero0;		//Ӣ�ۻ�0ͼ
	public static BufferedImage hero1;		//Ӣ�ۻ�1ͼ
	
	public static final int START = 0;	//����״̬
	public static final int RUNNING = 1;	//����״̬
	public static final int PAUSE =2;	//��ͣ״̬
	public static final int GAME_OVER =3;	//��Ϸ����״̬
	private int state = START; //��ǰ״̬(Ĭ������״̬)
	
	private Hero hero = new Hero(); //Ӣ�ۻ�����
	private FlyingObject[] flyings = {}; //�����������
	private Bullet[] bullets = {}; //�ӵ��������
	
	
	
	static{  //��ʼ����̬��Դ(ͼƬ)
		try{
			 background = ImageIO.read(ShootGame.class.getResource("background.png"));
			 start = ImageIO.read(ShootGame.class.getResource("start.png"));
			 pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
			 gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
			 airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
			 bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
			 bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
			 hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
			 hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//���ɵ��˶���
	public FlyingObject nextOne(){

		Random rand = new Random();
		int type = rand.nextInt(20);
		if(type == 0){
			return new Bee();
		}else{
			return new Airplane();
		}
	}
	
	//�����볡
	int flyEnterdIndex = 0;
	public void enterAction(){ //10������һ��

		flyEnterdIndex++;
		if(flyEnterdIndex%40==0){
			FlyingObject one = nextOne(); //��ȡ����
			flyings = Arrays.copyOf(flyings, flyings.length+1);//����
			flyings[flyings.length-1] = one; //�����ɵĵ�����ӵ���������һ��Ԫ��
		}
	}
	//��������һ��
	public void stepAction(){
		hero.step();
		for(int i=0;i<flyings.length;i++){
			flyings[i].step();//������һ��
		}
		for(int i=0;i<bullets.length;i++){
			bullets[i].step();//�ӵ���һ��
		}
	}
	
	//�ӵ��볡
	int shootIndex = 0;
	public void shootAction(){ //10������һ��
		shootIndex++; //ÿ10�������1
		if(shootIndex%30==0){  //ÿ300������һ��
			Bullet[] bs = hero.shoot();
			bullets = Arrays.copyOf(bullets, bullets.length+bs.length); //����
			System.arraycopy(bs, 0, bullets, bullets.length-bs.length, bs.length);//�����׷��
		}
	}
	
	//ɾ��Խ��ĵ���
	public void outOfBoundsAction(){
		int index = 0;
		FlyingObject[] flyingLives = new FlyingObject[flyings.length];
		for(int i=0;i<flyings.length;i++){
			FlyingObject f = flyings[i];
			if(!f.outOfBounds()){
				flyingLives[index] = f;
				index++;
			}
		}
		flyings = Arrays.copyOf(flyingLives, index);
		
		index = 0;
		Bullet[] bulletLives = new Bullet[bullets.length];
		for(int i=0;i<bullets.length;i++){
			Bullet b = bullets[i];
			if(!b.outOfBounds()){
				bulletLives[index] = b;
				index++;
			}
		}
		bullets = Arrays.copyOf(bulletLives, index);
	}
	
	//�ӵ�����˵���ײ
	public void bangAction(){
		for(int i=0;i<bullets.length;i++){
			Bullet b = bullets[i];
			bang(b);
		}
	}
	
	int score = 0; //�÷�
	//1���ӵ������е��˵���ײ
	public void bang(Bullet b){
		int index = -1; //�洢��ײ���˵��±�
		for(int i=0;i<flyings.length;i++){
			FlyingObject f = flyings[i];
			if(f.shootBy(b)){
				index = i;
				break;
			}
		}
		if(index != -1){
			FlyingObject one = flyings[index]; //��ȡ��ײ�ĵ���
			if(one instanceof Enemy){
				Enemy e = (Enemy)one;
				score+=e.getScore();
			}
			if(one instanceof Award){
				Award a = (Award)one;
				int type = a.getType();
				switch(type){
				case Award.DOUBLE_FIRE:
					hero.addDoubleFire();
					break;
				case Award.LIFE:
					hero.addLife();
					break;
				}
			}
			//����ײ�������������һ��Ԫ�ؽ���
			FlyingObject t = flyings[index];
			flyings[index] = flyings[flyings.length-1];
			flyings[flyings.length-1] = t;
			//����(ȥ�����һ��Ԫ��,����ײ�ĵ��˶���)
			flyings = Arrays.copyOf(flyings, flyings.length-1);
		}
	}
	
	//
	public void checkGameOverAction(){
		if(isGameOver()){ //��Ϸ����
			state = GAME_OVER;
		}
	}
	
	//�ж���Ϸ�Ƿ����
	public boolean isGameOver(){
		for(int i=0;i<flyings.length;i++){
			FlyingObject f = flyings[i];
			if(hero.hit(f)){
				hero.subtractLife(); //Ӣ�ۻ�����
				hero.clearDoubleFire();	//Ӣ�ۻ���ջ���
				//����ײ�������������һ��Ԫ�ؽ���
				FlyingObject t = flyings[i];
				flyings[i] = flyings[flyings.length-1];
				flyings[flyings.length-1] = t;
				//����(ȥ�����һ��Ԫ��,����ײ�ĵ��˶���)
				flyings = Arrays.copyOf(flyings, flyings.length-1);
			}
		}
		return hero.getLife()<=0;
	}
	
	//���������ִ��
	public void action(){
		//��������������
		MouseAdapter l = new MouseAdapter(){
			public void mouseMoved(MouseEvent e){
				if(state ==RUNNING){
					int x = e.getX(); //��ȡ����X����
					int y = e.getY(); //��ȡ����Y����
					hero.moveTo(x, y);//Ӣ�ۻ���������ƶ�
				}
			}
			//��д������¼�
			public void mouseClicked(MouseEvent e){
				switch(state){
				case START:
					state = RUNNING;
					break;
				case GAME_OVER:
					score = 0;//�����ֳ�(���ݹ���)
					hero = new Hero();
					flyings = new FlyingObject[0];
					bullets = new Bullet[0];
					state = START;
					break;
				}
			}
			
			//��д����Ƴ��¼�
			public void mouseExited(MouseEvent e){
				if(state == RUNNING){
					state = PAUSE;
				}
			}
			
			//��д��������¼�
			public void mouseEntered(MouseEvent e){
				if(state == PAUSE){
					state = RUNNING;
				}
			}
		};
		this.addMouseListener(l);//�����������¼�
		this.addMouseMotionListener(l);//������껬���¼�
		
		Timer timer = new Timer();
		int intervel = 10; //ʱ����(�Ժ���Ϊ��λ)
		timer.schedule(new TimerTask(){
			public void run(){ //��ʱ��������
				if(state ==RUNNING){
					enterAction();
					stepAction();//��������һ��
					shootAction();//�ӵ��볡
					outOfBoundsAction();//ɾ��Խ��ĵ���
					bangAction();//�ӵ��������ײ
					checkGameOverAction();//�����Ϸ�Ƿ����
				}
				repaint(); //�ػ�
			}
		},intervel,intervel);
		
	}
	//��дpaint() g:����
	public void paint(Graphics g){
		g.drawImage(background,0,0,null);
		paintHero(g);
		paintFlyingObjects(g);
		paintBullets(g);
		paintScoreAndLife(g);
		paintState(g);
	}
	//��Ӣ�ۻ�����
	public void paintHero(Graphics g){
		g.drawImage(hero.image, hero.x, hero.y, null);
	}
	//������(�л�+С�۷�)����
	public void paintFlyingObjects(Graphics g){
		for(int i=0;i<flyings.length;i++){ //������������
			FlyingObject f = flyings[i];  //��ȡÿһ������
			g.drawImage(f.image, f.x, f.y, null);
		}
	}
	//���ӵ�����
	public void paintBullets(Graphics g){
		for(int i=0;i<bullets.length;i++){ //�����ӵ�����
			Bullet b = bullets[i]; //��ȡÿһ���ӵ�
			g.drawImage(b.image, b.x, b.y, null);
		}
	}
	
	//���ֺ���
	public void paintScoreAndLife(Graphics g){
		g.setColor(new Color(0xFF0000));//������ɫ
		g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,24));//����������ʽ
		g.drawString("SCORE:"+score, 10, 25);
		g.drawString("LIFE:"+hero.getLife(), 10, 45);
	}
	
	//��״̬
	public void paintState(Graphics g){
		switch(state){
		case START:
			g.drawImage(start, 0, 0, null);
			break;
		case PAUSE:
			g.drawImage(pause, 0, 0, null);
			break;
		case GAME_OVER:
			g.drawImage(gameover, 0, 0, null);
			break;
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Fly");//����
		ShootGame game = new ShootGame();//���
		frame.add(game);//�������ӵ�������
		frame.setSize(WIDTH, HEIGHT);//���ô��ڴ�С
		frame.setAlwaysOnTop(true);//����һֱ��������
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//����Ĭ�Ϲرղ���(�رմ���ʱ�˳�����)
		frame.setLocationRelativeTo(null);//���ô��ھ�����ʾ
		frame.setVisible(true);//���ô��ڿɼ�
		
		game.action();//���������ִ��
	}
}
