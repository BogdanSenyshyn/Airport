package model
import java.time._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

case class Passenger (idPsg: Option[Long], name: String)

class PassengerTable(tag: Tag) extends Table[Passenger](tag, "passenger") {
  val idPsg = column[Long]("id_psg", O.PrimaryKey)
  val name = column[String]("name")

  //val directorFk = foreignKey("director_id_fk", directorId, TableQuery[StaffTable])(_.id)
  def * = (idPsg.?, name) <> (Passenger.apply _ tupled, Passenger.unapply)
}

object PassengerTable {
  val table = TableQuery[PassengerTable]
}

class PassengerRepository(db: Database) {
  def create(passenger: Passenger): Future[Passenger] = db.run(PassengerTable.table returning PassengerTable.table
    += passenger)
}