package models


trait Mappable {

  def toMap: Map[String, Object]

}