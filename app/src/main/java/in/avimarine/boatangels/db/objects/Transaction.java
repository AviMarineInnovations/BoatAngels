package in.avimarine.boatangels.db.objects;

import java.util.Date;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 09/11/2018.
 */
public class Transaction extends BaseDbObject {

  private String srcId;
  private String destId;

  public Transaction(String srcId, String destId,
      TransactionType type, int amount, TransactionStatus status) {
    this.srcId = srcId;
    this.destId = destId;
    this.type = type;
    this.amount = amount;
    this.status = status;
    this.firstAddedTime = new Date().getTime();
    this.lastUpdateTime = new Date().getTime();
  }

  private TransactionType type;
  private int amount;
  private TransactionStatus status;


  @Override
  public String toString() {
    return "";
  }

  public String getSrcId() {
    return srcId;
  }

  public void setSrcId(String srcId) {
    this.srcId = srcId;
  }

  public String getDestId() {
    return destId;
  }

  public void setDestId(String destId) {
    this.destId = destId;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public TransactionStatus getStatus() {
    return status;
  }

  public void setStatus(TransactionStatus status) {
    this.status = status;
  }
}
