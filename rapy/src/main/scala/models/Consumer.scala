package models

object Consumer extends ModelCompanion[Consumer] {
  protected def dbTable: DatabaseTable[Consumer] = Database.consumers
    
  def apply(username: String, locationId: Int): Consumer =
      new Consumer(username, locationId)
    
  private[models] def apply(jsonValue: JValue): Consumer = {
    val value = jsonValue.extract[Consumer]
    value._id = (jsonValue \ "id").extract[Int]
    value.save()
    value
  } 

}

class Consumer(
  val username: String, 
  val locationId: Int
) extends Model[Consumer] with User {
  protected def dbTable: DatabaseTable[Consumer] = Consumer.dbTable

  override def toMap: Map[String, Any] = 
    super.toMap + (
      "username" -> username,
      "locationId" -> locationId, 
      "balance" -> balance)
      
  override def toString: String = s"Consumer: $username"
}