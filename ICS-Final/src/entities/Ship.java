package entities;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import entities.projectiles.Bullet;
import entities.projectiles.Laser;
import entities.projectiles.Missile;
import entities.turrets.Turret;
import main.Game;
import main.Panel.CustColor;

public class Ship extends Entity {
	// TODO Rewrite weapon code with Turrets - Polymorphous!
	public boolean laserOn = false;
	public double turnSpeed = 8;
	public double spinSpeed = 0;
	public double speed = 0.03;
//	public int bulletCooldown = 0;
//	public int missileCooldown = 0;
//	public int laserCooldown = 30;
//	public int maxBulletCooldown = 30;
//	public double muzzleVelocity = 7;
//	public static final int MAX_MISSILE_COUNTDOWN = 50;
	public static final int MAX_PARTICLE_COOLDOWN = 5;
//	public double bulletAccuracy = 10;
	public double thrustParticleCooldown = MAX_PARTICLE_COOLDOWN;
	public double strafeParticleCooldown = MAX_PARTICLE_COOLDOWN;
	public int MAX_BOOST_DRIVE_COOLDOWN = 100;
	public static int ALPHACUTOFF = 10;
	public int boostDriveCooldown = MAX_BOOST_DRIVE_COOLDOWN;
//	public int caliber = 10;
//	public int missiles = 0;
	public int radius = 0;
	{
		super.transparency = true;
	}
//	public Point[] bulletTurrets;
//	public Point[] missileTurrets;
	public ShipAI ai;
	public ArrayList<Turret> turretList;
	public Ship(double x, double y, int shape, ArrayList<Turret> tList,CustColor c,ShipAI startingAI) {
		super(x, y, 0, 0, shape,c);
		outlineMe = true;
		turretList = tList;
		ai = startingAI;
	}
	public void addAI(ShipAI a){
		ai = a;
	}
	public int getAlpha(){
		if(transparency){
			int a = (int) (ALPHACUTOFF + ((255-ALPHACUTOFF) *((double)health / (double)maxHealth)));
			return (a < 0) ? 0 : a;
		}
		else{
			return 255;
		}
	}
	public void boost(double angle) {
			double dX = (Math.cos(Math.toRadians(realAngle) + angle)) * speed * 60;
			double dY = (Math.sin(Math.toRadians(realAngle) + angle)) * speed * 60;
			this.xSpeed += dX;
			this.ySpeed += dY;
			thrustParticleCooldown = MAX_PARTICLE_COOLDOWN;
			Game.addParticles(ParticleEffects.explode2(this.getX(), this.getY(), 1, 30, 100));
	}
	
