package objects;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 */

public class Message extends BaseDbObject {
  public String text;
  public Long compositionTime;
  public Long sentTime;

  @Override
  public String toString() {
    return "Message{" +
        "text='" + text + '\'' +
        ", compositionTime=" + compositionTime +
        ", sentTime=" + sentTime +
        ", lastUpdateTime=" + lastUpdateTime +
        ", firstAddedTime=" + firstAddedTime +
        ", _uuid=" + getUuid() +
        '}';
  }
}
