package model
import com.github.nscala_time.time.Imports._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

case class Passenger (idPsg: Option[Long], name: String)

class PassengerTable(tag: Tag) extends Table[Passenger](tag, "passenger") {
  val idComp = column[Long]("id_comp", O.PrimaryKey)
  val name = column[String]("name")

  //val directorFk = foreignKey("director_id_fk", directorId, TableQuery[StaffTable])(_.id)
  def * = (idComp.?, name) <> (Passenger.apply _ tupled, Passenger.unapply)
}