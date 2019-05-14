package app

import cask._
import models._

object RestfulAPIServer extends MainRoutes  {
  override def host: String = "0.0.0.0"
  override def port: Int = 4000

  @get("/")
  def root(): Response = {
    JSONResponse("Ok")
  }

  @get("/api/locations")
  def locations(): Response = {
    JSONResponse(Location.all.map(location => location.toMap))
  }

  @postJson("/api/locations")
  def locations(name: String, coordX: Int, coordY: Int): Response = {
    if (Location.exists("name", name)) {
      return JSONResponse("Existing location", 409)
    }

    val location = Location(name, coordX, coordY)
    location.save()
    JSONResponse(location.id)
  }

  @get("/api/consumers")
  def consumers(): Response = {
    JSONResponse(Consumer.all.map(c => c.toMap))
  }

  @postJson("api/consumers")
  def consumers(username: String, locationName: String): Response = {
    if (  Consumer.exists("username", username) 
       || Provider.exists("username", username)) {
      return JSONResponse("existing username", 409)
    }
    if (!(Location.exists("name", locationName))) {
      return JSONResponse("non existing location", 404)
    }
    val consumer = Consumer(username, Location.getLocationId(locationName))
    consumer.save()
    JSONResponse(consumer.id)
  }

  @get("/api/providers")
  def providers(locationName: String = ""): Response = {
    if (locationName.isEmpty()) {
      return JSONResponse(Provider.all.map(p => p.toMap))
    }
    if (!(Location.exists("name", locationName))) {
      return JSONResponse("non existing location", 404)
    }
    val locationId = Location.getLocationId(locationName)
    JSONResponse(
      (Provider.all.filter(p => 
        p.toMap("locationId") == locationId)).map(p => p.toMap))
  }

  @postJson("/api/providers")
  def providers(
    username: String, 
    storeName: String, 
    locationName: String, 
    maxDeliveryDistance: Int): Response = {
    if (maxDeliveryDistance < 0) {
      return JSONResponse("negative maxDeliveryDistance", 400)
    }
    if (!(Location.exists("name", locationName))) {
      return JSONResponse("non existing location", 404)
    }
    if (  (Consumer.exists("username", username))
       || (Provider.exists("username", username))
       || (Provider.exists("storeName", storeName))) {
      return JSONResponse("existing username/storeName", 409)
    }
    val provider = Provider(
      username, 
      Location.getLocationId(locationName), 
      storeName, 
      maxDeliveryDistance
    )
    provider.save()
    JSONResponse(provider.id) 
  }

  @post("/api/users/delete/:username")
  def delete(username: String) : Response = {
    if (Consumer.exists("username", username)){
      Consumer.delete(Consumer.getId("username", username))
      return JSONResponse("OK")
    }

    if (Provider.exists("username", username)){
      Provider.delete(Provider.getId("username", username))
      return JSONResponse("OK")
    }

    JSONResponse("Non existing user", 404)
  }

  @get("/api/items/")
  def items(providerUsername: String = ""):Response = {
    if (providerUsername.isEmpty()) {
      return JSONResponse(Item.all.map(p => p.toMap))
    }
    if (!Provider.exists("username", providerUsername)){
      return JSONResponse("non existing provider", 404)
    }
    val items = Item.filter(
        Map("providerId" -> Provider.getId("username", providerUsername)))
    JSONResponse(items.map(item => item.toMap))
  }

  @postJson("/api/items")
  def items(
    name: String,
    description: String,
    price: Float,
    providerUsername: String): Response = {
    if (price < 0){
      return JSONResponse("negative price", 400)
    }
    if (!Provider.exists("username", providerUsername)){
      return JSONResponse("non existing provider", 404)
    }
    val items = Item.filter(
      Map("providerId" -> Provider.getId("username", providerUsername))
    )
    if (items.exists(item => item.name == name)){
      return JSONResponse("existing item for provider", 409)
    }

    val item = Item(
      name, description, price, Provider.getId("username", providerUsername)
    )
    item.save()
    JSONResponse(item.id)  
  }

  @post("/api/items/delete/:id")
  def itemsDelete(id: Int): Response = Item.find(id) match {
    case Some(item) => {
      Item.delete(id)
      JSONResponse("OK")
    }
    case None => JSONResponse("non existing item", 404)
  }

  @get("/api/orders")
  def orders(username: String): Response = {
    if (Provider.exists("username", username)){  
      val orders = Order.filter(
        Map("providerId" -> Provider.getId("username", username)))
      return JSONResponse(orders.map(order => order.toMap))
    }
    if (Consumer.exists("username", username)){
      Order.filter(
        Map("consumerUsername" -> Consumer.getId("username", username)))
      val orders = Order.filter(Map("consumerUsername" -> username))

      return JSONResponse(orders.map(order => order.toMap))
    }
    JSONResponse("non existing user", 404)
  }

  @get("/api/orders/detail/:id")
  def detail(id: Int): Response = Order.find(id) match {
    case Some(order) => JSONResponse(order.toMapDetail)
    case None => JSONResponse("non existing order", 404)
  }

  @postJson("/api/orders")
  def createOrder(
    providerUsername: String,
    consumerUsername: String,
    items: List[ItemsToOrder]): Response = {
    val itemsToOrder = items.map(pedido => (pedido.name, pedido.amount))
    
    if (!itemsToOrder.forall({case (name, amount) => amount >= 0})){
      return JSONResponse("negative amount", 400)
    }
    if (  !Provider.exists("username", providerUsername) 
       || !Consumer.exists("username", consumerUsername)
       || !itemsToOrder.forall({case (name, amount) => Item.exists("name", name)})){
        return JSONResponse("non existing consumer/provider/item for provider", 404)
      }
    val order = Order(providerUsername, consumerUsername, itemsToOrder)
    order.save()
    val consumer = Consumer.filter(Map("username" -> consumerUsername)).head 
    consumer.decrement(order.totalPrice)
    val provider = Provider.filter(Map("username" -> providerUsername)).head 
    provider.increment(order.totalPrice)
    JSONResponse(order.id)
  }

  @post("/api/orders/delete/:id")
  def deleteOrder(id: Int): Response = Order.find(id) match {
    case Some(order) => {
      Order.delete(id)
      JSONResponse("OK")
    }
    case None => JSONResponse("non existing order", 404)
  }

  @post("/api/orders/deliver/:id")
  def deliverOrder(id: Int): Response = Order.find(id) match {
    case Some(order) => {
      if (order.getStatus == "payed"){        
        order.changeStatus("delivered")
        JSONResponse("OK")
      } else {
        JSONResponse("order delivered/finished", 405)
      }
      
    }
    case None => JSONResponse("non existing order", 404)
  }

  override def main(args: Array[String]): Unit = {
    System.err.println("\n " + "=" * 39)
    System.err.println(s"| Server running at http://$host:$port ")

    if (args.length > 0) {
      val databaseDir = args(0)
      Database.loadDatabase(databaseDir)
      System.err.println(s"| Using database directory $databaseDir ")
    } else {
      Database.loadDatabase()  // Use default location
    }
    System.err.println(" " + "=" * 39 + "\n")

    super.main(args)
  }

  initialize()
}
