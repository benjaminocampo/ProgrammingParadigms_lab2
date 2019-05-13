package models

trait ModelCompanion[M <: Model[M]] {
  protected def dbTable: DatabaseTable[M]

  private[models] def apply(jsonValue: JValue): M

  def all: List[M] = dbTable.instances.values.toList

  def find(id: Int): Option[M] = dbTable.instances.get(id)

  def exists(attr: String, value: Any): Boolean = 
    all.exists(m => m.toMap(attr) == value)

  def delete(id: Int): Unit = dbTable.delete(id)

  def filter(mapOfAttributes: Map[String, Any]): List[M] = 
    all.filter(m => mapOfAttributes.toSet.subsetOf(m.toMap.toSet))

  def getId(attr: String, value: Any): Int = {
    all.find(m => m.toMap(attr) == value) match {
      case Some(x) => x.id
    }
  }
}

trait Model[M <: Model[M]] { self: M =>
  protected var _id: Int = 0

  def id: Int = _id

  protected def dbTable: DatabaseTable[M]

  def toMap: Map[String, Any] = Map("id" -> _id)

  def save(): Unit = {
    if (_id == 0) { _id = dbTable.getNextId }
    dbTable.save(this)
  }
}
