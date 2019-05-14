package models
import upickle.default._
import upickle.default.write
import upickle.default.read
import upickle.default.{macroRW, ReadWriter}

case class ItemsToOrder(name: String, amount: Int)

object ItemsToOrder {
  implicit def uPickleFormatter: ReadWriter[ItemsToOrder] = macroRW 
}

object Order extends ModelCompanion[Order] {
  protected def dbTable: DatabaseTable[Order] = Database.orders

  def apply(
    providerUsername: String, 
    consumerUsername: String, 
    item: List[(String, Int)]): Order = 
      new Order(providerUsername, consumerUsername, item)
    
  private[models] def apply(jsonValue: JValue): Order = {
    val value = jsonValue.extract[Order]
    value._id = (jsonValue \ "id").extract[Int]
    value.save()
    value
  }

  def itemId(itemName: String, providerName: String): Int = {
    Item.filter(
      Map(
        "name" -> itemName, 
        "providerId" -> Provider.getId("username",providerName))
    ).head.id
  }

}

class Order(
  val providerUsername: String, 
  val consumerUsername: String, 
  val item: List[(String, Int)]
) extends Model[Order] {

  protected var theComment: String = ""

  private val consumerId = Consumer.getId("username", consumerUsername)
  
  private val providerId = Provider.getId("username", providerUsername)
  
  private var status:String = "payed"
  
  private val items = item.map({case (iname,cant) => 
      (Order.itemId(iname,providerUsername), 
      iname, 
      Item.getValueById(Order.itemId(iname,providerUsername), "description"), 
      Item.getValueById(Order.itemId(iname,providerUsername), "price"), 
      cant)})
  
  val totalPrice = calcTotalPrice(items)

  protected def dbTable: DatabaseTable[Order] = Order.dbTable

  override def toMap: Map[String, Any] = 
    super.toMap + (
      "consumerId" -> consumerId,
      "consumerUsername" -> consumerUsername,
      "consumerLocation" ->  Consumer.getValueById(consumerId, "locationId"), 
      "providerId" -> providerId, 
      "providerStoreName" -> Provider.getValueById(providerId, "storeName"), 
      "orderTotal" -> totalPrice, 
      "status" -> status)
  
  def toMapDetail: List[Map[String, Any]] = {
    items.map({case (id, name, desc, price, cant) => 
      Map(
        "id" -> id, 
        "name" -> name, 
        "description" -> desc, 
        "price" -> price, 
        "amount" -> cant)
    })
  }

  def changeStatus(status: String): Unit = this.status = status
  
  def getStatus(): String = this.status

  def calcTotalPrice(items: List[(Int, String, Any, Any, Int)]): Float = {
    items.map({ case (id, name, desc, price, cant) => 
      price.asInstanceOf[Float] * cant.asInstanceOf[Float]
    }).sum
  }

  override def toString: String = s"Order: $id"

  def comment(comment: String): Unit =  this.theComment = comment

  def showComment(): Any = this.theComment
}