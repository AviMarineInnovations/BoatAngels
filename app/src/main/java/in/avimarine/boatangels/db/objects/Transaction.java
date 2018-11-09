package in.avimarine.boatangels.db.objects;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 09/11/2018.
 */
public class Transaction extends BaseDbObject {

  private String srcUid;
  private String destUid;
  private int amount;
  private TransactionStatus status;

  @Override
  public String toString() {
    return "";
  }

  public String getSrcUid() {
    return srcUid;
  }

  public void setSrcUid(String srcUid) {
    this.srcUid = srcUid;
  }

  public String getDestUid() {
    return destUid;
  }

  public void setDestUid(String destUid) {
    this.destUid = destUid;
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
