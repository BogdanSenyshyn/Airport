package model

import java.time._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

case class PassInTrip (tripNo: Option[Int], date: LocalDateTime, idPsg: Int, place: String)

class PassInTripTable(tag: Tag) extends Table[PassInTrip](tag, "pass_in_trip") {
  val tripNo = column[Int]("trip_no")
  val date = column[LocalDateTime]("date")
  val idPsg = column[Int]("id_psg")
  val place = column[String]("place")

  val tripNoFk = foreignKey("trip_no_fk", tripNo, TableQuery[TripTable])(_.tripNo)
  val idPsgFk = foreignKey("id_psg_fk", idPsg, TableQuery[PassengerTable])(_.idPsg)

  def * = (tripNo.?, date, idPsg, place) <> (PassInTrip.apply _ tupled, PassInTrip.unapply)
}

object PassInTripTable {
  val table = TableQuery[PassInTripTable]
}


case class PassInTripToTrip (tripNo: Option[Int], idPsg: Int, date: LocalDateTime)
class PassInTripToTripTable(tag: Tag) extends Table[(PassInTripToTrip)](tag, "pass_in_trip_to_trip") {
  val tripNo = column[Int]("trip_no", O.PrimaryKey)
  val idPsg = column[Int]("idPsg")
  val date = column[LocalDateTime]("date")

  val tripNoFk = foreignKey("trip_no_fk", tripNo, TableQuery[TripTable])(_.tripNo)
  def * = (tripNo.?, idPsg, date) <> (PassInTripToTrip.apply _ tupled,PassInTripToTrip.unapply)
}

object PassInTripToTripTable {
  val table = TableQuery[PassInTripToTripTable]
}

case class PassInTripToPassenger(idPsg: Option[Int], idNo: Int, name: String)
class PassInTripToPassengerTable(tag: Tag) extends Table[(PassInTripToPassenger)](tag, "pass_in_trip_to_passenger") {
  val idPsg = column[Int]("idPsg", O.PrimaryKey)
  val tripNo = column[Int]("trip_no")
  val name = column[String]("name")

  val idPsgFk = foreignKey("id_psg_fk", idPsg, TableQuery[PassengerTable])(_.idPsg)
  def * = (idPsg.?, tripNo, name) <> (PassInTripToPassenger.apply _ tupled,PassInTripToPassenger.unapply)
}

object PassInTripToPassengerTable {
  val table = TableQuery[PassInTripToPassengerTable]
}


class PassInTripRepository(db: Database) {
  def create(passInTrip: PassInTrip): Future[PassInTrip] = db.run(PassInTripTable.table returning PassInTripTable.table
    += passInTrip)
}
