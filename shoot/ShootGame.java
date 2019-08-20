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
/** 主程序类 **/
public class ShootGame extends JPanel{
	public static final int WIDTH = 400;	//窗口宽
	public static final int HEIGHT = 654;	//窗口高
	
	public static BufferedImage background;	//背景图片
	public static BufferedImage start;		//启动图
	public static BufferedImage	pause;		//暂停图
	public static BufferedImage gameover;	//游戏结束图
	public static BufferedImage airplane;	//敌机图
	public static BufferedImage bee;		//小蜜蜂图
	public static BufferedImage bullet;		//子弹图
	public static BufferedImage hero0;		//英雄机0图
	public static BufferedImage hero1;		//英雄机1图
	
	public static final int START = 0;	//启动状态
	public static final int RUNNING = 1;	//运行状态
	public static final int PAUSE =2;	//暂停状态
	public static final int GAME_OVER =3;	//游戏结束状态
	private int state = START; //当前状态(默认启动状态)
	
	private Hero hero = new Hero(); //英雄机对象
	private FlyingObject[] flyings = {}; //敌人数组对象
	private Bullet[] bullets = {}; //子弹数组对象
	
	
	
	static{  //初始化静态资源(图片)
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

	//生成敌人对象
	public FlyingObject nextOne(){

		Random rand = new Random();
		int type = rand.nextInt(20);
		if(type == 0){
			return new Bee();
		}else{
			return new Airplane();
		}
	}
	
	//敌人入场
	int flyEnterdIndex = 0;
	public void enterAction(){ //10毫秒走一次

		flyEnterdIndex++;
		if(flyEnterdIndex%40==0){
			FlyingObject one = nextOne(); //获取敌人
			flyings = Arrays.copyOf(flyings, flyings.length+1);//扩容
			flyings[flyings.length-1] = one; //将生成的敌人添加到数组的最后一个元素
		}
	}
	//飞行物走一次
	public void stepAction(){
		hero.step();
		for(int i=0;i<flyings.length;i++){
			flyings[i].step();//敌人走一笔
		}
		for(int i=0;i<bullets.length;i++){
			bullets[i].step();//子弹走一步
		}
	}
	
	//子弹入场
	int shootIndex = 0;
	public void shootAction(){ //10毫秒走一次
		shootIndex++; //每10个毫秒加1
		if(shootIndex%30==0){  //每300毫秒走一次
			Bullet[] bs = hero.shoot();
			bullets = Arrays.copyOf(bullets, bullets.length+bs.length); //扩容
			System.arraycopy(bs, 0, bullets, bullets.length-bs.length, bs.length);//数组的追加
		}
	}
	
	//删除越界的敌人
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
	
	//子弹与敌人的碰撞
	public void bangAction(){
		for(int i=0;i<bullets.length;i++){
			Bullet b = bullets[i];
			bang(b);
		}
	}
	
	int score = 0; //得分
	//1个子弹与所有敌人的碰撞
	public void bang(Bullet b){
		int index = -1; //存储被撞敌人的下标
		for(int i=0;i<flyings.length;i++){
			FlyingObject f = flyings[i];
			if(f.shootBy(b)){
				index = i;
				break;
			}
		}
		if(index != -1){
			FlyingObject one = flyings[index]; //获取被撞的敌人
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
			//将被撞敌人与数组最后一个元素交换
			FlyingObject t = flyings[index];
			flyings[index] = flyings[flyings.length-1];
			flyings[flyings.length-1] = t;
			//缩容(去掉最后一个元素,即被撞的敌人对象)
			flyings = Arrays.copyOf(flyings, flyings.length-1);
		}
	}
	
	//
	public void checkGameOverAction(){
		if(isGameOver()){ //游戏结束
			state = GAME_OVER;
		}
	}
	
	//判断游戏是否结束
	public boolean isGameOver(){
		for(int i=0;i<flyings.length;i++){
			FlyingObject f = flyings[i];
			if(hero.hit(f)){
				hero.subtractLife(); //英雄机减命
				hero.clearDoubleFire();	//英雄机清空火力
				//将被撞敌人与数组最后一个元素交换
				FlyingObject t = flyings[i];
				flyings[i] = flyings[flyings.length-1];
				flyings[flyings.length-1] = t;
				//缩容(去掉最后一个元素,即被撞的敌人对象)
				flyings = Arrays.copyOf(flyings, flyings.length-1);
			}
		}
		return hero.getLife()<=0;
	}
	
	//启动程序的执行
	public void action(){
		//创建侦听器对象
		MouseAdapter l = new MouseAdapter(){
			public void mouseMoved(MouseEvent e){
				if(state ==RUNNING){
					int x = e.getX(); //获取鼠标的X坐标
					int y = e.getY(); //获取鼠标的Y坐标
					hero.moveTo(x, y);//英雄机随着鼠标移动
				}
			}
			//重写鼠标点击事件
			public void mouseClicked(MouseEvent e){
				switch(state){
				case START:
					state = RUNNING;
					break;
				case GAME_OVER:
					score = 0;//清理现场(数据归零)
					hero = new Hero();
					flyings = new FlyingObject[0];
					bullets = new Bullet[0];
					state = START;
					break;
				}
			}
			
			//重写鼠标移除事件
			public void mouseExited(MouseEvent e){
				if(state == RUNNING){
					state = PAUSE;
				}
			}
			
			//重写鼠标移入事件
			public void mouseEntered(MouseEvent e){
				if(state == PAUSE){
					state = RUNNING;
				}
			}
		};
		this.addMouseListener(l);//处理鼠标操作事件
		this.addMouseMotionListener(l);//处理鼠标滑动事件
		
		Timer timer = new Timer();
		int intervel = 10; //时间间隔(以毫秒为单位)
		timer.schedule(new TimerTask(){
			public void run(){ //定时器做的事
				if(state ==RUNNING){
					enterAction();
					stepAction();//飞行物走一步
					shootAction();//子弹入场
					outOfBoundsAction();//删除越界的敌人
					bangAction();//子弹与敌人相撞
					checkGameOverAction();//检查游戏是否结束
				}
				repaint(); //重画
			}
		},intervel,intervel);
		
	}
	//重写paint() g:画笔
	public void paint(Graphics g){
		g.drawImage(background,0,0,null);
		paintHero(g);
		paintFlyingObjects(g);
		paintBullets(g);
		paintScoreAndLife(g);
		paintState(g);
	}
	//画英雄机对象
	public void paintHero(Graphics g){
		g.drawImage(hero.image, hero.x, hero.y, null);
	}
	//画敌人(敌机+小蜜蜂)对象
	public void paintFlyingObjects(Graphics g){
		for(int i=0;i<flyings.length;i++){ //遍历敌人数组
			FlyingObject f = flyings[i];  //获取每一个敌人
			g.drawImage(f.image, f.x, f.y, null);
		}
	}
	//画子弹对象
	public void paintBullets(Graphics g){
		for(int i=0;i<bullets.length;i++){ //遍历子弹数组
			Bullet b = bullets[i]; //获取每一个子弹
			g.drawImage(b.image, b.x, b.y, null);
		}
	}
	
	//画分和命
	public void paintScoreAndLife(Graphics g){
		g.setColor(new Color(0xFF0000));//设置颜色
		g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,24));//设置字体样式
		g.drawString("SCORE:"+score, 10, 25);
		g.drawString("LIFE:"+hero.getLife(), 10, 45);
	}
	
	//画状态
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
		JFrame frame = new JFrame("Fly");//窗口
		ShootGame game = new ShootGame();//面板
		frame.add(game);//将面板添加到窗口上
		frame.setSize(WIDTH, HEIGHT);//设置窗口大小
		frame.setAlwaysOnTop(true);//设置一直在最上面
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置默认关闭操作(关闭窗口时退出程序)
		frame.setLocationRelativeTo(null);//设置窗口居中显示
		frame.setVisible(true);//设置窗口可见
		
		game.action();//启动程序的执行
	}
}