	public void move(double angle, boolean arcade) {

		move(angle, 0.5 + (0.5 - (0.5 * (Math.abs(angle) / Math.PI))), 1,arcade);
	}
	public void move(double angle) {

		move(angle, 0.5 + (0.5 - (0.5 * (Math.abs(angle) / Math.PI))), 1,false);
	}
	public void move(double angle, double t, double particleT,boolean arcade) {
		double ang = angle;
		if (!arcade) {
			ang += Math.toRadians(realAngle);
		}
		double dX = (Math.cos(ang)) * speed * t;
		double dY = (Math.sin(ang)) * speed * t;
		this.xSpeed += dX;
		this.ySpeed += dY;
		if (thrustParticleCooldown < 0) {
			fumes(particleT, angle+Math.toRadians(realAngle));
			thrustParticleCooldown = MAX_PARTICLE_COOLDOWN;
		}
	}
	public void strafe(double t){
		double dX = (Math.cos(Math.toRadians(realAngle + 90)))*speed*t;
		double dY = (Math.sin(Math.toRadians(realAngle + 90)))*speed*t;
		this.xSpeed += dX;
		this.ySpeed += dY;
		if(strafeParticleCooldown < 0){
			fumes(t, realAngle + 90);
			strafeParticleCooldown = MAX_PARTICLE_COOLDOWN;
		}
	}
	public void thrust(double t){
		double dX = (Math.cos(Math.toRadians(realAngle)))*speed*t;
		double dY = (Math.sin(Math.toRadians(realAngle)))*speed*t;
		this.xSpeed += dX;
		this.ySpeed += dY;
		if(thrustParticleCooldown < 0){
			fumes(t, realAngle);
			thrustParticleCooldown = MAX_PARTICLE_COOLDOWN;
		}
	}
	/**
	 * Ejects particle effects in the opposite angle to simulate engine
	 * smoke/fire
	 * 
	 * @param t
	 *            particle speed
	 * @param angle
	 *            the ship is accelerating towards
	 */
	public void fumes(double t, double angle) {
		Point2D p = centerPoint;
			Game.addParticles(ParticleEffects.helix((float) (p.getX() + xPos), (float) (p.getY() + yPos), angle, 20, getLife()*80));
	}
	public void update() {
		super.update();
//		bulletCooldown--;
//		missileCooldown--;
		thrustParticleCooldown--;
		strafeParticleCooldown--;
		boostDriveCooldown--;
		xSpeed -= xSpeed / 100D;
		ySpeed -= ySpeed / 100D;
	}
	static int deathParticles = 20;
	public void onDeath() {
		ArrayList<VoxelParticle> a = new ArrayList<VoxelParticle>();
		for (int i = 0; i < deathParticles; i++) {
			int r = rand.nextInt(10);
			Point[] deadParticles;
			deadParticles = new Point[4];
			deadParticles[0] = new Point(0, 0);
			deadParticles[1] = new Point(r, 0);
			deadParticles[2] = new Point(r, r);
			deadParticles[3] = new Point(0, r);
			VoxelParticle p = new VoxelParticle(this.getX(), this.getY(), (rand.nextDouble() - 0.5) * 2,
					(rand.nextDouble() - 0.5) * 2, (rand.nextDouble() - 0.5) * 100, this.color, 100);
			a.add(p);
		}

		Game.addParticles(a);
	}

//	public void shootBullet() {
//		if (bulletCooldown < 0) {
//			bulletCooldown = maxBulletCooldown;
//			Point[] p = getTurrets();
//			for (int i = 0; i < p.length; i++) {
//				double x = p[i].x;
//				double y = p[i].y;
//				double a = (rand.nextDouble() - 0.5) * bulletAccuracy;
//				Entity b;
//				b = new Bullet(x, y, realAngle + a, muzzleVelocity, Structures.BULLET, caliber);
//				if (laserOn) {
//					b = new Laser(x, y, Structures.LASER, 0, 1);
//					b.realAngle = realAngle;
//				}
//				b.team = this.team;
//				Game.entityArray.add(b);
//
//			}
//		}
//	}
//
//	public void shootMissile() {
//		if (missileCooldown < 0) {
//			if (missiles > 0) {
//				missiles--;
//				missileCooldown = MAX_MISSILE_COUNTDOWN;
//				Point[] p = getMissileTurrets();
//				for (int i = 0; i < p.length; i++) {
//					double x = p[i].x;
//					double y = p[i].y;
//					Missile m = new Missile(x, y, realAngle, Structures.BULLET, 1000);
//					m.team = this.team;
//					Game.entityArray.add(m);
//
//				}
//			}
//		}
//	}

	/**
	 * @return all turret points translated and rotated
	 */
//	public Point[] getTurrets() {
//		Point[] tempPoints = new Point[bulletTurrets.length];
//		for (int i = 0; i < bulletTurrets.length; i++) {
//			tempPoints[i] = new Point(0, 0);
//		}
//		Point2D c = centerPoint;
//		AffineTransform.getRotateInstance(Math.toRadians(realAngle), c.getX(), c.getY()).transform(bulletTurrets, 0,
//				tempPoints, 0, bulletTurrets.length);
//		for (int i = 0; i < tempPoints.length; i++) {
//			Point point = tempPoints[i];
//			point.x += this.xPos;
//			point.y += this.yPos;
//		}
//		return tempPoints;
//	}
//
//	public Point[] getMissileTurrets() {
//		Point[] tempPoints = new Point[missileTurrets.length];
//		for (int i = 0; i < missileTurrets.length; i++) {
//			tempPoints[i] = new Point(0, 0);
//		}
//		Point2D c = centerPoint;
//		AffineTransform.getRotateInstance(Math.toRadians(realAngle), c.getX(), c.getY()).transform(missileTurrets, 0,
//				tempPoints, 0, missileTurrets.length);
//		for (int i = 0; i < tempPoints.length; i++) {
//			Point point = tempPoints[i];
//			point.x += this.xPos;
//			point.y += this.yPos;
//		}
//		return tempPoints;
//	}

	public void turn(double throttle) {
		realAngle += turnSpeed*throttle/5f;
	}
	
}
