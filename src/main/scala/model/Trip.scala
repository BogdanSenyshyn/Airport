package model

import com.github.nscala_time.time.Imports._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Trip (trip_no: Option[Long], idComp: Long, plane: String, townFrom: String, townTo: String, timeOut: DateTime, timeIn: DateTime)

class TripTable(tag: Tag) extends Table[Trip](tag, "trip") {
  val trip_no = column[Long]("trip_no", O.PrimaryKey, O.AutoInc)
  val idComp = column[Long]("id_comp")
  val plane = column[String]("plane")
  val townFrom = column[String]("town_from")
  val townTo = column[String]("town_to")
  val timeOut = column[DateTime]("time_out")
  val timeIn = column[DateTime]("time_out")

  //val directorFk = foreignKey("director_id_fk", directorId, TableQuery[StaffTable])(_.id)
  def * = (trip_no, idComp, plane, townFrom, townTo, timeOut, timeIn) <> (Trip.apply _ tupled, Trip.unapply)
}
