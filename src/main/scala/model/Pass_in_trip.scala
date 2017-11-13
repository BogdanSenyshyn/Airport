package model

import com.github.nscala_time.time.Imports._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

case class PassInTrip (trip_no: Option[Long], date: DateTime, idPsg: Long, place: String)

class PassInTripTable(tag: Tag) extends Table[PassInTrip](tag, "pass_in_trip") {
  val trip_no = column[Long]("trip_no", O.PrimaryKey)
  val date = column[DateTime]("date", O.PrimaryKey)
  val idPsg = column[Long]("id_psg", O.PrimaryKey)
  val place = column[String]("place")

  //val directorFk = foreignKey("director_id_fk", directorId, TableQuery[StaffTable])(_.id)
  def * = (trip_no.?, date, idPsg, place) <> (PassInTrip.apply _ tupled, PassInTrip.unapply)
}

