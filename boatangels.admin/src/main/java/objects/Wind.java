package objects;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 14/01/2018.
 */
public class Wind {

  private Float speed;
  private Float direction;

  public Wind(double speed, double dir) {
    this.speed = (float) speed;
    this.direction = (float) dir;
  }
  public Wind(){

  }


  public void setSpeed(Float speed) {
    this.speed = speed;
  }

  public void setDirection(Float direction) {
    this.direction = direction;
  }

  public Float getSpeed() {
    return speed;
  }

  public Float getDirection() {
    return direction;
  }

  @Override
  public String toString() {
    return "Wind{" +
        "speed=" + speed +
        ", direction=" + direction +
        '}';
  }
}
