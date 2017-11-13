package model

import java.time._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

case class PassInTrip (tripNo: Option[Long], date: LocalDateTime, idPsg: Long, place: String)

class PassInTripTable(tag: Tag) extends Table[PassInTrip](tag, "pass_in_trip") {
  val tripNo = column[Long]("trip_no", O.PrimaryKey)
  val date = column[LocalDateTime]("date", O.PrimaryKey)
  val idPsg = column[Long]("id_psg", O.PrimaryKey)
  val place = column[String]("place")

  val tripNoFk = foreignKey("trip_no_fk", tripNo, TableQuery[TripTable])(_.tripNo)
  val idPsgFk = foreignKey("id_psg_fk", idPsg, TableQuery[PassengerTable])(_.idPsg)

  def * = (tripNo.?, date, idPsg, place) <> (PassInTrip.apply _ tupled, PassInTrip.unapply)
}

object PassInTripTable {
  val table = TableQuery[PassInTripTable]
}

class PassInTripRepository(db: Database) {
  def create(passInTrip: PassInTrip): Future[PassInTrip] = db.run(PassInTripTable.table returning PassInTripTable.table
    += passInTrip)
}
