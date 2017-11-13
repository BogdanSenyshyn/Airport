package model

import java.time._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Trip (tripNo: Option[Long], idComp: Long, plane: String, townFrom: String, townTo: String, timeOut: LocalDateTime, timeIn: LocalDateTime)

class TripTable(tag: Tag) extends Table[Trip](tag, "trip") {
  val tripNo = column[Long]("trip_no", O.PrimaryKey)
  val idComp = column[Long]("id_comp")
  val plane = column[String]("plane")
  val townFrom = column[String]("town_from")
  val townTo = column[String]("town_to")
  val timeOut = column[LocalDateTime]("time_out")
  val timeIn = column[LocalDateTime]("time_in")

  val idCompFk = foreignKey("id_comp_fk", idComp, TableQuery[CompanyTable])(_.idComp)
  def * = (tripNo.?, idComp, plane, townFrom, townTo, timeOut, timeIn) <> (Trip.apply _ tupled, Trip.unapply)
}

object TripTable {
  val table = TableQuery[TripTable]
}

case class TripToCompany (idComp: Option[Long], name: String)
class TripToCompanyTable(tag: Tag) extends Table[(TripToCompany)](tag, "trip_to_company") {
  val idComp = column[Long]("id_comp", O.PrimaryKey)
  val name = column[String]("name")

  val idCompFk = foreignKey("id_comp_fk", idComp, TableQuery[CompanyTable])(_.idComp)
  def * = (idComp.?, name) <> (TripToCompany.apply _ tupled,TripToCompany.unapply)
  // ers - 1 parameter for previous tuple
}

object TripToCompanyTable {
  val table = TableQuery[TripToCompanyTable]
}

class TripRepository(db: Database) {
  def create(trip: Trip): Future[Trip] = db.run(TripTable.table returning TripTable.table += trip)
}
