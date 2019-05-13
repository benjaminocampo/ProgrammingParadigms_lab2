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
